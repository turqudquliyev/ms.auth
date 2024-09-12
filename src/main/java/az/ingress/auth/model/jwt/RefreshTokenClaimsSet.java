package az.ingress.auth.model.jwt;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class RefreshTokenClaimsSet {
    private String userId;
    private Date exp;
    private Integer count;
    private String iss;
}