package bank.logic;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import bank.models.Account;
import bank.models.AccountType;
import bank.models.CheckingAccount;
import bank.models.FixedTermDeposit;
import bank.models.SavingsAccount;

public class Bank {
	private String bankName;

	// Key is String (Name), Value is Account (The Object)
	private Map<String, Account> accountsByName = new TreeMap<>();

	// Key is Integer (ID), Value is Account (The Object)
	private Map<Integer, Account> accountsById = new TreeMap<>();

	// Constructor

	public Bank(String name) {
		this.bankName = name;
		this.accountsByName = new HashMap<>();
		this.accountsById = new HashMap<>();
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

	public void addAccount(Account acc) {
		String sql = "INSERT INTO accounts (owner_name, balance, account_type) VALUES (?, ?, ?)";

		try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

			pstmt.setString(1, acc.getOwner());
			pstmt.setDouble(2, acc.getBalance());
			String type = acc.getClass().getSimpleName().replace("Account", "");
			pstmt.setString(3, type);

			pstmt.executeUpdate();

			try (ResultSet rs = pstmt.getGeneratedKeys()) {
				if (rs.next()) {
					int newId = rs.getInt(1);
					acc.setId(newId);

					accountsById.put(newId, acc);
					accountsByName.put(acc.getOwner().toLowerCase(), acc);
				}
			}

			System.out.println("✅ Account saved to Database and Memory!");
		} catch (SQLException e) {
			System.out.println("❌ Error saving to database");
			e.printStackTrace();
		}

	}

	// Method to open accounts with type

