package bank.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import bank.models.Account;

public class AccountDAO {
	private String dbPassword = System.getenv("DB_PASSWORD");
	private String url = "jdbc:mysql://localhost:3306/amazing_bilbao_bank";

	public boolean insertAccount(Account acc) {

		String sql = "INSERT INTO accounts (owner_name, account_type, iban, balance, password) VALUES (?, ?, ?, ?, ?)";

		try (Connection conn = DriverManager.getConnection(url, "root", dbPassword);
				PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

			ps.setString(1, acc.getOwner());
			ps.setString(2, acc.getType());
			ps.setString(3, acc.getIban());
			ps.setDouble(4, acc.getBalance());
			ps.setString(5, acc.getPassword());

			int rows = ps.executeUpdate();

			if (rows > 0) {
				try (ResultSet rs = ps.getGeneratedKeys()) {
					if (rs.next())
						acc.setId(rs.getInt(1));
					return true;
				}
			}

		} catch (SQLException e) {
			System.err.println("Database Error: " + e.getMessage());
			e.printStackTrace();
		}

		return false;

	}

}
