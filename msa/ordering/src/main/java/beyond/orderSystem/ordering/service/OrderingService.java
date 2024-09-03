package beyond.orderSystem.ordering.service;

import beyond.orderSystem.common.dto.CommonResDto;
import beyond.orderSystem.common.service.StockInventoryService;
import beyond.orderSystem.ordering.controller.SSEController;
import beyond.orderSystem.ordering.domain.OrderDetail;
import beyond.orderSystem.ordering.domain.OrderStatus;
import beyond.orderSystem.ordering.domain.Ordering;
import beyond.orderSystem.ordering.dto.*;
import beyond.orderSystem.ordering.repository.OrderDetailRepository;
import beyond.orderSystem.ordering.repository.OrderingRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class OrderingService {
    private final OrderingRepository orderingRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final StockInventoryService stockInventoryService;
//    private final StockDecreaseEventHandler stockDecreaseEventHandler;
    private final SSEController sseController;
    private final RestTemplate restTemplate;
    private final ProductFeign productFeign;
//    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Autowired
    public OrderingService(OrderingRepository orderingRepository, OrderDetailRepository orderDetailRepository,
                           StockInventoryService stockInventoryService,
                           /*StockDecreaseEventHandler stockDecreaseEventHandler,*/ SSEController sseController, RestTemplate restTemplate, ProductFeign productFeign /*, KafkaTemplate<String, Object> kafkaTemplate*/){
        this.orderingRepository = orderingRepository;
        this.orderDetailRepository = orderDetailRepository;
        this.stockInventoryService = stockInventoryService;
//        this.stockDecreaseEventHandler = stockDecreaseEventHandler;
        this.sseController = sseController;
        this.restTemplate = restTemplate;
        this.productFeign = productFeign;
//        this.kafkaTemplate = kafkaTemplate;
    }

//    public Ordering orderCreate(List<OrderSaveReqDto> dto){
////        Member member = memberRepository.findById(dto.getMemberId()).orElseThrow(
////                ()-> new EntityNotFoundException("member is not found"));
//        String memberEmail = SecurityContextHolder.getContext().getAuthentication().getName();
//
//
//        Ordering ordering = Ordering.builder()
//                .memberEmail(memberEmail)
//                .build();
//
////        for(OrderSaveReqDto orders : dto){
////
////            int quantity = orders.getProductCount();
////            if(quantity == 0) throw new IllegalArgumentException("상품 수량을 반드시 선택해주세요");
//////            Product API에 요청을 통해 product 객체를 조회해야함(동기처리) -> rest template, feighclient 라이브러리 또는 의존성.
////            if(product.getName().contains("sale")){
//////             redis를 통한 재고관리 및 재고잔량 확인 코드가 들어가야 되는 자리
////                int newQuantity = stockInventoryService.decreaseStock(product.getId(), quantity).intValue();
////                if(newQuantity < 0){
////                    throw new IllegalArgumentException("재고 부족");
////                }
////                stockDecreaseEventHandler.publish(
////                        new StockDecreaseEvent(product.getId(), orders.getProductCount()));
//////                rdb에 재고를 업데이트. rabbitmq를 통해 비동기적으로 이벤트 처리.
////            } else{
////                if(quantity > product.getStockQuantity()){
////                    throw new IllegalArgumentException(product.getName() + "의 재고가 부족합니다. 현재 재고: " + product.getStockQuantity());
////                }
////                product.updateQuantity(quantity); // 변경감지(dirty checking)로 인해 별도의 save 불필요
////            }
////
////            OrderDetail orderDetail = OrderDetail.builder()
////                    .product(product)
////                    .ordering(ordering)
////                    .quantity(quantity)
////                    .build();
////            ordering.getOrderDetails().add(orderDetail);
////        }
//        Ordering savedOrdering = orderingRepository.save(ordering);
//        sseController.publishMessage(savedOrdering.fromEntity(), "admin@test.com");
//        return savedOrdering;
//    }

//    1.	조회: rest template(동기), 변경: rest template(동기)
//    2.	조회: feign client(동기), 변경: feign client(동기)
//    3.	조회: feign client(동기), 변경: kafka(비동기)

    public Ordering orderRestTemplateCreate(List<OrderSaveReqDto> dto){
        String memberEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        Ordering ordering = Ordering.builder()
                .memberEmail(memberEmail)
                .build();
        for(OrderSaveReqDto orders : dto){
            int quantity = orders.getProductCount();
            if(quantity == 0) throw new IllegalArgumentException("상품 수량을 반드시 선택해주세요");
//            Product API에 요청을 통해 product 객체를 조회해야함(동기처리) -> rest template
            String productGetUrl = "http://product-service/product/" + orders.getProductId();
            HttpHeaders httpHeaders = new HttpHeaders();
            String token = SecurityContextHolder.getContext().getAuthentication().getCredentials().toString();
            httpHeaders.set("Authorization", token);
            HttpEntity<String> entity = new HttpEntity<>(httpHeaders);
            ResponseEntity<CommonResDto> productEntity = restTemplate.exchange(
                    productGetUrl, HttpMethod.GET, entity, CommonResDto.class);
            ObjectMapper objectMapper = new ObjectMapper();
            ProductDto productDto = objectMapper.convertValue(productEntity.getBody().getResult(), ProductDto.class);
//            System.out.println(productDto);
            if(productDto.getName().contains("sale")){
//             redis를 통한 재고관리 및 재고잔량 확인 코드가 들어가야 되는 자리
                int newQuantity = stockInventoryService.decreaseStock(productDto.getId(), quantity).intValue();
                if(newQuantity < 0){
                    throw new IllegalArgumentException("재고 부족");
                }
//                stockDecreaseEventHandler.publish(
//                        new StockDecreaseEvent(productDto.getId(), orders.getProductCount()));
//                rdb에 재고를 업데이트. rabbitmq를 통해 비동기적으로 이벤트 처리.
            } else{
                if(quantity > productDto.getStockQuantity()){
                    throw new IllegalArgumentException(productDto.getName() + "의 재고가 부족합니다. 현재 재고: " + productDto.getStockQuantity());
                }
//                productDto.updateQuantity(quantity); // restTemplate을 통한 update 요청
                String updateUrl = "http://product-service/product/updatestock";
                httpHeaders.setContentType(MediaType.APPLICATION_JSON);
                HttpEntity<ProductUpdateStockDto> updateEntity = new HttpEntity<>(
                        new ProductUpdateStockDto(orders.getProductId(),
                                orders.getProductCount()), httpHeaders);
                restTemplate.exchange(updateUrl, HttpMethod.PUT ,updateEntity, Void.class);
            }

            OrderDetail orderDetail = OrderDetail.builder()
                    .productId(productDto.getId())
                    .ordering(ordering)
                    .quantity(quantity)
                    .build();
            ordering.getOrderDetails().add(orderDetail);
        }
        Ordering savedOrdering = orderingRepository.save(ordering);
        sseController.publishMessage(savedOrdering.fromEntity(), "admin@test.com");
        return savedOrdering;
    }

    public Ordering orderFeignClientCreate(List<OrderSaveReqDto> dto){
        String memberEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        Ordering ordering = Ordering.builder()
                .memberEmail(memberEmail)
                .build();
        for(OrderSaveReqDto orders : dto){
            int quantity = orders.getProductCount();
            if(quantity == 0) throw new IllegalArgumentException("상품 수량을 반드시 선택해주세요");
//            ResponseEntity가 기본 응답 값이므로 바로 commonResDto로 매핑
            CommonResDto commonResDto = productFeign.getProductById(orders.getProductId());
            ObjectMapper objectMapper = new ObjectMapper();
            ProductDto productDto = objectMapper.convertValue(commonResDto.getResult(), ProductDto.class);

            if(productDto.getName().contains("sale")){
//             redis를 통한 재고관리 및 재고잔량 확인 코드가 들어가야 되는 자리
                int newQuantity = stockInventoryService.decreaseStock(productDto.getId(), quantity).intValue();
                if(newQuantity < 0){
                    throw new IllegalArgumentException("재고 부족");
                }
//                stockDecreaseEventHandler.publish(
//                        new StockDecreaseEvent(productDto.getId(), orders.getProductCount()));
//                rdb에 재고를 업데이트. rabbitmq를 통해 비동기적으로 이벤트 처리.
            } else{
                if(quantity > productDto.getStockQuantity()){
                    throw new IllegalArgumentException(productDto.getName() + "의 재고가 부족합니다. 현재 재고: " + productDto.getStockQuantity());
                }
                productFeign.updateProductStock(new ProductUpdateStockDto(orders.getProductId(),
                        orders.getProductCount()));
            }

            OrderDetail orderDetail = OrderDetail.builder()
                    .productId(productDto.getId())
                    .ordering(ordering)
                    .quantity(quantity)
                    .build();
            ordering.getOrderDetails().add(orderDetail);
        }
        Ordering savedOrdering = orderingRepository.save(ordering);
        sseController.publishMessage(savedOrdering.fromEntity(), "admin@test.com");
        return savedOrdering;
    }
//
//    public Ordering orderFeignKafkaCreate(List<OrderSaveReqDto> dto){
//        String memberEmail = SecurityContextHolder.getContext().getAuthentication().getName();
//        Ordering ordering = Ordering.builder()
//                .memberEmail(memberEmail)
//                .build();
//        for(OrderSaveReqDto orders : dto){
//            int quantity = orders.getProductCount();
//            if(quantity == 0) throw new IllegalArgumentException("상품 수량을 반드시 선택해주세요");
////            ResponseEntity가 기본 응답 값이므로 바로 commonResDto로 매핑
//            CommonResDto commonResDto = productFeign.getProductById(orders.getProductId());
//            ObjectMapper objectMapper = new ObjectMapper();
//            ProductDto productDto = objectMapper.convertValue(commonResDto.getResult(), ProductDto.class);
//
//            if(productDto.getName().contains("sale")){
////             redis를 통한 재고관리 및 재고잔량 확인 코드가 들어가야 되는 자리
//                int newQuantity = stockInventoryService.decreaseStock(productDto.getId(), quantity).intValue();
//                if(newQuantity < 0){
//                    throw new IllegalArgumentException("재고 부족");
//                }
//                stockDecreaseEventHandler.publish(
//                        new StockDecreaseEvent(productDto.getId(), orders.getProductCount()));
////                rdb에 재고를 업데이트. rabbitmq를 통해 비동기적으로 이벤트 처리.
//            } else{
//                if(quantity > productDto.getStockQuantity()){
//                    throw new IllegalArgumentException(productDto.getName() + "의 재고가 부족합니다. 현재 재고: " + productDto.getStockQuantity());
//                }
//                ProductUpdateStockDto productUpdateStockDto = new ProductUpdateStockDto(orders.getProductId(), orders.getProductCount());
//                kafkaTemplate.send("product-update-topic", productUpdateStockDto);
//            }
//
//            OrderDetail orderDetail = OrderDetail.builder()
//                    .productId(productDto.getId())
//                    .ordering(ordering)
//                    .quantity(quantity)
//                    .build();
//            ordering.getOrderDetails().add(orderDetail);
//        }
//        Ordering savedOrdering = orderingRepository.save(ordering);
//        sseController.publishMessage(savedOrdering.fromEntity(), "admin@test.com");
//        return savedOrdering;
//    }

    public List<OrderListResDto> orderList(){
        List<Ordering> orderings = orderingRepository.findAll();
        List<OrderListResDto> orderListResDtos = new ArrayList<>();
        for(Ordering ordering : orderings){
            orderListResDtos.add(ordering.fromEntity());
        }
        return orderListResDtos;
    }

    public List<OrderListResDto> myOrderList(){
        String memberEmail = SecurityContextHolder.getContext().getAuthentication().getName();

        List<Ordering> orderings = orderingRepository.findAllByMemberEmail(memberEmail);
        List<OrderListResDto> orderListResDtos = new ArrayList<>();

        for(Ordering ordering : orderings){
            orderListResDtos.add(ordering.fromEntity());
        }
        return orderListResDtos;
    }

//    public void orderCancel(Long orderId){
//        Member member = memberRepository.findByEmail(
//                SecurityContextHolder.getContext().getAuthentication().getName()).orElseThrow(
//                ()-> new EntityNotFoundException("존재하지 않는 유저"));
//        Ordering ordering = orderingRepository.findById(orderId).orElseThrow(
//                ()-> new EntityNotFoundException("존재하지 않는 주문"));
//        if(!ordering.getMember().equals(member)) throw new SecurityException("접근 권한이 없습니다");
//        else if(ordering.getOrderStatus().equals(OrderStatus.CANCELED)) throw new IllegalArgumentException ("이미 취소된 주문입니다");
//        else ordering.orderCancel();
//    }

    public void orderCancelByAdmin(Long orderId){
        Ordering ordering = orderingRepository.findById(orderId).orElseThrow(
                ()-> new EntityNotFoundException("존재하지 않는 주문"));
        if(ordering.getOrderStatus().equals(OrderStatus.CANCELED)) throw new IllegalArgumentException ("이미 취소된 주문입니다");
        ordering.orderCancel();
    }


}
