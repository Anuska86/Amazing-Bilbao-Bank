package basic;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
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

		System.out.printf("%-15s | %-12s | %-10s\n", "OWNER", "BALANCE", "TYPE");

		System.out.println("-------------------------------------");

		String sql = "SELECT * FROM accounts";

		try (Connection conn = connect();
				PreparedStatement pstmt = conn.prepareStatement(sql);
				ResultSet rs = pstmt.executeQuery()) {

			while (rs.next()) {
				String name = rs.getString("owner_name");
				double balance = rs.getDouble("balance");
				String type = "Savings";

				System.out.printf("%-15s | %10.2f€ | %-10s\n", name, balance, type);
			}

		} catch (

		SQLException e) {
			System.out.println("❌ Error loading accounts: " + e.getMessage());
		}

	}

	// Method to search an account

	public Account findAccount(String nameToFind) {

		String sql = "SELECT * FROM accounts WHERE LOWER (owner_name) = ?";

		try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setString(1, nameToFind.toLowerCase());
			ResultSet rs = pstmt.executeQuery();

			if (rs.next()) {
				String name = rs.getString("owner_name");
				double balance = rs.getDouble("balance");

				return new SavingsAccount(name, balance);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	// Method to update the balance

	public void updateBalanceInDB(Account acc) {
		String sql = "UPDATE accounts SET balance = ? WHERE LOWER (owner_name) = ?";

		try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

			pstmt.setDouble(1, acc.getBalance());
			pstmt.setString(2, acc.getOwner().toLowerCase());

			pstmt.executeUpdate();
			System.out.println("💾 Database updated: New balance is " + acc.getBalance() + "€");

		} catch (SQLException e) {
			System.out.println("❌ Database update failed!");
			e.printStackTrace();
		}
	}

	// Method to delete an account

	public void closeAccount(String nameToClose)

	{

		String sql = "DELETE FROM accounts WHERE LOWER(owner_name) = ?";

		try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

			pstmt.setString(1, nameToClose.toLowerCase());
			int rowsDeleted = pstmt.executeUpdate();

			if (rowsDeleted > 0) {
				System.out.println("✅ SUCCESS: Account for " + nameToClose + " has been closed.");
			} else {
				System.out.println("⚠️ ERROR: Could not find an account for " + nameToClose);
			}

		} catch (SQLException e) {
			e.printStackTrace();
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
