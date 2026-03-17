package basic;

public class Account {

	// Variables (Attributes)

	private String owner;
	private double balance;

	// CONSTRUCTOR

	public Account(String initialOwner, double initialBalance) {
		owner = initialOwner;
		balance = initialBalance;
	}

	// Methods (Actions)

	public void deposit(double amount) {
		balance += amount;
	}

	public void withdraw(double amount) {
		if (amount <= balance) {
			balance -= amount;
		} else {
			System.out.println("Error: Insufficient funds!");
		}
	}

	public String toString() {
		return "Account Owner: " + owner + " | Balance " + balance + "€";
	}

	// SETTERS & GETTERS

	// Owner group
	public void setOwner(String name) {
		owner = name;

	}

	public String getOwner() {
		return owner;
	}

	// Balance group
	public void setBalance(double amount) {
		balance = amount;
	}

	public double getBalance() {
		return balance;
	}

	// Balance with currency
	public String getBalanceWithCurrency() {
		return balance + "€";
	}

}
