package minibank.model;
import java.io.Serializable;
import java.util.Objects;
import minibank.exception.InsufficientFundsException;
import minibank.exception.InvalidAmountException;
import minibank.model.annotation.Id;
import minibank.model.annotation.Positive;
import minibank.model.annotation.MaxLength;

public abstract class Account implements Transactable, InterestBearing, Serializable, Comparable<Account> {
    private static final long serialVersionUID = 1L;
    private static long accountCounter = 0;
    @Id
    private final String accountNumber;
    @MaxLength(80)
    private String ownerName;
    @Positive
    protected long balance;
    private boolean active = true;

    protected Account(String ownerName, long openingBalance) {
        this.accountNumber = String.format("AC%04d", ++accountCounter);
        this.ownerName = ownerName;
        this.balance = openingBalance;
    }

    public String getAccountNumber() { return accountNumber; }
    public String getOwnerName() { return ownerName; }
    public synchronized long getBalance() { return balance; }
    public boolean isActive() { return active; }
    public void close() { active = false; }

    @Override
    public synchronized void deposit(long amount) throws InvalidAmountException {
        if (amount <= 0) throw new InvalidAmountException("Deposit amount must be positive.");
        if (!active) throw new InvalidAmountException("This account is inactive.");
        balance = Math.addExact(balance, amount);
    }

    @Override
    public synchronized boolean withdraw(long amount) throws InsufficientFundsException, InvalidAmountException {
        if (amount <= 0) throw new InvalidAmountException("Withdrawal amount must be positive.");
        if (!active) throw new InvalidAmountException("This account is inactive.");
        if (!canWithdraw(amount)) {
            long shortfall = Math.max(0L, amount - balance);
            throw new InsufficientFundsException("Withdrawal is not allowed for this account. Short by " + shortfall + ".", shortfall);
        }
        balance -= amount;
        return true;
    }

    public abstract boolean canWithdraw(long amount);

    public void transfer(Account to, long amount) throws BankTransferException {
        if (to == null) throw new BankTransferException("Destination account was not found.");
        if (to == this) throw new BankTransferException("Source and destination accounts must be different.");
        Account first = this.compareTo(to) <= 0 ? this : to;
        Account second = first == this ? to : this;
        synchronized (first) {
            synchronized (second) {
                try {
                    this.withdraw(amount);
                    to.deposit(amount);
                } catch (InsufficientFundsException | InvalidAmountException e) {
                    throw new BankTransferException(e.getMessage(), e);
                } catch (ArithmeticException e) {
                    throw new BankTransferException("Amount is too large.", e);
                }
            }
        }
    }

    public static class BankTransferException extends Exception {
        public BankTransferException(String message) { super(message); }
        public BankTransferException(String message, Throwable cause) { super(message, cause); }
    }

    @Override public long balanceForInterest() { return getBalance(); }

    @Override
    public int compareTo(Account other) {
        return this.accountNumber.compareTo(other.accountNumber);
    }

    @Override
    public String toString() {
        return accountNumber + " | " + ownerName + " | " + getBalance() + " | " + (active ? "Active" : "Inactive");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Account account)) return false;
        return Objects.equals(accountNumber, account.accountNumber);
    }

    @Override public int hashCode() { return Objects.hash(accountNumber); }
}
