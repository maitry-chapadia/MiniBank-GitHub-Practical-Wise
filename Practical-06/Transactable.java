package minibank.model;
import minibank.exception.InsufficientFundsException;
import minibank.exception.InvalidAmountException;
public interface Transactable {
    void deposit(long amount) throws InvalidAmountException;
    boolean withdraw(long amount) throws InsufficientFundsException, InvalidAmountException;
}
