package bank.models;

public class FixedTermDeposit extends Account {
	public FixedTermDeposit(int id, String owner, double balance, String password) {
		super(id, owner, balance, password);
	}

	@Override
	public void printMonthlyReport() {
		System.out.println("Monthly Report for Fixed-Term Deposit: " + getOwner());
	}

	@Override

	public double getInterestRate() {
		return 5.0;
	}
}
