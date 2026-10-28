package minibank.service;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import minibank.exception.AccountNotFoundException;
import minibank.exception.BankException;
import minibank.exception.InsufficientFundsException;
import minibank.exception.InvalidAmountException;
import minibank.model.*;
import minibank.util.*;

public class BankService {
    private final Repository<Account> repository;
    private final Path dataFolder;
    private final Path logsFolder;
    private final Path accountsFile;
    private final Path transactionFile;

    public BankService(Repository<Account> repository, Path dataFolder, Path logsFolder) {
        this.repository = repository;
        this.dataFolder = dataFolder;
        this.logsFolder = logsFolder;
        this.accountsFile = dataFolder.resolve("accounts.dat");
        this.transactionFile = logsFolder.resolve("transactions.log");
    }

    public Account openAccount(String owner, String type, long openingBalance) throws InvalidAmountException {
        if (owner == null || owner.isBlank()) throw new IllegalArgumentException("Owner name is required.");
        if (openingBalance < 0) throw new IllegalArgumentException("Opening balance cannot be negative.");
        Account account = switch (type.toUpperCase()) {
            case "S", "SAVINGS" -> new SavingsAccount(owner, openingBalance, 500);
            case "C", "CURRENT" -> new CurrentAccount(owner, openingBalance, 2000);
            case "F", "FD", "FIXED" -> new FixedDepositAccount(owner, openingBalance);
            default -> throw new IllegalArgumentException("Choose S, C or F.");
        };
        repository.save(account);
        log("OPEN " + account.getAccountNumber() + " " + openingBalance);
        return account;
    }

    public Account findAccount(String accountNumber) throws AccountNotFoundException {
        Account account = repository.findById(accountNumber.toUpperCase());
        if (account == null) throw new AccountNotFoundException("Account " + accountNumber + " was not found.");
        return account;
    }

    public void deposit(String accountNumber, long amount) throws BankException {
        Account account = findAccount(accountNumber);
        account.deposit(amount);
        log("DEPOSIT " + account.getAccountNumber() + " " + amount);
    }

    public void withdraw(String accountNumber, long amount) throws BankException {
        Account account = findAccount(accountNumber);
        account.withdraw(amount);
        log("WITHDRAW " + account.getAccountNumber() + " " + amount);
    }

    public void transfer(String fromNumber, String toNumber, long amount) throws BankException {
        Account from = findAccount(fromNumber);
        Account to = findAccount(toNumber);
        try {
            from.transfer(to, amount);
        } catch (Account.BankTransferException e) {
            throw new BankException(e.getMessage());
        }
        log("TRANSFER " + from.getAccountNumber() + " " + amount + " TO " + to.getAccountNumber());
    }

    public String statement(String accountNumber) throws AccountNotFoundException {
        return StatementFormatter.buildStatement(findAccount(accountNumber));
    }

    public ArrayList<Account> listAccounts() {
        ArrayList<Account> all = repository.findAll();
        all.sort(Comparator.comparingLong(Account::getBalance).reversed());
        return all;
    }

    public String report() throws IOException {
        return ReportGenerator.generate(logsFolder, dataFolder.resolve("end-of-day-report.txt"));
    }

    public void load() {
        try {
            for (Account account : StatePersister.load(accountsFile)) repository.save(account);
        } catch (IOException | ClassNotFoundException | RuntimeException e) {
            System.out.println("Could not load saved accounts: " + e.getMessage());
        }
    }

    public void save() {
        try {
            StatePersister.save(repository.findAll().toArray(new Account[0]), accountsFile);
        } catch (IOException e) {
            System.out.println("Could not save accounts: " + e.getMessage());
        }
    }

    private void log(String line) {
        try { TransactionLog.append(transactionFile, line); }
        catch (IOException e) { System.out.println("Warning: transaction log could not be written."); }
    }

    public Repository<Account> getRepository() { return repository; }
}