	public void addAccountWithSpecificType(String name, double balance, AccountType type, String password) {
		String sql = "INSERT INTO accounts (owner_name, balance, account_type, password) VALUES (?, ?, ?, ?)";

		int generatedId = 0;
		Account newAcc = null;

		try (Connection conn = connect();
				PreparedStatement pstmt = conn.prepareStatement(sql, java.sql.Statement.RETURN_GENERATED_KEYS)) {

			pstmt.setString(1, name);
			pstmt.setDouble(2, balance);
			pstmt.setString(3, type.name());
			pstmt.setString(4, password);

			pstmt.executeUpdate();
			System.out.println("✅ " + type + " account created for " + name);

			try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
				if (generatedKeys.next()) {
					generatedId = generatedKeys.getInt(1);
				}
			}

			if (type == AccountType.SAVINGS) {
				newAcc = new SavingsAccount(generatedId, name, balance, password);
			} else if (type == AccountType.CHECKING) {
				newAcc = new CheckingAccount(generatedId, name, balance, password);
			} else if (type == AccountType.FIXED_TERM_DEPOSIT) {
				newAcc = new FixedTermDeposit(generatedId, name, balance, password);
			}

			if (newAcc != null) {
				accountsByName.put(name.toLowerCase(), newAcc);
				accountsById.put(generatedId, newAcc);
			}

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

	// Methods to search an account

	// BY ID

	public Account findAccountById(int id) {

		return accountsById.get(id);

	}

	// BY NAME

	public Account findAccount(String nameToFind) {

		String sql = "SELECT * FROM accounts WHERE LOWER (owner_name) = ?";

		try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setString(1, nameToFind.toLowerCase());
			ResultSet rs = pstmt.executeQuery();

			if (rs.next()) {
				int id = rs.getInt("id");
				String name = rs.getString("owner_name");
				double balance = rs.getDouble("balance");
				String typeFromDB = rs.getString("account_type");
				String passFromDB = rs.getString("password");

				if (typeFromDB.equalsIgnoreCase("Fixed-Term Deposit") || typeFromDB.equalsIgnoreCase("FIXED_TERM")) {
					return new FixedTermDeposit(id, name, balance, passFromDB);
				} else if (typeFromDB.equalsIgnoreCase("Savings") || typeFromDB.equalsIgnoreCase("SAVINGS")) {
					return new SavingsAccount(id, name, balance, passFromDB);
				} else {
					return new CheckingAccount(id, name, balance, passFromDB);
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

		String sql = "UPDATE accounts SET balance = ? WHERE owner_name = ?";

		try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

			pstmt.setDouble(1, newBalance);
			pstmt.setString(2, name);

			pstmt.executeUpdate();

		} catch (SQLException e) {
			System.out.println("❌ Database Error: Could not update balance for " + name);
			e.printStackTrace();
		}

	}

	// Method update balance and year DB

	public void updateBalanceAndYearInDB(String name, double newBalance, int year) {
		String sql = "UPDATE accounts SET balance = ?, last_interest_year = ? WHERE owner_name = ?";

		try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setDouble(1, newBalance);
			pstmt.setInt(2, year);
			pstmt.setString(3, name);
			pstmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	// Method to transfer money

	public void transferMoney(String fromName, String toName, double amount, String password) {
		Account sender = findAccount(fromName);
		Account receiver = findAccount(toName);

		if (sender == null || receiver == null) {
			System.out.println("❌ Error: One or both accounts not found.");
			return;
		}

		if (!sender.verifyPassword(password)) {
			System.out.println("❌ Error: Incorrect password for sender " + fromName);
			return;
		}

		if (sender.withdraw(amount)) {

			receiver.deposit(amount);

			updateBalanceInDB(sender);
			updateBalanceInDB(receiver);

			logTransaction(sender.getId(), "Transfer to " + receiver.getOwner(), -amount);
			logTransaction(receiver.getId(), "Transfer from " + sender.getOwner(), amount);

			System.out.println("✅ Transfer completed and logged to history!");
		}

	}

	// Method to log the transactions

	public void logTransaction(int accountId, String type, double amount) {
		String sql = "INSERT INTO transactions (account_id, type, amount) VALUES (?, ?, ?)";

		try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

			pstmt.setInt(1, accountId);
			pstmt.setString(2, type);
			pstmt.setDouble(3, amount);

			pstmt.executeUpdate();

		} catch (SQLException e) {
			System.out.println("⚠️ Warning: Could not save transaction history for ID" + accountId);
			e.printStackTrace();
		}
	}

	// Method to print the account statement

	public void printStatement(int accountId) {
		System.out.println("\n--- 🧾 OFFICIAL STATEMENT FOR ACCOUNT ID" + accountId + " ---");
		System.out.printf("%-20s | %-15s | %-10s\n", "DATE", "TYPE", "AMOUNT");
		System.out.println("-------------------------------------------------------------");

		String sql = "SELECT * FROM transactions WHERE account_id = ? ORDER BY transaction_date DESC";

		try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setInt(1, accountId);
			ResultSet rs = pstmt.executeQuery();

			boolean found = false;
			while (rs.next()) {
				found = true;
				String date = rs.getString("transaction_date");
				String type = rs.getString("type");
				double amount = rs.getDouble("amount");

				System.out.printf("%-20s | %-15s | %10.2f€\n", date, type, amount);
			}

			if (!found) {
				System.out.println("No transactions found for this account.");
			}
			System.out.println("-------------------------------------------------------------");

		} catch (SQLException e) {
			System.out.println("❌ Error retrieving statement.");
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
		ArrayList<Account> sortedList = new ArrayList<>(accountsByName.values());

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

		int currentYear = LocalDate.now().getYear();

		String selectSql = "SELECT * FROM accounts";

		try (Connection conn = connect();
				PreparedStatement selectStmt = conn.prepareStatement(selectSql);
				ResultSet rs = selectStmt.executeQuery()) {

			while (rs.next()) {
				String name = rs.getString("owner_name");
				int lastYear = rs.getInt("last_interest_year");

				if (lastYear >= currentYear) {
					System.out.println("ℹ️ Skipping " + name + ": Interest already applied for " + currentYear);
					continue;
				}

				double currentBalance = rs.getDouble("balance");
				Account acc = findAccount(name);

				if (acc != null) {
					double rate = acc.getInterestRate();
					double interestEarned = currentBalance * (rate / 100);
					double newBalance = currentBalance + interestEarned;

					updateBalanceAndYearInDB(name, newBalance, currentYear);

					System.out.printf("💰 %s earned %.2f€ interest (New Total: %.2f€)\n", name, interestEarned,
							newBalance);
				}

			}

			System.out.println("All accounts updated for the year!");
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	// Method to update the password in DB

	public void updatePasswordInDB(Account acc, String newPassword) {
		String sql = "UPDATE accounts SET password = ? WHERE owner_name = ?";

		try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

			pstmt.setString(1, newPassword);
			pstmt.setString(2, acc.getOwner());
			pstmt.executeUpdate();
			System.out.println("💾 Database: Password permanently updated for " + acc.getOwner());

		} catch (SQLException e) {
			System.out.println("❌ Database Error: Could not update password.");
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
