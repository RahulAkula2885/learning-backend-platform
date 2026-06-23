package in.rahul.learning.repo;

import in.rahul.learning.model.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IProductRepository extends JpaRepository<Product, Long> {

    boolean existsBySku(String sku);
}