package in.rahul.learning.service.validations;

import in.rahul.learning.exceptions.CustomException;
import in.rahul.learning.model.request.ProductRequest;
import in.rahul.learning.repo.IProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProductValidation {

    private final IProductRepository productRepository;

    public void validateCreateProduct(ProductRequest request) {

        if (productRepository.existsBySku(request.sku())) {
            throw new CustomException("Product SKU already exists");
        }
    }
}