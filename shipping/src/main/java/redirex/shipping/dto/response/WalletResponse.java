package redirex.shipping.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public record WalletResponse (
    String id,
    String userId,
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    LocalDateTime createdAt,
    LocalDateTime updatedAt,
    String currency,
    BigDecimal balance
) {

        }
