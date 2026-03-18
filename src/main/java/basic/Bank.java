package basic;

import java.util.ArrayList;

public class Bank {
	private String bankName;
	private ArrayList<Account> accounts;

//Constructor

	public Bank(String name) {
		this.bankName = name;
		this.accounts = new ArrayList<>();

	}

//Method to open accounts

	public void addAccounts(Account acc) {
		this.accounts.add(acc);

	}

	// Method to show the accounts

	public void showStatus() {
		System.out.println("--- Welcome to " + bankName + "---");

		for (Account a : accounts) {
			System.out.println(a);
		}

		System.out.println("-------------------------------------");
	}

}
