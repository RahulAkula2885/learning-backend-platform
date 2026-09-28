package in.rahul.learning.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(
        name = "short_urls",
        indexes = {
                @Index(
                        name = "idx_short_code",
                        columnList = "shortCode",
                        unique = true
                )
        }
)
public class ShortUrl {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(
            name = "shortCode",
            nullable = false,
            unique = true
            //length = 10
    )
    private String shortCode;

    @Column(
            name = "originalUrl",
            nullable = false,
            columnDefinition = "TEXT"
    )
    private String originalUrl;

    @Column(name = "createdTime")
    private Instant createdTime;

    @Column(name = "expiresTime")
    private Instant expiresTime;
}


