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

	// OVERRIDES

	// Interface method

	@Override

	public void applyInterest() {

		double interest = getBalance() * (interestRate / 100);
		deposit(interest);
		System.out.println("Interest applied: " + interest + "€");

	}

	@Override

	public void printMonthlyReport() {
		System.out.println("--- SAVINGS ACCOUNT REPORT ---");
		System.out.println("Owner: " + getOwner());
		System.out.println("Current Balance: " + getBalanceWithCurrency());
		System.out.println("Interest Rate: " + interestRate + "%");
		System.out.println("------------------------------");
	}

	// Abstract method

	@Override
	public void withdraw(double amount) {
		double totalWithFee = amount + 2.0;

		if (totalWithFee <= getBalance()) {
			super.withdraw(totalWithFee);
			transactionHistory.add("Savings Fee applied: 2.0€");
			System.out.println("Note: A 2€ saving fee was applied");
		} else {
			System.out.println("Error: Not enough funds to cover the withdrawal");
		}
	}

	@Override
	public String toString() {
		return super.toString() + "| Interest Rate: " + interestRate + "%";

	}

}
