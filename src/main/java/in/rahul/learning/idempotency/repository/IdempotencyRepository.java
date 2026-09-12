package in.rahul.learning.idempotency.repository;

import in.rahul.learning.util.IdempotencyRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IdempotencyRepository
        extends JpaRepository<IdempotencyRecord, Long> {

    Optional<IdempotencyRecord> findByIdempotencyKey(
            String idempotencyKey
    );

    @Modifying
    @Query(value = """
    INSERT INTO idempotency_record
    (
        idempotency_key,
        request_hash,
        status,
        created_at,
        updated_at
    )
    VALUES
    (
        :key,
        :hash,
        'IN_PROGRESS',
        CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP
    )
    ON CONFLICT (idempotency_key)
    DO NOTHING
    """, nativeQuery = true)
    int createIfAbsent(
            @Param("key") String key,
            @Param("hash") String hash
    );
}

