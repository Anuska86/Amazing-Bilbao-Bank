package bank.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

public class AccountDAO {
	private String dbPassword = System.getenv("DB_PASSWORD");
	private String url = "jdbc:mysql://localhost:3306/amazing_bilbao_bank";

public boolean insertAccount(String owner, String type, String iban, double balance) {
	String sql = "INSERT INTO accounts (owner_name, account_type, iban, balance, password) " +
            "VALUES (?, ?, ?, ?, (SELECT password FROM (SELECT password FROM accounts WHERE owner_name = ? LIMIT 1) as temp))";
	
	try (Connection conn = DriverManager.getConnection(url, "root", dbPassword);
			PreparedStatement ps = conn.prepareStatement(sql){
		
				
				ps.setString(1, owner);
	            ps.setString(2, type);
	            ps.setString(3, iban);
	            ps.setDouble(4, balance);
	            ps.setString(5, owner);
				
	} catch (Exception e) {
		e.printStackTrace();
        return false;
	}
	
	return false;
}

}
