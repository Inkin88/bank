package ui.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
public class Transfer {

    private String fromACC;
    private String toAcc;
    private String amount;
    private boolean isConfirm;
    private String message;
}
