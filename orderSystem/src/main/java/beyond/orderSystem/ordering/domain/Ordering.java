package beyond.orderSystem.ordering.domain;

import beyond.orderSystem.member.domain.Member;
import beyond.orderSystem.ordering.dto.OrderListResDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class Ordering {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Enumerated(value = EnumType.STRING)
    @Builder.Default
    private OrderStatus orderStatus = OrderStatus.ORDERED;

    @OneToMany(mappedBy = "ordering", cascade = CascadeType.PERSIST) // cascading 작업. cascade = CascadeType.PERSIST 이 부분이 없으면 못함
//    빌더 패턴에서도 ArrayList로 초기화 되도록 하는 설정(빌더에는 List가 초기화가 안되어있음 - 빌더 패턴의 이해 필요)
    @Builder.Default
    private List<OrderDetail> orderDetails = new ArrayList<>();

//    public OrderResDto fromEntity(){
//        OrderResDto orderResDto = OrderResDto.builder()
//                .orderId(this.getId())
//                .memberEmail(this.getMember().getEmail())
//                .orderStatus(this.orderStatus.toString())
//                .orderDetail(this.orderDetails)
//                .build();
//        return orderResDto;
//    }
    public OrderListResDto fromEntity(){
        List<OrderDetail> orderDetailList = this.getOrderDetails();
        List<OrderListResDto.OrderDetailDto> orderDetailDtos = new ArrayList<>();
        for(OrderDetail orderDetail : orderDetailList){
            orderDetailDtos.add(orderDetail.fromEntity());
        }

        OrderListResDto orderListResDto = OrderListResDto.builder()
                .memberEmail(this.getMember().getEmail())
                .orderStatus(this.orderStatus)
                .id(this.id)
                .orderDetailDtos(orderDetailDtos)
                .build();
        return orderListResDto;
    }
    public void orderCancel(){
        List<OrderDetail> details = this.orderDetails;
        for(OrderDetail o : details){
            o.getProduct().increaseQuantity(o.getQuantity());
        }
        this.orderStatus = OrderStatus.CANCELED;
    }
}
