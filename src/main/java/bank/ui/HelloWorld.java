package bank.ui;

import java.util.List;

import bank.logic.Bank;
import bank.models.Account;
import bank.models.AccountType;

public class HelloWorld {

	// MENU CONSTANTS

	public static final int SHOW_ALL = 1;
	public static final int SHOW_VIP = 2;
	public static final int DEPOSIT = 3;
	public static final int WITHDRAW = 4;
	public static final int OPEN_ACCOUNT = 5;
	public static final int CLOSE_ACCOUNT = 6;
	public static final int ANNUAL_INTEREST = 7;
	public static final int TRANSFER = 8;
	public static final int VIEW_STATEMENT = 9;
	public static final int CHANGE_PASSWORD = 10;
	public static final int EXIT = 0;

	public static void main(String[] args) {

		System.out.println("DEBUG: Password found? " + (System.getenv("DB_PASSWORD") != null));

		// SETUP

		Bank myBank = new Bank("International Java Bank");

		/*
		 * myBank.addAccounts(new SavingsAccount("Lauren", 1200.0, 2.5));
		 * myBank.addAccounts(new SavingsAccount("Anuska", 5000.0, 3.0));
		 * myBank.addAccounts(new SavingsAccount("Jon", 9800.0, 5));
		 * 
		 */

		// USER's INTERFACE

		boolean running = true;

		while (running) {
			System.out.println("\n--- BANK MAIN MENU ---");
			System.out.println("1. Show All Accounts");
			System.out.println("2. Show VIP Customers");
			System.out.println("3. Deposit Money");
			System.out.println("4. Withdraw Money");
			System.out.println("5. 0pen account");
			System.out.println("6. Close account");
			System.out.println("7. Run Annual Interest (Year-End)");
			System.out.println("8. Transfer Money");
			System.out.println("9. View Account Statement");
			System.out.println("10. Change Password");
			System.out.println("0. Exit");

			int choice = Read.readInt("Choose an option: ");

			switch (choice) {
			case SHOW_ALL:

				myBank.showStatus();
				break;

			case SHOW_VIP:

				List<String> vips = myBank.getVIPCustomers();
				System.out.println("--- ⭐ VIP CUSTOMERS (>100000€) ---");

				if (vips.isEmpty()) {
					System.out.println("No VIPs found. Time to find richer friends!");
				} else {
					vips.forEach(name -> System.out.println("💎 " + name));
				}

				break;

			case DEPOSIT:

				String nameToDeposit = Read.readString("Enter account owner name: ");
				Account accDep = myBank.findAccount(nameToDeposit);

				if (accDep == null) {
					System.out.println("❌ Account '" + nameToDeposit + "' not found.");
					break;
				}

				double depAmount = Read.readDouble("Enter amount to deposit: ");

				if (accDep.deposit(depAmount)) {
					myBank.updateBalanceInDB(accDep);
					myBank.logTransaction(accDep.getId(), "Deposit", depAmount);
					System.out.println(" ✅ Deposit successful!");
				}

				break;

			case WITHDRAW:

				String nameWithdraw = Read.readString("Enter account owner name: ");
				Account accWithdraw = myBank.findAccount(nameWithdraw);

				if (accWithdraw == null) {
					System.out.println("❌ Error: Account '" + nameWithdraw + "' does not exist.");
					break;
				}

				String passInput = Read.readString("Enter password for " + nameWithdraw + ": ");

				if (!accWithdraw.verifyPassword(passInput)) {
					System.out.println("❌ Access Denied: Incorrect Password!");
					break;
				}

				double amountWithdraw = Read.readDouble("Enter amount to withdraw: ");

				if (accWithdraw.withdraw(amountWithdraw)) {
					myBank.updateBalanceInDB(accWithdraw);
					myBank.logTransaction(accWithdraw.getId(), "Withdrawal", -amountWithdraw);
					System.out.println("✅ Please take your cash. New balance: " + accWithdraw.getBalance() + "€");
				} else {
					System.out.println("⚠️ Transaction failed. Check your balance and try again.");
				}

				break;

			case OPEN_ACCOUNT:

				String nameAdd = Read.readString("Enter new account name: ");
				double initialBalance = Read.readDouble("Enter initial deposit: ");

				String newPassword = "";
				boolean passwordValid = false;

				while (!passwordValid) {
					newPassword = Read.readString("Create a secure password: ");

					if (!isPasswordStrong(newPassword)) {

						continue;
					}

					String confirmPassword = Read.readString("Repeat password: ");

					if (newPassword.equals(confirmPassword)) {
						passwordValid = true;
					} else {
						System.out.println("❌ Error: Passwords do not match. Please try again.");
					}
				}

				System.out.println("Choose account type:");
				System.out.println("1. Checking | 2. Savings | 3. Fixed-Term");

				int typeChoice = Read.readInt("Select: ");

				AccountType type;

				if (typeChoice == 1) {
					type = AccountType.CHECKING;
				} else if (typeChoice == 2) {
					type = AccountType.SAVINGS;
				} else {
					type = AccountType.FIXED_TERM;
				}

				myBank.addAccountWithSpecificType(nameAdd, initialBalance, type, newPassword);

				System.out.println("🎊 Welcome to the bank, " + nameAdd + "!");

				break;

			case CLOSE_ACCOUNT:

				String nameClose = Read.readString("Enter account owner name: ");
				Account accClose = myBank.findAccount(nameClose);

				if (accClose != null) {
					System.out.println("⚠️ Account Found!");
					System.out.println("Owner: " + accClose.getOwner());
					System.out.println("Balance: " + accClose.getBalance() + "€");

					String confirm = Read.readString("Are you SURE you want to close this account?");

					if (confirm.equalsIgnoreCase("y")) {

						boolean success = myBank.closeAccount(nameClose);

						if (success) {
							System.out.println("👋 Goodbye, " + nameClose + "!");
						} else {
							System.out.println("❌ Something went wrong with the database deletion.");
						}

					} else {
						System.out.println("❌ Operation cancelled. The account remains open.");
					}

				} else {
					System.out.println("⚠️ Error: No account found for '" + nameClose + "'");
				}

				break;

			case ANNUAL_INTEREST:

				System.out.println("⏳ Calculating interest for all accounts...");
				myBank.applyAnnualInterest();
				break;

			case TRANSFER:

				String from = Read.readString("Enter sender name: ");

				String to = Read.readString("Enter receiver name: ");

				double transferAmount = Read.readDouble("Enter amount to transfer: ");

				String senderPass = Read.readString("Enter password for " + from + ": ");

				myBank.transferMoney(from, to, transferAmount, senderPass);

				break;

			case VIEW_STATEMENT:

				String statementName = Read.readString("Enter account owner name for statement:");
				Account accStat = myBank.findAccount(statementName);

				if (accStat != null) {
					myBank.printStatement(accStat.getId());
				} else {
					System.out.println("❌ Account not found.");
				}

				break;

			case CHANGE_PASSWORD:

				String nameChange = Read.readString("Enter account name: ");
				Account accChange = myBank.findAccount(nameChange);

				if (accChange != null) {
					String currentPass = Read.readString("Enter current password: ");

					if (accChange.verifyPassword(currentPass)) {
						String newPass = Read.readString("Enter new password: ");
						String confirmPass = Read.readString("Confirm new password: ");

						if (newPass.equals(confirmPass)) {
							accChange.setPassword(newPass);
							myBank.updatePasswordInDB(accChange, newPass);
							System.out.println("Password changed!");
						} else {
							System.out.println("❌ Passwords do not match.");
						}
					} else {
						System.out.println("❌ Incorrect password.");
					}

				} else {
					System.out.println("❌ Account not found.");
				}

				break;

			case EXIT:

				System.out.println("Thank you for using Amazing Bilbao Bank. Have a nice day! Agur!");
				running = false;
				break;

			default:
				System.out.println("Invalid option. Try again.");
			}
		}

	}

	// HELPERS

	private static boolean isPasswordStrong(String password) {
		if (password.length() < 6) {
			System.out.println("❌ Error: Password must be at least 6 characters long.");
			return false;
		}

		if (!password.matches(".*\\d.*")) {
			System.out.println("Error: Password must contain at least one number.");
			return false;
		}

		if (!password.matches(".*[A-Z].*")) {
			System.out.println("❌ Error: Password must contain at least one uppercase letter.");
			return false;
		}
		return true;
	}

}
