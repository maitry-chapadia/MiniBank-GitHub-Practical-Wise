package minibank.util;
import minibank.model.Command;
import minibank.model.TransactionType;

public final class CommandParser {
    private CommandParser() {}
    public static Command parse(String line) {
        if (line == null) throw new IllegalArgumentException("Command cannot be empty.");
        String[] parts = line.trim().split("\\s+");
        if (parts.length != 3) throw new IllegalArgumentException("Use: DEPOSIT|WITHDRAW|TRANSFER ACCOUNT_NUMBER AMOUNT");
        TransactionType type;
        long amount;
        try {
            type = TransactionType.valueOf(parts[0].toUpperCase());
            amount = Long.parseLong(parts[2]);
        } catch (RuntimeException e) {
            throw new IllegalArgumentException("Invalid transaction type or amount.");
        }
        if (amount <= 0) throw new IllegalArgumentException("Amount must be positive.");
        return new Command(type, parts[1].toUpperCase(), amount);
    }
}
