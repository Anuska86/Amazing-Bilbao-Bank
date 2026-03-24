package basic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Bank {
	private String bankName;

	// Key is String (Name), Value is Account (The Object)
	private HashMap<String, Account> accountsMap;

	// Constructor

	public Bank(String name) {
		this.bankName = name;
		this.accountsMap = new HashMap<>();
	}

	// Method to open accounts

	public void addAccounts(Account acc) {
		accountsMap.put(acc.getOwner().toLowerCase(), acc);

	}

	// Method to show the accounts

	public void showStatus() {
		System.out.println("--- " + bankName + " Status ---");

		for (Account acc : accountsMap.values()) {
			System.out.printf("Owner: %-10s | Balance: %8.2f€ | Rate: %.1f%%%n", acc.getOwner(), acc.getBalance(),
					acc.getInterestRate());
		}

		System.out.println("-------------------------------------");
	}

	// Method to search an account

	public Account findAccount(String nameToFind) {

		Account acc = accountsMap.get(nameToFind.toLowerCase());

		if (acc == null) {

		}
		return acc;
	}

	// Method to delete an account

	public void closeAccount(String nameToClose)

	{

		Account removedAcc = accountsMap.remove(nameToClose.toLowerCase());

		if (removedAcc != null) {
			System.out.println("SUCCESS: Account for " + nameToClose + "has been closed");
		} else {
			System.out.println("ERROR: Could not find an account for " + nameToClose);
		}

	}

	// Method to compare the money quantity

	public void sortAccountsByBalance() {
		ArrayList<Account> sortedList = new ArrayList<>(accountsMap.values());

		sortedList.sort((a1, a2) -> Double.compare(a2.getBalance(), a1.getBalance()));

		System.out.println("--- Accounts Sorted by Balance (Highest First) ---");
		for (Account acc : sortedList) {
			System.out.println(acc);
		}
	}

	/*
	 * public void sortAccountsByBalance() { accounts.sort((a1, a2) ->
	 * Double.compare(a2.getBalance(), a1.getBalance())); }
	 */

	// SEGMENTING THE COSTUMERS

	// Method to get the VIPS clients

	public List<String> getVIPCustomers() {
		return accountsMap.values().stream().filter(acc -> acc.getBalance() > 5000).map(acc -> acc.getOwner()).toList();
	}

}
