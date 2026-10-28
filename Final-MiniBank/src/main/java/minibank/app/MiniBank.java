package minibank.app;
import java.nio.file.Path;
import java.util.Scanner;
import minibank.exception.BankException;
import minibank.model.Account;
import minibank.model.BankInfo;
import minibank.model.MenuOption;
import minibank.service.BankService;
import minibank.service.Repository;
import minibank.service.TransactionProcessor;
import minibank.util.AnnotationValidator;
import minibank.util.Validator;

public class MiniBank {
    private static final Path DATA = Path.of("data");
    private static final Path LOGS = Path.of("logs");

    public static void main(String[] args) {
        BankInfo info = new BankInfo("CHARUSAT MiniBank", "Changa Branch");
        Repository<Account> repository = new Repository<>();
        BankService bank = new BankService(repository, DATA, LOGS);
        bank.load();
        try (Scanner scanner = new Scanner(System.in)) {
            System.out.println("==================================");
            System.out.println(info.name() + " | " + info.branch());
            System.out.println("==================================");
            boolean running = true;
            while (running) {
                printMenu();
                System.out.print("Enter choice: ");
                String input = scanner.nextLine().trim();
                int choice;
                try { choice = Integer.parseInt(input); }
                catch (NumberFormatException e) {
                    System.out.println("Please enter a number from 1 to 8.");
                    continue;
                }
                MenuOption option = switch (choice) {
                    case 1 -> MenuOption.OPEN_ACCOUNT;
                    case 2 -> MenuOption.DEPOSIT;
                    case 3 -> MenuOption.WITHDRAW;
                    case 4 -> MenuOption.TRANSFER;
                    case 5 -> MenuOption.STATEMENT;
                    case 6 -> MenuOption.LIST_ACCOUNTS;
                    case 7 -> MenuOption.REPORT;
                    case 8 -> MenuOption.EXIT;
                    default -> null;
                };
                if (option == null) {
                    System.out.println("Invalid choice. Choose 1 to 8.");
                    continue;
                }
                try {
                    switch (option) {
                        case OPEN_ACCOUNT -> openAccount(scanner, bank);
                        case DEPOSIT -> {
                            System.out.print("Account number: ");
                            String number = scanner.nextLine().trim();
                            long amount = readAmount(scanner);
                            bank.deposit(number, amount);
                            System.out.println("Deposit successful.");
                        }
                        case WITHDRAW -> {
                            System.out.print("Account number: ");
                            String number = scanner.nextLine().trim();
                            long amount = readAmount(scanner);
                            bank.withdraw(number, amount);
                            System.out.println("Withdrawal successful.");
                        }
                        case TRANSFER -> {
                            System.out.print("From account: ");
                            String from = scanner.nextLine().trim();
                            System.out.print("To account: ");
                            String to = scanner.nextLine().trim();
                            long amount = readAmount(scanner);
                            bank.transfer(from, to, amount);
                            System.out.println("Transfer successful.");
                        }
                        case STATEMENT -> {
                            System.out.print("Account number: ");
                            System.out.println(bank.statement(scanner.nextLine().trim()));
                        }
                        case LIST_ACCOUNTS -> {
                            System.out.println("Accounts sorted by balance (highest first):");
                            if (bank.listAccounts().isEmpty()) System.out.println("No accounts found.");
                            bank.listAccounts().forEach(System.out::println);
                        }
                        case REPORT -> System.out.println(bank.report());
                        case EXIT -> {
                            bank.save();
                            System.out.println("Accounts saved. Thank you for using MiniBank. Goodbye!");
                            running = false;
                        }
                    }
                } catch (BankException | IllegalArgumentException e) {
                    System.out.println("Operation failed: " + e.getMessage());
                } catch (Exception e) {
                    System.out.println("Could not complete operation: " + e.getMessage());
                } finally {
                    if (running) System.out.println();
                }
            }
        }
    }

    private static void printMenu() {
        System.out.println("\n1. Open account");
        System.out.println("2. Deposit");
        System.out.println("3. Withdraw");
        System.out.println("4. Transfer");
        System.out.println("5. View statement");
        System.out.println("6. List accounts");
        System.out.println("7. End-of-day report");
        System.out.println("8. Exit");
    }

    private static void openAccount(Scanner scanner, BankService bank) throws Exception {
        System.out.print("Customer name: ");
        String name = scanner.nextLine().trim();
        System.out.print("Email: ");
        String email = scanner.nextLine().trim();
        if (!Validator.isValidEmail(email)) throw new IllegalArgumentException("Enter a valid email address.");
        System.out.print("Mobile (10 digits, starts 6-9): ");
        String mobile = scanner.nextLine().trim();
        if (!Validator.isValidMobile(mobile)) throw new IllegalArgumentException("Enter a valid 10-digit mobile number.");
        System.out.print("Account type (S = Savings, C = Current, F = Fixed Deposit): ");
        String type = scanner.nextLine().trim();
        System.out.print("Opening balance (whole rupees): ");
        long balance = readAmountAllowZero(scanner);
        Account account = bank.openAccount(name, type, balance);
        String[] errors = AnnotationValidator.validate(account);
        if (errors.length > 0) {
            System.out.println("Validation warning: " + String.join(", ", errors));
        }
        System.out.println("Account opened successfully: " + account);
        System.out.println("Email/mobile validated. Customer email: " + email + ", mobile: " + mobile);
    }

    private static long readAmount(Scanner scanner) {
        System.out.print("Amount (positive whole rupees): ");
        return parseAmount(scanner.nextLine().trim(), false);
    }
    private static long readAmountAllowZero(Scanner scanner) {
        return parseAmount(scanner.nextLine().trim(), true);
    }
    private static long parseAmount(String value, boolean allowZero) {
        try {
            long amount = Long.parseLong(value);
            if (amount < 0 || (!allowZero && amount == 0)) throw new NumberFormatException();
            return amount;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(allowZero ? "Enter zero or a positive whole number." : "Enter a positive whole number.");
        }
    }
}
