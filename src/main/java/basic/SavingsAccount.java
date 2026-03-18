package basic;

public class SavingsAccount extends Account {

	private double interestRate;

	public SavingsAccount(String owner, double balance, double interestRate) {

		// super calls the contructor of Account (parent)
		super(owner, balance);
		// TODO Auto-generated constructor stub
		this.interestRate = interestRate;
	}

	// a new method ONLY for savings accounts

	public void applyInterest() {
		double interest = getBalance() * (interestRate / 100);
		deposit(interest);
		System.out.println("Interest applied: " + interest + "€");
	}

}
