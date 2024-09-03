package beyond.orderSystem.product.controller;

import beyond.orderSystem.common.dto.CommonResDto;
import beyond.orderSystem.product.domain.Product;
import beyond.orderSystem.product.dto.ProductResDto;
import beyond.orderSystem.product.dto.ProductSaveReqDto;
import beyond.orderSystem.product.dto.ProductSearchDto;
import beyond.orderSystem.product.dto.ProductUpdateStockDto;
import beyond.orderSystem.product.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

// 해당 어노테이션 사용 시 아래 Spring Bean이 실시간 config 변경 사항의 대상이 됨
//@RefreshScope
@RestController
public class ProductController {
//
//    @Value("${message.hello}")
//    private String helloWorld;

    private final ProductService productService;

    @Autowired
    public ProductController(ProductService productService){
        this.productService = productService;
    }

//    @GetMapping("/product/config/test")
//    public String configTest(){
//        return helloWorld;
//    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/product/create")
//    public ResponseEntity<?> createProduct(@RequestBody ProductSaveReqDto dto){
    public ResponseEntity<?> createProduct(ProductSaveReqDto dto){
        System.out.println(dto);
        Product product = productService.productAwsCreate(dto);
//        Product product = productService.productCreate(dto);
        CommonResDto commonResDto = new CommonResDto(HttpStatus.CREATED, "products are Successly created", product);
        return new ResponseEntity<>(commonResDto, HttpStatus.CREATED);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/product/increase")
    public ResponseEntity<?> increaseProduct(@RequestBody ProductUpdateStockDto dto){
        Product product = productService.productAdd(dto);
        CommonResDto commonResDto = new CommonResDto(HttpStatus.OK, "products are Successly added", product.getStockQuantity());
        return new ResponseEntity<>(commonResDto, HttpStatus.OK);
    }

    @GetMapping("/product/list")
    public ResponseEntity<?> productList(ProductSearchDto searchDto, Pageable pageable){
        Page<ProductResDto> dtos = productService.productList(searchDto, pageable);
        CommonResDto commonResDto = new CommonResDto(HttpStatus.OK, "product list", dtos);
        return new ResponseEntity<>(commonResDto, HttpStatus.OK);
    }

    @GetMapping("/product/{id}")
    public ResponseEntity<?> productDetail(@PathVariable Long id){
        ProductResDto dto = productService.productDetail(id);
        CommonResDto commonResDto = new CommonResDto(HttpStatus.OK, "정상 조회 완료", dto);
        return new ResponseEntity<>(commonResDto, HttpStatus.OK);
    }

    @PutMapping("/product/updatestock")
    public ResponseEntity<?> productStockUpdate(@RequestBody ProductUpdateStockDto dto){
        Product product = productService.productUpdateStock(dto);
        CommonResDto commonResDto = new CommonResDto(HttpStatus.OK,
                "update is successful", product.getId());
        return new ResponseEntity<>(commonResDto, HttpStatus.OK);
    }
}
