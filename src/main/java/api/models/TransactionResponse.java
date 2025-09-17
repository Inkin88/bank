package api.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TransactionResponse extends BaseModel {

    private long id;
    private double amount;
    private Type type;
    private String timestamp;
    private long relatedAccountId;
}
