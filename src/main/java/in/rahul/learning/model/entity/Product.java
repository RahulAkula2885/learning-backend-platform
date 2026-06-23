package in.rahul.learning.model.entity;


import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;

@Data
@Entity
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Product name
    @Column(nullable = false)
    private String name;

    // Short description
    @Column(length = 1000)
    private String description;

    // Unique product identifier
    @Column(nullable = false, unique = true)
    private String sku;

    // Brand
    @Column(nullable = false)
    private String brand;

    // Selling price
    @Column(nullable = false)
    private BigDecimal price;

    // Cost price
    private BigDecimal costPrice;

    // Available stock
    @Column(nullable = false)
    private Integer quantity;

    // Product image URL
    private String imageUrl;

    // Product status
    private Boolean active = true;

    private Boolean deleted = false;

    private Instant createdTime;

    private Instant modifiedTime;
}
