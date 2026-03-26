package basic;

public class FixedTermDeposit extends Account {
	public FixedTermDeposit(String owner, double balance) {
		super(owner, balance);
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
