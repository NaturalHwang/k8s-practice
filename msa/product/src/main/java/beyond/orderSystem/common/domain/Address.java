package beyond.orderSystem.common.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Embeddable // 타 엔티티에서 사용 가능한 형태로 만드는 어노테이션
public class Address {
    private String city;
    private String street;
    private String zipcode;
}
