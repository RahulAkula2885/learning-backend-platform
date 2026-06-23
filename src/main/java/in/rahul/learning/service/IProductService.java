package in.rahul.learning.service;

import in.rahul.learning.commons.BaseResponse;
import in.rahul.learning.model.request.ProductRequest;
import org.springframework.http.ResponseEntity;

public interface IProductService {

    ResponseEntity<BaseResponse> createProduct(ProductRequest request);
}