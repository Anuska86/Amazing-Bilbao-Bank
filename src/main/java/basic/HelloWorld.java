package basic;

import java.util.List;

public class HelloWorld {

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
			System.out.println("7. Run Annual Interest (Year-End)");
			System.out.println("8. Transfer Money");
			System.out.println("0. Exit");

			int choice = Read.readInt("Choose an option: ");

			switch (choice) {
			case 1:

				myBank.showStatus();
				break;

			case 2:

				List<String> vips = myBank.getVIPCustomers();
				System.out.println("--- ⭐ VIP CUSTOMERS (>100000€) ---");

				if (vips.isEmpty()) {
					System.out.println("No VIPs found. Time to find richer friends!");
				} else {
					vips.forEach(name -> System.out.println("💎 " + name));
				}

				break;

			case 3:

				String nameToDeposit = Read.readString("Enter account owner name: ");
				Account accDep = myBank.findAccount(nameToDeposit);

				if (accDep == null) {
					System.out.println("❌ Account '" + nameToDeposit + "' not found.");
					break;
				}

				double depAmount = Read.readDouble("Enter amount to deposit: ");

				if (accDep.deposit(depAmount)) {
					myBank.updateBalanceInDB(accDep);
					myBank.logTransaction(nameToDeposit, "Deposit", depAmount);
					System.out.println(" ✅ Deposit successful!");
				}

				break;

			case 4:

				String nameWithdraw = Read.readString("Enter account owner name: ");
				Account accWithdraw = myBank.findAccount(nameWithdraw);

				if (accWithdraw == null) {
					System.out.println("❌ Error: Account '" + nameWithdraw + "' does not exist.");
					break;
				}

				double amountWithdraw = Read.readDouble("Enter amount to withdraw: ");

				if (accWithdraw.withdraw(amountWithdraw)) {
					System.out.println("✅ Please take your cash.");
				} else {
					System.out.println("⚠️ Transaction failed. Check your balance and try again.");
				}

				break;

			case 5:

				String nameAdd = Read.readString("Enter new account name: ");
				double initialBalance = Read.readDouble("Enter initial deposit: ");

				System.out.println("Choose account type:");
				System.out.println("1. Checking");
				System.out.println("2. Savings");
				System.out.println("3. Fixed-Term Deposit");

				int typeChoice = Read.readInt("Select (1-2-3:)");

				String typeStr;

				if (typeChoice == 1) {
					typeStr = "Checking";
				} else if (typeChoice == 2) {
					typeStr = "Savings";
				} else {
					typeStr = "Fixed-Term Deposit";
				}

				myBank.addAccountWithSpecificType(nameAdd, initialBalance, typeStr);

				System.out.println("🎊 Welcome to the bank, " + nameAdd + "!");

				break;

			case 6:

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

			case 7:

				System.out.println("⏳ Calculating interest for all accounts...");
				myBank.applyAnnualInterest();
				break;

			case 8:

				String from = Read.readString("Enter sender name: ");

				String to = Read.readString("Enter receiver name: ");

				double transferAmount = Read.readDouble("Enter amount to transfer: ");
				myBank.transferMoney(from, to, transferAmount);

				break;

			case 0:

				System.out.println("Thank you for using Amazing Bilbao Bank. Have a nice day! Agur!");
				running = false;
				break;

			default:
				System.out.println("Invalid option. Try again.");
			}
		}

	}

}
