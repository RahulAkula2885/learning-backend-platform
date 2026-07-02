package in.rahul.learning;

public class BllomFilter {


    //dependency

//            <dependency>
//    <groupId>com.google.guava</groupId>
//    <artifactId>guava</artifactId>
//    <version>33.0.0-jre</version>
//</dependency>


//    Fix 1: Rebuild on startup
//    @PostConstruct
//    public void load() {
//        userRepository.findAllEmails()
//                .forEach(emailBloomFilter::put);
//    }
//    @PostConstruct
//    public void loadExistingEmails() {
//        List<String> emails = userRepository.findAllEmails();
//
//        emails.forEach(emailBloomFilter::put);
//    }

    //2. Combine with DB (mandatory)
//    if (bloomFilter.mightContain(email)) {
//        if (userRepository.existsByEmail(email)) {
//            throw new RuntimeException("Email already exists");
//        }
//    }


    //-------------------_Config----------------------

//    import com.google.common.hash.BloomFilter;
//import com.google.common.hash.Funnels;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//import java.nio.charset.StandardCharsets;
//
//    @Configuration
//    public class BloomFilterConfig {
//
//        @Bean
//        public BloomFilter<String> emailBloomFilter() {
//            return BloomFilter.create(
//                    Funnels.stringFunnel(StandardCharsets.UTF_8),
//                    1_000_000,   // expected insertions
//                    0.01         // 1% false positive rate
//            );
//        }
//    }
//    -----------------_Service =----------------

//    @Autowired
//    private BloomFilter<String> emailBloomFilter;
//
//    @Autowired
//    private UserRepository userRepository;
//
//    public void createUser(UserRequest request) {
//
//        String email = request.email();
//
//        // Step 1: Bloom filter check
//        if (emailBloomFilter.mightContain(email)) {
//
//            // maybe exists → confirm with DB
//            if (userRepository.existsByEmail(email)) {
//                throw new RuntimeException("Email already exists");
//            }
//        }
//
//        // Step 2: create user
//        User user = new User();
//        user.setEmail(email);
//        user.setName(request.name());
//        userRepository.save(user);
//
//        // Step 3: update Bloom filter
//        emailBloomFilter.put(email);
//    }
}
