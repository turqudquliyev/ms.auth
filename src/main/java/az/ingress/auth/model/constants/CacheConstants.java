package az.ingress.auth.model.constants;

import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public final class CacheConstants {
    public static final String CACHE_PREFIX = "ms-auth:%s";
    public static final Long CACHE_EXPIRE_SECONDS = 1200L;
}