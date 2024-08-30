package beyond.orderSystem.product.domain;

import beyond.orderSystem.common.domain.BaseTimeEntity;
import beyond.orderSystem.ordering.domain.OrderDetail;
import beyond.orderSystem.product.dto.ProductResDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String category;

    @Column(nullable = false)
    private Integer price;

    @Column(nullable = false)
    private Integer stockQuantity;

    private String imagePath;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
    private List<OrderDetail> details;

    public void updateQuantity(int quantity){
        this.stockQuantity -= quantity;
    }

    public void increaseQuantity(int quantity){
        this.stockQuantity += quantity;
    }

    public void updateImagePath(String imagePath){
        this.imagePath = imagePath;
    }

    public ProductResDto fromEntity(){
        ProductResDto dto = ProductResDto.builder()
                .id(this.id)
                .name(this.name)
                .category(this.category)
                .price(this.price)
                .stockQuantity(this.stockQuantity)
                .imagePath(this.imagePath)
                .build();

        return dto;
    }

}
