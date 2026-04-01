package bank.models;

public class CheckingAccount extends Account {

	public CheckingAccount(int id, String owner, double balance, String password) {
		super(id, owner, balance, password);
	}

	@Override
	public void printMonthlyReport() {
		System.out.println("--- CHECKING ACCOUNT REPORT ---");
		System.out.println("Owner: " + getOwner());
		System.out.println("Balance: " + getBalanceWithCurrency());
		System.out.println("Status: Standard (No Interest)");
		System.out.println("-------------------------------");

	}

	@Override

	public double getInterestRate() {
		return 0.0;
	}

}
