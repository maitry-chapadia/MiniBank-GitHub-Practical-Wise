package minibank.util;
import minibank.model.Account;
public final class StatementFormatter {
    private StatementFormatter() {}
    public static String buildStatement(Account account) {
        StringBuilder out = new StringBuilder();
        out.append("----- MINI BANK STATEMENT -----\n");
        out.append("Account: ").append(account.getAccountNumber()).append('\n');
        out.append("Owner: ").append(account.getOwnerName()).append('\n');
        out.append("Balance: Rs. ").append(account.getBalance()).append('\n');
        out.append("Interest rate: ").append(account.interestRate()).append("%\n");
        out.append("Yearly interest estimate: Rs. ").append(String.format("%.2f", account.yearlyInterest())).append('\n');
        out.append("Status: ").append(account.isActive() ? "Active" : "Inactive").append('\n');
        out.append("-------------------------------");
        return out.toString();
    }
}
