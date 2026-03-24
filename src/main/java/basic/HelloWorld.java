package basic;

public class HelloWorld {

	public static void main(String[] args) {

		// SETUP

		Bank myBank = new Bank("International Java Bank");

		myBank.addAccounts(new SavingsAccount("Lauren", 1200.0, 2.5));
		myBank.addAccounts(new SavingsAccount("Anuska", 5000.0, 3.0));
		myBank.addAccounts(new SavingsAccount("Jon", 9800.0, 5));

		// USER's INTERFACE

		boolean running = true;

		while (running) {
			System.out.println("\n--- BANK MAIN MENU ---");
			System.out.println("1. Show All Accounts");
			System.out.println("2. Show VIP Customers");
			System.out.println("3. Deposit Money");
			System.out.println("4. Withdraw Money");
			System.out.println("5. Exit");

			int choice = Read.readInt("Choose an option: ");

			switch (choice) {
			case 1:

				myBank.showStatus();
				break;

			case 2:

				System.out.println("--- VIP List ---");
				myBank.getVIPCustomers().forEach(name -> System.out.println("⭐ " + name));
				break;

			case 3:

				String nameToDeposit = Read.readString("Enter account owner name: ");
				double depAmount = Read.readDouble("Enter amount to deposit: ");

				Account accDep = myBank.findAccount(nameToDeposit);
				if (accDep != null) {
					accDep.deposit(depAmount);
					System.out.println(" ✅ Deposit successful!");
				} else {
					System.out.println("❌ Account '" + nameToDeposit + "' not found.");
				}

				break;

			case 4:

				String nameWithdraw = Read.readString("Enter account owner name: ");
				double amountWithdraw = Read.readDouble("Enter amount to withdraw");
				Account accWithdraw = myBank.findAccount(nameWithdraw);

				if (accWithdraw != null) {

					if (accWithdraw.withdraw(amountWithdraw)) {
						System.out.println("✅ Please take your cash.");
					} else {
						System.out.println("⚠️ Transaction failed. Check your balance and try again.");
					}

				} else {
					System.out.println("❌ Account not found.");
				}

				break;
			case 5:

				System.out.println("Thank you for using Amazing Bilbao Bank. Have a nice day! Agur!");
				running = false;
				break;

			default:
				System.out.println("Invalid option. Try again.");
			}
		}

	}

}
