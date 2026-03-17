package basic;

public class HelloWorld {

	public static void main(String[] args) {

		// Creating Accounts

		Account acc1 = new Account("Anuska", 1500.50);
		Account acc2 = new Account("Jon", 5300.30);

		// Creating the Bank and adding the accounts

		Bank myBank = new Bank("Amazing Bilbao Bank");
		myBank.addAccounts(acc1, acc2);

		// Showing all the accounts

		myBank.showStatus();

	}

}
