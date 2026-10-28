package minibank.service;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import minibank.model.Account;

public class AccountStore {
    private final Map<String, Account> accounts = new HashMap<>();
    private final ArrayList<String> transactionHistory = new ArrayList<>();
    public void add(Account account) { accounts.put(account.getAccountNumber(), account); }
    public Account get(String accountNumber) { return accounts.get(accountNumber); }
    public ArrayList<Account> getAll() { return new ArrayList<>(accounts.values()); }
    public void addHistory(String entry) { transactionHistory.add(entry); }
    public ArrayList<String> getTransactionHistory() { return new ArrayList<>(transactionHistory); }
    public ArrayList<Account> sortedByBalance() {
        ArrayList<Account> result = getAll();
        result.sort(Comparator.comparingLong(Account::getBalance).reversed());
        return result;
    }
}
