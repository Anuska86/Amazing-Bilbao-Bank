package basic;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public abstract class Account {

	// Variables (Attributes)

	private String owner;
	private double balance;
	protected ArrayList<String> transactionHistory;

	// CONSTRUCTOR

	public Account(String owner, double balance) {
		this.owner = owner;
		this.balance = balance;
		this.transactionHistory = new ArrayList<>();
		transactionHistory.add("Account created with " + balance + "€");
	}

	// Methods (Actions)

	// Abstract Methods

	public abstract void printMonthlyReport();

	// Other Methods

	public void deposit(double amount) {
		balance += amount;
		transactionHistory.add(getTimestamp() + "Deposited: " + amount + "€");
	}

	public boolean withdraw(double amount) {
		if (amount > 0 && amount <= balance) {
			balance -= amount;
			transactionHistory.add(getTimestamp() + "Withdraw: " + amount + "€");
			return true;
		} else {
			System.out.println("FAILED Withdrawal: " + amount + "€ (Insufficient funds)");
			return false;
		}
	}

	public String toString() {
		return "Account Owner: " + owner + " | Balance " + balance + "€";
	}

	public void transfer(double amount, Account destinationAccount) {
		if (amount <= balance) {
			this.withdraw(amount);
			destinationAccount.deposit(amount);
			System.out.println("Transfer successful!");
		} else {
			System.out.println("Transfer failed, put in contact with your bank ");
		}
	}

	public void printHistory() {
		System.out.println("--- Transaction History for " + owner + " ---");
		for (String record : transactionHistory) {
			System.out.println("- " + record);
		}
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

	// HELPERS

	private String getTimestamp() {
		LocalDateTime now = LocalDateTime.now();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
		return "[" + now.format(formatter) + "]";
	}

}
