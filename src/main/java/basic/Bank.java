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

	// Method to search an account

	public Account findAccount(String nameToFind) {
		for (Account acc : accounts) {
			if (acc.getOwner().equals(nameToFind)) {
				return acc;
			}
		}
		System.out.println("Error: Account for " + nameToFind + "not found.");
		return null;
	}

	// Method to delete an account

	public void closeAccount(String nameToClose)

	{
		boolean removedAcc = accounts.removeIf(acc -> acc.getOwner().equals(nameToClose));

		if (removedAcc) {
			System.out.println("SUCCESS: Account for " + nameToClose + " has been closed.");
		} else {
			System.out.println("ERROR: Could not find an account for " + nameToClose);
		}
	}

}
