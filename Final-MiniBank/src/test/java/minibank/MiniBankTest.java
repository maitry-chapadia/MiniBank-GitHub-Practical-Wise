package minibank;
import minibank.exception.BankException;
import minibank.exception.InsufficientFundsException;
import minibank.exception.InvalidAmountException;
import minibank.model.*;
import minibank.service.BankService;
import minibank.service.Repository;
import minibank.util.AnnotationValidator;
import minibank.util.Validator;
import org.junit.jupiter.api.Test;
import java.nio.file.Path;
import static org.junit.jupiter.api.Assertions.*;

class MiniBankTest {
    @Test
    void depositIncreasesBalance() throws Exception {
        SavingsAccount account = new SavingsAccount("Riya", 1000, 0);
        account.deposit(500);
        assertEquals(1500, account.getBalance());
    }

    @Test
    void invalidDepositThrows() {
        SavingsAccount account = new SavingsAccount("Riya", 1000, 0);
        assertThrows(InvalidAmountException.class, () -> account.deposit(0));
    }

    @Test
    void insufficientWithdrawalThrows() {
        SavingsAccount account = new SavingsAccount("Riya", 1000, 0);
        assertThrows(InsufficientFundsException.class, () -> account.withdraw(5000));
    }

    @Test
    void transferUpdatesBothAccounts() throws Exception {
        SavingsAccount a = new SavingsAccount("Riya", 5000, 0);
        SavingsAccount b = new SavingsAccount("Mira", 1000, 0);
        a.transfer(b, 500);
        assertEquals(4500, a.getBalance());
        assertEquals(1500, b.getBalance());
    }

    @Test
    void repositoryFindsAccountById() {
        Repository<Account> repository = new Repository<>();
        Account account = new CurrentAccount("Asha", 3000, 1000);
        repository.save(account);
        assertEquals(account, repository.findById(account.getAccountNumber()));
    }

    @Test
    void validatorsAcceptAndRejectInputs() {
        assertTrue(Validator.isValidMobile("9876543210"));
        assertFalse(Validator.isValidMobile("12345"));
        assertTrue(Validator.isValidEmail("riya@example.com"));
        assertFalse(Validator.isValidEmail("not-an-email"));
    }

    @Test
    void annotationValidatorFindsNonPositiveBalance() {
        Account account = new SavingsAccount("Riya", -100, 0);
        assertTrue(java.util.Arrays.stream(AnnotationValidator.validate(account))
            .anyMatch(message -> message.contains("must be > 0")));
    }

    @Test
    void concurrentDepositsKeepCorrectBalance() throws Exception {
        SavingsAccount account = new SavingsAccount("Riya", 0, 0);
        Thread[] threads = new Thread[10];
        for (int i = 0; i < threads.length; i++) {
            threads[i] = new Thread(() -> {
                for (int j = 0; j < 1000; j++) {
                    try { account.deposit(1); }
                    catch (InvalidAmountException e) { throw new RuntimeException(e); }
                }
            });
            threads[i].start();
        }
        for (Thread thread : threads) thread.join();
        assertEquals(10000, account.getBalance());
    }

    @Test
    void bankServiceOpensAndListsAccounts() throws Exception {
        Repository<Account> repository = new Repository<>();
        BankService service = new BankService(repository, Path.of("target/test-data"), Path.of("target/test-logs"));
        Account account = service.openAccount("Riya", "S", 1000);
        assertEquals(account, service.findAccount(account.getAccountNumber()));
        assertEquals(1, service.listAccounts().size());
    }
}
