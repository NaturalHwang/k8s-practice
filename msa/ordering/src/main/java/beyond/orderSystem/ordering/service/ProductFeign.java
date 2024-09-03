package beyond.orderSystem.ordering.service;

import beyond.orderSystem.common.configs.FeignConfig;
import beyond.orderSystem.common.dto.CommonResDto;
import beyond.orderSystem.ordering.dto.ProductUpdateStockDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

// url 설정을 추가하여 service 자원을 검색하도록 설정(url은 k8s의 url을 뜻함 -> eureka의 도메인이 아님)
// 배포하는 것이 아니면 url을 빼고 써야됨.
@FeignClient(name="product-service", url="http://product-service",configuration = FeignConfig.class)
public interface ProductFeign {
    @GetMapping(value = "/product/{id}")
    CommonResDto getProductById(@PathVariable("id") Long id);

    @PutMapping(value = "/product/updatestock")
    void updateProductStock(@RequestBody ProductUpdateStockDto dto);
}
