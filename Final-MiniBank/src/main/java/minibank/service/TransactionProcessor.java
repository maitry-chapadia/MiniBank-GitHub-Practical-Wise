package minibank.service;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import minibank.model.Account;

public class TransactionProcessor {
    private final ExecutorService executor = Executors.newFixedThreadPool(4);
    private final Map<String, Account> sharedAccounts = new ConcurrentHashMap<>();

    public TransactionProcessor(Iterable<Account> accounts) {
        for (Account account : accounts) sharedAccounts.put(account.getAccountNumber(), account);
    }
    public void submit(Runnable task) { executor.execute(task); }
    public Account getAccount(String accountNumber) { return sharedAccounts.get(accountNumber); }
    public Map<String, Account> getSharedAccounts() { return sharedAccounts; }
    public void stop() {
        executor.shutdown();
        try {
            if (!executor.awaitTermination(30, TimeUnit.SECONDS)) executor.shutdownNow();
        } catch (InterruptedException e) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}
