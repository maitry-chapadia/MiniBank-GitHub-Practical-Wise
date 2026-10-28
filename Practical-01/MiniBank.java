package minibank.app;

import java.util.Scanner;
import minibank.model.BankInfo;
import minibank.model.MenuOption;

public class MiniBank {
    public static void main(String[] args) {
        BankInfo info = new BankInfo("MiniBank", "Main Branch");
        Scanner scanner = new Scanner(System.in);
        System.out.println(info);
        boolean running = true;
        while (running) {
            System.out.println("\n1. Open Account\n2. Deposit\n3. Withdraw\n4. Transfer\n5. Exit");
            System.out.print("Choose an option: ");
            String input = scanner.nextLine();
            try {
                MenuOption option = switch (Integer.parseInt(input)) {
                    case 1 -> MenuOption.OPEN_ACCOUNT;
                    case 2 -> MenuOption.DEPOSIT;
                    case 3 -> MenuOption.WITHDRAW;
                    case 4 -> MenuOption.TRANSFER;
                    case 5 -> MenuOption.EXIT;
                    default -> null;
                };
                if (option == null) { System.out.println("Invalid choice."); continue; }
                switch (option) {
                    case OPEN_ACCOUNT -> System.out.println("Open Account — to be implemented in a later lab");
                    case DEPOSIT -> System.out.println("Deposit — to be implemented in a later lab");
                    case WITHDRAW -> System.out.println("Withdraw — to be implemented in a later lab");
                    case TRANSFER -> System.out.println("Transfer — to be implemented in a later lab");
                    case EXIT -> { System.out.println("Goodbye!"); running = false; }
                }
            } catch (NumberFormatException ex) { System.out.println("Enter a valid menu number."); }
        }
        scanner.close();
    }
}
