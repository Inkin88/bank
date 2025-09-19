package ui.pages;

import lombok.Getter;

@Getter
public enum BankAlert {
    INVALID_AMOUNT_MESSAGE("Please enter a valid amount."),
    OVER_MAX_AMOUNT_MESSAGE("❌ Please deposit less or equal to 5000$."),
    ACCOUNT_NOT_SELECTED_MESSAGE("❌ Please select an account."),
    NO_USER_FOUND("❌ No user found with this account number."),
    INVALID_TRANSFER("❌ Error: Invalid transfer: insufficient funds or invalid accounts"),
    NAME_UPDATED("✅ Name updated successfully!"),
    ENTER_A_VALID_NAME("❌ Please enter a valid name."),
    FILL_ALL_FIELDS("❌ Please fill all fields and confirm.");

    private String message;

    BankAlert(String message) {
        this.message = message;
    }
}
