package basic;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class Bank {
	private String bankName;

	// Key is String (Name), Value is Account (The Object)
	private Map<String, Account> accountsMap = new TreeMap<>();

	// Constructor

	public Bank(String name) {
		this.bankName = name;
		this.accountsMap = new HashMap<>();
	}

	// METHODS

	// MySQL CONNECTION

	private Connection connect() throws SQLException {
		String url = "jdbc:mysql://localhost:3306/amazing_bilbao_bank";
		String user = "root";
		String password = System.getenv("DB_PASSWORD");
		return DriverManager.getConnection(url, user, password);
	}

	// Method to open accounts

	public void addAccounts(Account acc) {
		String sql = "INSERT INTO accounts (owner_name, balance, account_type) VALUES (?, ?, ?)";

		try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

			pstmt.setString(1, acc.getOwner());
			pstmt.setDouble(2, acc.getBalance());
			pstmt.setString(3, acc.getClass().getSimpleName());

			pstmt.executeUpdate();
			System.out.println("✅ Account saved to Database!");
		} catch (SQLException e) {
			System.out.println("❌ Error saving to database");
			e.printStackTrace();
		}

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
