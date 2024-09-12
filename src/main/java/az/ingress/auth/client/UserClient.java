package az.ingress.auth.client;

import az.ingress.auth.client.decoder.CustomErrorDecoder;
import az.ingress.auth.model.client.UserResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(
        name = "ms.user",
        path = "/internal",
        url = "${client.urls.ms-user}",
        configuration = CustomErrorDecoder.class
)
public interface UserClient {

    @GetMapping("/v1/users")
    UserResponseDto getUserDetails(@RequestParam String username);
}