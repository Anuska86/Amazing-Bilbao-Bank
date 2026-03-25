package basic;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class BankDB {

	public static void main(String[] args) {

		String url = "jdbc:mysql://localhost:3306/amazing_bilbao_bank";
		String user = "root";
		String password = System.getenv("DB_PASSWORD");

		try (Connection conn = DriverManager.getConnection(url, user, password)) {
			System.out.println("✅ Connected to Bilbao Bank Database!");

			String sql = "SELECT * FROM accounts";
			Statement statement = conn.createStatement();
			ResultSet result = statement.executeQuery(sql);

			while (result.next()) {
				System.out.println("ID: " + result.getInt("id") + " | Name: " + result.getString("owner_name")
						+ " | Balance: " + result.getDouble("balance") + "€");
			}

		} catch (SQLException e) {
			System.out.println("❌ Database Error!");
			e.printStackTrace();

		}

	}

}
