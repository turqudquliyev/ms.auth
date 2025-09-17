package az.ingress.auth.model.jwt;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RefreshTokenClaimsDto implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private Date exp;
    private String iss;
    private String userId;
    private Integer count;
}