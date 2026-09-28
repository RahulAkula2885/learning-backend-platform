package in.rahul.learning.repo;

import in.rahul.learning.model.entity.ShortUrl;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IShortUrlRepository extends JpaRepository<ShortUrl, Long> {

    Optional<ShortUrl> findByShortCode(String shortCode);
}
