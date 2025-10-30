package az.ingress.auth.client.mock;

import az.ingress.auth.client.UserClient;
import az.ingress.auth.logger.ApplicationLogger;
import az.ingress.auth.model.client.UserResponseDto;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("local")
public class MockUserClient implements UserClient {
    private final ApplicationLogger log = ApplicationLogger.getLogger(UserClient.class);

    @Override
    public UserResponseDto getUserDetails(String username) {
        log.info("ActionLog.getUserDetails.start with username: {}", username);
        var responseDto = new UserResponseDto("1", "hey");
        log.info("ActionLog.getUserDetails.end with userId: {}", responseDto.id());
        return responseDto;
    }
}
