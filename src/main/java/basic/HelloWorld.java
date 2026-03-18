package basic;

public class HelloWorld {

	public static void main(String[] args) {

		Bank myBank = new Bank("Amazing Bilbao Bank");

		myBank.addAccounts(new Account("Anuska", 1000.0));
		myBank.addAccounts(new SavingsAccount("Jon", 2000.0, 5.0));
		myBank.addAccounts(new Account("Lauren", 1300.0));
		myBank.addAccounts(new Account("Ione", 1200.0));

		myBank.showStatus();

	}
}
