package bank.models;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public abstract class Account {

	// Variables (Attributes)

	private int id;
	private String owner;
	private double balance;
	private String password;
	protected ArrayList<String> transactionHistory;

	// CONSTRUCTOR

	public Account(int id, String owner, double balance, String password) {
		this.id = id;
		this.owner = owner;
		this.balance = balance;
		this.password = password;
		this.transactionHistory = new ArrayList<>();
		transactionHistory.add("Account created with " + balance + "€");
	}

	// Methods (Actions)

	// Abstract Methods

	public abstract void printMonthlyReport();

	// Other Methods

	// Method String (account data)
	public String toString() {
		return "Account Owner: " + owner + " | Balance " + balance + "€";
	}

	// Method to do a deposit

	public boolean deposit(double amount) {
		if (amount <= 0) {
			System.out.println("❌ Error: Deposit amount must be positive.");
			return false;
		}
		this.balance += amount;
		this.transactionHistory.add(String.format("Deposited: %.2f€", amount));
		return true;
	}

	// Method to do a withdraw

	public boolean withdraw(double amount) {

		if (this instanceof FixedTermDeposit) {
			System.out
					.println("❌ ERROR: Access Denied. Fixed-Term Deposits cannot be withdrawn until the term expires!");
			return false;
		}

		if (amount > 0 && amount <= balance) {
			balance -= amount;
			transactionHistory.add(getTimestamp() + "Withdraw: " + amount + "€");
			return true;
		} else {
			System.out.println("FAILED Withdrawal: " + amount + "€ (Insufficient funds)");
			return false;
		}
	}

	// Method to do a transfer
	public void transfer(double amount, Account destinationAccount) {
		if (amount <= balance) {
			this.withdraw(amount);
			destinationAccount.deposit(amount);
			System.out.println("Transfer successful!");
		} else {
			System.out.println("Transfer failed, put in contact with your bank ");
		}
	}

	// Method to print the account history

	public void printHistory() {
		System.out.println("--- Transaction History for " + owner + " ---");
		for (String record : transactionHistory) {
			System.out.println("- " + record);
		}
		System.out.printf("Current Balance: %.2f€%n", balance);
	}

	// Method to default interest rate

	public double getInterestRate() {
		return 0.0;
	}

	// Method to verify the password

	public boolean verifyPassword(String input) {
		if (input == null)
			return false;

		return this.password.equals(input);
	}

	// Method to set the password

	public void setPassword(String newPassword) {
		if (newPassword != null && !newPassword.trim().isEmpty()) {
			this.password = newPassword;
			System.out.println("✅ Password updated in memory.");
		}
	}

	// Method to display the name of the account type nicely

	public String getDisplayName() {
		String accTypeName = this.getClass().getSimpleName();

		if (accTypeName.equals("FixedTermDeposit"))
			return "Fixed-Term Deposit";

		return accTypeName.replace("Account", "") + " Account";
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

	// Id Group

	public void setId(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}

	// GET Balance with currency
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
