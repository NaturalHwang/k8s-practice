package beyond.orderSystem.ordering.controller;

import beyond.orderSystem.common.dto.CommonErrorDto;
import beyond.orderSystem.common.dto.CommonResDto;
import beyond.orderSystem.member.repository.MemberRepository;
import beyond.orderSystem.ordering.domain.Ordering;
import beyond.orderSystem.ordering.dto.OrderListResDto;
import beyond.orderSystem.ordering.dto.OrderSaveReqDto;
import beyond.orderSystem.ordering.service.OrderingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import java.util.List;

@RestController
public class OrderingController {
    private final OrderingService orderingService;    private final MemberRepository memberRepository;

    @Autowired
    public OrderingController(OrderingService orderingService, MemberRepository memberRepository){
        this.orderingService = orderingService;
        this.memberRepository = memberRepository;
    }

    @PostMapping("/order/create")
    public ResponseEntity<?> orderCreate(@RequestBody List<OrderSaveReqDto> dto){
        Ordering ordering = orderingService.orderCreate(dto);
        CommonResDto commonResDto = new CommonResDto(HttpStatus.CREATED, "정상 완료", ordering.getId());
        return new ResponseEntity<>(commonResDto, HttpStatus.CREATED);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/order/list")
//    public ResponseEntity<?> orderList(Pageable pageable){
//        Page<OrderResDto> dtos = orderingService.orderList(pageable);
//        CommonResDto commonResDto = new CommonResDto(HttpStatus.OK, "정상 완료", dtos);
//        return new ResponseEntity<>(commonResDto, HttpStatus.OK);
//    }
    public ResponseEntity<?> orderList(){
        List<OrderListResDto> dtos = orderingService.orderList();
        CommonResDto commonResDto = new CommonResDto(HttpStatus.OK, "정상 완료", dtos);
        return new ResponseEntity<>(commonResDto, HttpStatus.OK);
    }

//    내 주문만 볼 수 있는 myOrders
//    order/myorders
    @GetMapping("/order/myorders")
    public ResponseEntity<?> myOrders(){
        List<OrderListResDto> dtos = orderingService.myOrderList();
        CommonResDto commonResDto = new CommonResDto(HttpStatus.OK, "조회 완료", dtos);
        return new ResponseEntity<>(commonResDto, HttpStatus.OK);
    }

    @PatchMapping("/order/{orderId}/cancel")
    public ResponseEntity<?> orderCancel(@PathVariable Long orderId){
        try {
            orderingService.orderCancel(orderId);
            CommonResDto commonResDto = new CommonResDto(HttpStatus.OK, "취소 완료", orderId);
            return new ResponseEntity<>(commonResDto, HttpStatus.OK);
        } catch (SecurityException e){
            CommonErrorDto commonErrorDto = new CommonErrorDto(HttpStatus.FORBIDDEN.value(), e.getMessage());
            return new ResponseEntity<>(commonErrorDto, HttpStatus.FORBIDDEN);
        } catch (IllegalArgumentException e){
            CommonErrorDto commonErrorDto = new CommonErrorDto(HttpStatus.BAD_REQUEST.value(), e.getMessage());
            return new ResponseEntity<>(commonErrorDto, HttpStatus.BAD_REQUEST);
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/order/{orderId}/cancelbyadmin")
    public ResponseEntity<?> orderCancelByAdmin(@PathVariable Long orderId){
        try {
            orderingService.orderCancelByAdmin(orderId);
            CommonResDto commonResDto = new CommonResDto(HttpStatus.OK, "취소 완료", orderId);
            return new ResponseEntity<>(commonResDto, HttpStatus.OK);
        } catch (IllegalArgumentException e){
            CommonErrorDto commonErrorDto = new CommonErrorDto(HttpStatus.BAD_REQUEST.value(), e.getMessage());
            return new ResponseEntity<>(commonErrorDto, HttpStatus.BAD_REQUEST);
        } catch (EntityNotFoundException e){
            CommonErrorDto commonErrorDto = new CommonErrorDto(HttpStatus.NOT_FOUND.value(), e.getMessage());
            return new ResponseEntity<>(commonErrorDto, HttpStatus.NOT_FOUND);
        }
    }
}
