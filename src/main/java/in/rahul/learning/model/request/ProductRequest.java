package in.rahul.learning.model.request;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record ProductRequest(
        @NotBlank(message = "{product.name.required}")
        @Size(max = 255)
        String name,

        @NotBlank(message = "{product.description.required}")
        @Size(max = 1000)
        String description,

        @NotBlank(message = "{product.sku.required}")
        String sku,

        @NotBlank(message = "{product.brand.required}")
        String brand,

        @NotNull(message = "{product.price.required}")
        @DecimalMin(value = "0.01")
        BigDecimal price,

        @NotNull(message = "{product.quantity.required}")
        @Min(0)
        Integer quantity
) {
}