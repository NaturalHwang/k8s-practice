package beyond.orderSystem.product.controller;

import beyond.orderSystem.common.dto.CommonResDto;
import beyond.orderSystem.product.domain.Product;
import beyond.orderSystem.product.dto.ProductResDto;
import beyond.orderSystem.product.dto.ProductSaveReqDto;
import beyond.orderSystem.product.dto.ProductSearchDto;
import beyond.orderSystem.product.dto.ProductUpdateDto;
import beyond.orderSystem.product.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


@RestController
public class ProductController {
    private final ProductService productService;

    @Autowired
    public ProductController(ProductService productService){
        this.productService = productService;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/product/create")
//    public ResponseEntity<?> createProduct(@RequestBody ProductSaveReqDto dto){
    public ResponseEntity<?> createProduct(ProductSaveReqDto dto){
        Product product = productService.productAwsCreate(dto);
//        Product product = productService.productCreate(dto);
        CommonResDto commonResDto = new CommonResDto(HttpStatus.CREATED, "products are Successly created", product);
        return new ResponseEntity<>(commonResDto, HttpStatus.CREATED);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/product/increase")
    public ResponseEntity<?> increaseProduct(@RequestBody ProductUpdateDto dto){
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
}
