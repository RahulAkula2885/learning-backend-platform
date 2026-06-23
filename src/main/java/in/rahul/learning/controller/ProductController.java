package in.rahul.learning.controller;

import in.rahul.learning.commons.BaseResponse;
import in.rahul.learning.model.request.ProductRequest;
import in.rahul.learning.service.IProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/products")
@Tag(name = "Product Management")
public class ProductController {

    private final IProductService productService;

    @Operation(
            summary = "Create Product",
            description = "Creates a new product"
    )
    @PostMapping
    public ResponseEntity<BaseResponse> createProduct(
            @Valid @RequestBody ProductRequest request) {

        return productService.createProduct(request);
    }
}