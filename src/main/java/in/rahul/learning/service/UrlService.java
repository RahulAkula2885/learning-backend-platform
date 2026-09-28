package in.rahul.learning.service;

import in.rahul.learning.commons.BaseResponse;
import in.rahul.learning.exceptions.CustomException;
import in.rahul.learning.model.entity.ShortUrl;
import in.rahul.learning.repo.IShortUrlRepository;
import in.rahul.learning.util.CommonUtils;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import static in.rahul.learning.commons.CommonMessages.SUCCESS;

@Service
@RequiredArgsConstructor
public class UrlService {

    private static final Logger LOG = LoggerFactory.getLogger(UrlService.class);

    private final IShortUrlRepository shortUrlRepository;
    private final StringRedisTemplate redis;


    public ResponseEntity<BaseResponse> shorten(String originalUrl) {

        ShortUrl shortUrl = shortUrlRepository.save(
                ShortUrl.builder()
                        .originalUrl(originalUrl)
                        .shortCode(originalUrl)
                        .createdTime(Instant.now())
                        .expiresTime(Instant.now().plusSeconds(120))
                        .build()
        );

        String shortCode = CommonUtils.generateShortCode(originalUrl);

        shortUrl.setShortCode(shortCode);

        shortUrlRepository.save(shortUrl);

        Map<String, String> map = new HashMap<>();
        map.put("shortCode", shortCode);
        map.put("originalUrl", originalUrl);

        return ResponseEntity.ok(BaseResponse.builder()
                .status(200)
                .message(SUCCESS)
                .data(map)
                .timestamp(Instant.now())
                .build());
    }

    public String getUrl(String code) {

        try {
            String cacheData = redis.opsForValue().get(code);

            if (cacheData != null) {
                return cacheData;
            }
        }catch (RedisConnectionFailureException e) {
            LOG.error("Redis is unavailable. Falling back to database. code={}", code);
        }  catch (Exception e) {
            LOG.error("Redis unavailable while reading key: {}", code, e);
        }


        ShortUrl url = shortUrlRepository.findByShortCode(code)
                .orElseThrow(() ->
                        new CustomException("Short URL not found: " + code));

        try {
            redis.opsForValue().set(
                    code,
                    url.getOriginalUrl(),
                    Duration.ofHours(24)
            );
        } catch (RedisConnectionFailureException e) {
            LOG.error("Redis unavailable. Could not cache code={}", code);
        } catch (Exception e) {
            LOG.error("Redis unavailable while caching key: {}", code, e);
        }


        return url.getOriginalUrl();
    }
}
