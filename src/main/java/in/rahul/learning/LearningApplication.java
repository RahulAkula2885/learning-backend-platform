package in.rahul.learning;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class LearningApplication {

    static int add(int a, int b) {
        return a + b;
    }

    public static void main(String[] args) {
        int res = add(10, 20);
        System.out.println("res:- " + res);
        SpringApplication.run(LearningApplication.class, args);
    }

}
