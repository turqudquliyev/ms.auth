package az.ingress.auth.model.constants;

import static lombok.AccessLevel.PRIVATE;

import lombok.NoArgsConstructor;

@NoArgsConstructor(access = PRIVATE)
public final class CacheConstants {
    public static final String CACHE_PREFIX = "ms.auth:user-id:%s";
    public static final Long CACHE_EXPIRE_SECONDS = 1200L;
}