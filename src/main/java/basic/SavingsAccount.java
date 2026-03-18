package basic;

public class SavingsAccount extends Account {

	private double interestRate;

	// CONSTRUCTOR

	public SavingsAccount(String owner, double balance, double interestRate) {

		// super calls the contructor of Account (parent)
		super(owner, balance);
		// TODO Auto-generated constructor stub
		this.interestRate = interestRate;
	}

	// OVERRIDES

	@Override
	public void withdraw(double amount) {
		double totalWithFee = amount + 2.0;

		if (totalWithFee <= getBalance()) {
			super.withdraw(totalWithFee);
			System.out.println("Note: A 2€ saving fee was applied");
		} else {
			System.out.println("Error: Not enough funds to cover the withdrawal");
		}
	}

	@Override
	public String toString() {
		return super.toString() + "| Interest Rate: " + interestRate + "%";

	}

	// NEW METHODS

	// a new method ONLY for savings accounts

	public void applyInterest() {
		double interest = getBalance() * (interestRate / 100);
		deposit(interest);
		System.out.println("Interest applied: " + interest + "€");
	}

}
