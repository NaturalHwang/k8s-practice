package beyond.orderSystem.ordering.dto;

import beyond.orderSystem.ordering.domain.OrderDetail;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderResDto {
    private Long orderId;
    private String memberEmail;
    private String orderStatus;
    private List<OrderDetail> orderDetail;

}