package az.ingress.auth;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.cloud.openfeign.EnableFeignClients;

import static org.springframework.boot.SpringApplication.run;

@EnableFeignClients
@SpringBootApplication
@ConfigurationPropertiesScan
public class AuthApplication {

    public static void main(String[] args) {
        run(AuthApplication.class, args);
    }
}