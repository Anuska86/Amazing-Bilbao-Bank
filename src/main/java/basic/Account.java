package basic;

public class Account {

	// Variables (Attributes)

	private String owner;
	private double balance;

	// Methods (Actions)

	public void deposit(double amount) {
		balance += amount;
	}

	// SETTERS

	public void setOwner(String name) {
		owner = name;
	}

	public void setBalance(double amount) {
		balance = amount;
	}

	// GETTERS

	public String getOwner() {
		return owner;
	}

	public double getBalance() {
		return balance;
	}

}
