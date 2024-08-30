package beyond.orderSystem.ordering.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderSaveReqDto {
//    private Long memberId; // 토큰에 정보가 들어있으므로 사라져도 무방
//    private List<OrderDetailDto> orderDtos; // 토큰 방식으로 변경하면서 구조 Dto 구성 변경
    private Long productId;
    private Integer productCount;
//    @Data
//    @NoArgsConstructor
//    @AllArgsConstructor
//    @Builder
//    public static class OrderDetailDto{
//        private Long productId;
//        private Integer productCount;
//    }

//    public Ordering toEntity(Member member){
//        return Ordering.builder()
//                .member(member)
//                .orderStatus(OrderStatus.ORDERED)
//                .build();
//    }
}
