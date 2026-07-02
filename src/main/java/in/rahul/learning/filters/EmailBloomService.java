package in.rahul.learning.filters;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.connection.ReturnType;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailBloomService {

    private static final Logger LOG = LoggerFactory.getLogger(EmailBloomService.class);

    private final String KEY = "email-bloom-filter";

    private final StringRedisTemplate redisTemplate;

    public boolean mightExist(String email) {

        String script =
                "return redis.call('BF.EXISTS', KEYS[1], ARGV[1])";

        Long result = redisTemplate.execute(
                (RedisCallback<Long>) (connection) -> connection.eval(
                        script.getBytes(),
                        ReturnType.INTEGER,
                        1,
                        KEY.getBytes(),
                        email.getBytes()
                )
        );

        return result != null && result == 1;
    }

    public void add(String email) {

        String script =
                "return redis.call('BF.ADD', KEYS[1], ARGV[1])";

        redisTemplate.execute(
                (RedisCallback<Object>) (connection) -> connection.eval(
                        script.getBytes(),
                        ReturnType.INTEGER,
                        1,
                        KEY.getBytes(),
                        email.getBytes()
                )
        );
    }

//    // check existence
//    public boolean mightExist(String email) {
//        LOG.info("Checking if email address {} exists", email);
//        Object result = redisTemplate.execute((RedisCallback<Object>) connection ->
//                connection.execute("BF.EXISTS", KEY.getBytes(), email.getBytes())
//        );
//
//        return result != null && (Long) result == 1;
//    }
//
//    // add email
//    public void add(String email) {
//        LOG.info("Adding email {} to bloom filter", email);
//        redisTemplate.execute((RedisCallback<Object>) connection ->
//                connection.execute("BF.ADD", KEY.getBytes(), email.getBytes())
//        );
//    }
}
