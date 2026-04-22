package bank.models;

public class FixedTermDeposit extends Account {
	public FixedTermDeposit(int id, String owner, double balance, String iban, String password) {
		super(id, owner, balance, iban, password);
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
