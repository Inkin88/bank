package api.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.concurrent.ThreadLocalRandom;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DepositAccountRequest extends BaseModel {
    private long id;
    private double balance;

    public DepositAccountRequest(long id) {
        this.id = id;
        this.balance = Math.round(ThreadLocalRandom.current().nextDouble(0.01, 5000.01) * 100.0) / 100.0;;
    }
}
