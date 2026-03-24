package basic;

public class HelloWorld {

	public static void main(String[] args) {

		Bank myBank = new Bank("International Java Bank");

		myBank.addAccounts(new SavingsAccount("Lauren", 1200.0, 2.5));
		myBank.addAccounts(new SavingsAccount("Anuska", 5000.0, 3.0));
		myBank.addAccounts(new SavingsAccount("Jon", 9800.0, 5));

		Account lauren = myBank.findAccount("Lauren");

		if (lauren != null) {
			lauren.deposit(100);
			lauren.withdraw(30);
			lauren.withdraw(2000);

			lauren.printHistory();
		}

		myBank.showStatus();
	}
}
