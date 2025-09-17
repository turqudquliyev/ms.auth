package az.ingress.auth.client.decoder;

import az.ingress.auth.exception.CustomFeignException;
import az.ingress.auth.logger.ApplicationLogger;
import com.fasterxml.jackson.databind.JsonNode;
import feign.Response;
import feign.codec.ErrorDecoder;

import static az.ingress.auth.client.decoder.JsonNodeFieldName.MESSAGE;
import static az.ingress.auth.exception.ErrorMessage.CLIENT_ERROR;
import static az.ingress.auth.util.MapperUtil.MAPPER_UTIL;

public class CustomErrorDecoder implements ErrorDecoder {
    private final ApplicationLogger log = ApplicationLogger.getLogger(CustomErrorDecoder.class);

    public Exception decode(String methodKey, Response response) {
        var errorMessage = CLIENT_ERROR.getMessage();
        var statusCode = response.status();

        log.error("ActionLog.decode.error from url {} with statusCode {} ", response.request().url(), statusCode);

        try (var body = response.body().asInputStream()) {
            var jsonNode = MAPPER_UTIL.map(body, JsonNode.class);

            if (jsonNode.has(MESSAGE.getValue()))
                errorMessage = jsonNode.get(MESSAGE.getValue()).asText();

            throw new CustomFeignException(errorMessage, statusCode);
        } catch (Exception ex) {
            log.error("ActionLog.decoder.error ", ex);
            throw new CustomFeignException(errorMessage, statusCode);
        }
    }
}