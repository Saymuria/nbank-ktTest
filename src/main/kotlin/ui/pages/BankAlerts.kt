package ui.pages

enum class BankAlerts(val message: String) {
    USER_CREATED_SUCCESSFULLY("✅ User created successfully!"),
    USERNAME_MUST_BE_BETWEEN_3_AND_15_CHARACTERS("Username must be between 3 and 15 characters"),
    NEW_ACCOUNT_CREATED("✅ New Account Created! Account Number: "),
    SUCCESSFUL_DEPOSIT("✅ Successfully deposited \$%s to account %s!"),
    INVALID_DEPOSIT_SUM("User cannot make deposit for sum more than 10000.00"),
    ACCOUNT_SELECTION_NEEDED("Please select an account."),
    FILLING_VALID_SUM_NEEDED("Please enter a valid amount."),
    NAME_UPDATED_SUCCESSFULLY("✅ Name updated successfully!"),
    INVALID_NAME("Name should be valid"),
    FILLING_VALID_NAME("❌ Please enter a valid name."),
    SAME_NAME_IS_NOT_UPDATED("⚠️ New name is the same as the current one."),
    SUCCESSFUL_TRANSFER("Successfully transferred \$%s to account %s!"),
    UNSUCCESSFUL_TRANSFER("Transfer is not available fo sum \$%s"),
    TRANSFER_MORE_THAN_DEPOSIT("Error: Invalid transfer: insufficient funds or invalid accounts"),
    INCORRECT_RECIPIENT_NAME("The recipient name does not match the registered name"),
    SUBMIT_EMPTY_TRANSFER_FORM("Please fill all fields and confirm."),
    TRANSFER_AGAIN_SUCCESS("✅ Transfer of $%s successful from Account %s to %s!"),
    NO_MATCHING_USER_FOUND("No matching users found.");

    fun format(vararg args: Any): String = String.format(message, *args)
}
