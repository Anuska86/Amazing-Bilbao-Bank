package basic;

public class CheckingAccount extends Account {

	public CheckingAccount(String owner, double balance) {
		super(owner, balance);
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
