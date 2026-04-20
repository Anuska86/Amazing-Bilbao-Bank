package bank.models;

import java.sql.Timestamp;

public class Transaction {
	private int id;
	private String type;
	private double amount;
	private Timestamp date;
	private int accountId;

	public Transaction(int id, String type, double amount, Timestamp date, int accountId) {
		super();
		this.id = id;
		this.type = type;
		this.amount = amount;
		this.date = date;
		this.accountId = accountId;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public Timestamp getDate() {
		return date;
	}

	public void setDate(Timestamp date) {
		this.date = date;
	}

	public int getAccountId() {
		return accountId;
	}

	public void setAccountId(int accountId) {
		this.accountId = accountId;
	}

}
