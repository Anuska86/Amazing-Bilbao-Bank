package basic;

public class SavingsAccount extends Account implements InterestBearing {

	private double interestRate;

	// CONSTRUCTOR

	public SavingsAccount(String owner, double balance, double interestRate) {

		// super calls the constructor of Account (parent)
		super(owner, balance);
		// TODO Auto-generated constructor stub
		this.interestRate = interestRate;
	}

	// 2nd CONSTRUCTOR

	public SavingsAccount(String owner, double balance) {
		super(owner, balance);
		this.interestRate = 2.0;
	}

	// OVERRIDES

	// Interface method

	@Override

	public void applyInterest() {

		double interest = getBalance() * (interestRate / 100);
		deposit(interest);
		System.out.println("Interest applied: " + interest + "€");

	}

	// Abstract method

	@Override

	public void printMonthlyReport() {
		System.out.println("--- SAVINGS ACCOUNT REPORT ---");
		System.out.println("Owner: " + getOwner());
		System.out.println("Current Balance: " + getBalanceWithCurrency());
		System.out.println("Interest Rate: " + interestRate + "%");
		System.out.println("------------------------------");
	}

	// Standard method

	@Override

	public double getInterestRate() {
		return this.interestRate;
	}

	@Override
	public boolean withdraw(double amount) {
		double totalWithFee = amount + 2.0;

		if (totalWithFee <= getBalance()) {
			boolean success = super.withdraw(totalWithFee);

			if (success) {
				transactionHistory.add("Savings Fee applied: 2.0€");
				System.out.println("Note: A 2€ saving fee was applied");
			}
			return success;
		} else {

			return false;
		}
	}

	@Override
	public String toString() {
		return super.toString() + "| Interest Rate: " + interestRate + "%";

	}

}
