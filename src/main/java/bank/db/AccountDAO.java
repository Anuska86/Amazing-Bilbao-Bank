package bank.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class AccountDAO {
	private String dbPassword = System.getenv("DB_PASSWORD");
	private String url = "jdbc:mysql://localhost:3306/amazing_bilbao_bank";

	public int insertAccount(String owner, String type, String iban, double balance, String password) {

		String sql = "INSERT INTO accounts (owner_name, account_type, iban, balance, password) VALUES (?, ?, ?, ?, ?)";

		try (Connection conn = DriverManager.getConnection(url, "root", dbPassword);
				PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

			ps.setString(1, owner);
			ps.setString(2, type);
			ps.setString(3, iban);
			ps.setDouble(4, balance);
			ps.setString(5, password);

			int rows = ps.executeUpdate();

			if (rows > 0) {
				try (ResultSet rs = ps.getGeneratedKeys()) {
					if (rs.next())
						return rs.getInt(1);
				}
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return -1;

	}

}
