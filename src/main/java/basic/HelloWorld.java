package basic;

import java.util.List;

public class HelloWorld {

	public static void main(String[] args) {

		// SETUP

		Bank myBank = new Bank("International Java Bank");

		myBank.addAccounts(new SavingsAccount("Lauren", 1200.0, 2.5));
		myBank.addAccounts(new SavingsAccount("Anuska", 5000.0, 3.0));
		myBank.addAccounts(new SavingsAccount("Jon", 9800.0, 5));

		// DISPLAYING THE INFO

		myBank.showStatus();

		List<String> vips = myBank.getVIPCustomers();

		System.out.println("--- Our VIP Customers ---");
		vips.forEach(name -> System.out.println("⭐ " + name));

		// INTERACTION

		double money = Read.readDouble("How much you want to deposit? ");

		// UPDATE

		Account acc = myBank.findAccount("Anuska");

		if (acc != null) {
			acc.deposit(money);
			acc.printHistory();
		}
	}

}
