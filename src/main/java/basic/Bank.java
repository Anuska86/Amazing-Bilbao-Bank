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
			String type = acc.getClass().getSimpleName().replace("Account", "");
			pstmt.setString(3, type);

			pstmt.executeUpdate();
			System.out.println("✅ Account saved to Database!");
		} catch (SQLException e) {
			System.out.println("❌ Error saving to database");
			e.printStackTrace();
		}

	}

	// Method to open accounts with type

	public void addAccountWithSpecificType(String name, double balance, String type) {
		String sql = "INSERT INTO accounts (owner_name, balance, account_type) VALUES (?, ?, ?)";

		try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

			pstmt.setString(1, name);
			pstmt.setDouble(2, balance);
			pstmt.setString(3, type);

			pstmt.executeUpdate();
			System.out.println("✅ " + type + " account created for " + name);

		} catch (SQLException e) {
			System.out.println("❌ Error: Could not create account.");
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
				String typeFromDB = rs.getString("account_type");

				System.out.printf("%-15s | %10.2f€ | %-10s\n", name, balance, typeFromDB);
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
				String typeFromDB = rs.getString("account_type");

				if (typeFromDB.equalsIgnoreCase("Fixed-Term Deposit")) {
					return new FixedTermDeposit(name, balance);
				} else if (typeFromDB.equalsIgnoreCase("Savings")) {
					return new SavingsAccount(name, balance);
				} else {
					return new SavingsAccount(name, balance);
				}

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

	// Method to update the balance for calculate annual interest

	public void updateBalanceInDB(String name, double newBalance) {

		String sql = "UPDATE accounts SET balance = ? WHERE LOWER owner_name =?";

		try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

			pstmt.setDouble(1, newBalance);
			pstmt.setString(2, name);

			pstmt.executeUpdate();

		} catch (SQLException e) {
			System.out.println("❌ Database Error: Could not update balance for " + name);
			e.printStackTrace();
		}

	}

	// Method to delete an account

	public boolean closeAccount(String nameToClose)

	{

		String sql = "DELETE FROM accounts WHERE LOWER(owner_name) = ?";

		try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

			pstmt.setString(1, nameToClose.toLowerCase());

			int rowsDeleted = pstmt.executeUpdate();

			if (rowsDeleted > 0) {
				System.out.println("✅ SUCCESS: Account for " + nameToClose + " has been closed.");
				return true;
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
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

	// Method to calculate the specific interest

	public void applyAnnualInterest() {

		String selectSql = "SELECT * FROM accounts";

		try (Connection conn = connect();
				PreparedStatement selectStmt = conn.prepareStatement(selectSql);
				ResultSet rs = selectStmt.executeQuery()) {

			while (rs.next()) {
				String name = rs.getString("owner_name");
				double currentBalance = rs.getDouble("balance");

				Account acc = findAccount(name);

				if (acc != null) {
					double rate = acc.getInterestRate();
					double interestEarned = currentBalance * (rate / 100);
					double newBalance = currentBalance + interestEarned;

					updateBalanceInDB(name, newBalance);

					System.out.printf("💰 %s earned %.2f€ interest (New Total: %.2f€)\n", name, interestEarned,
							newBalance);
				}

			}

			System.out.println("All accounts updated for the year!");
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	// SEGMENTING THE COSTUMERS

	// Method to get the VIPS clients

	public List<String> getVIPCustomers() {
		List<String> vips = new ArrayList<>();

		String sql = "SELECT owner_name FROM accounts WHERE balance >100000";

		try (Connection conn = connect();
				PreparedStatement pstmt = conn.prepareStatement(sql);
				ResultSet rs = pstmt.executeQuery()) {

			while (rs.next()) {
				vips.add(rs.getString("owner_name"));

			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return vips;
	}

}
