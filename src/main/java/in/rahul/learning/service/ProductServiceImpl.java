package in.rahul.learning.service;

import in.rahul.learning.commons.BaseResponse;
import in.rahul.learning.model.entity.Product;
import in.rahul.learning.model.request.ProductRequest;
import in.rahul.learning.repo.IProductRepository;
import in.rahul.learning.service.validations.ProductValidation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements IProductService {

    private final IProductRepository productRepository;
    private final ProductValidation productValidation;

    @Override
    @Transactional
    public ResponseEntity<BaseResponse> createProduct(ProductRequest request) {

        productValidation.validateCreateProduct(request);

        Product product = new Product();

        product.setName(request.name());
        product.setDescription(request.description());
        product.setSku(request.sku());
        product.setBrand(request.brand());
        product.setPrice(request.price());
        product.setQuantity(request.quantity());

        product.setActive(true);
        product.setDeleted(false);

        Instant now = Instant.now();
        product.setCreatedTime(now);
        product.setModifiedTime(now);

        productRepository.save(product);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(
                        BaseResponse.builder()
                                .status(HttpStatus.CREATED.value())
                                .message("Product created successfully")
                                .timestamp(now)
                                .build()
                );
    }
}