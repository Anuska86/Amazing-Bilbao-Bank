package com.bank.web;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.NumberFormat;
import java.util.Locale;

import com.bank.utils.UIHelper;

import bank.models.Account;
import bank.models.AccountType;
import bank.models.CheckingAccount;
import bank.models.FixedTermDeposit;
import bank.models.SavingsAccount;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class AccountServlet
 */
@WebServlet("/account")
public class AccountServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * Default constructor.
	 */
	public AccountServlet() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		String sessionUser = (String) request.getSession().getAttribute("user");

		if (sessionUser == null) {
			response.sendRedirect("index.html");
			return;
		}

		double balanceFromDB = 0.0;
		String dbPassword = System.getenv("DB_PASSWORD");
		String typeFromDB = "";

		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/amazing_bilbao_bank", "root",
					dbPassword);

			String sql = "SELECT balance, account_type FROM accounts WHERE owner_name = ?";
			PreparedStatement st = conn.prepareStatement(sql);
			st.setString(1, sessionUser);

			ResultSet rs = st.executeQuery();

			if (rs.next()) {
				balanceFromDB = rs.getDouble("balance");
				typeFromDB = rs.getString("account_type").toUpperCase().replace("-", "_").replace(" ", "_");

			}
			conn.close();

		} catch (Exception e) {
			e.printStackTrace();
		}

		AccountType type = AccountType.valueOf(typeFromDB);

		Account myAcc = null;

		switch (type) {
		case CHECKING:
			myAcc = new CheckingAccount(0, sessionUser, balanceFromDB, "");
			break;

		case FIXED_TERM_DEPOSIT:
			myAcc = new FixedTermDeposit(0, sessionUser, balanceFromDB, "");
			break;

		case SAVINGS:
			myAcc = new SavingsAccount(0, sessionUser, balanceFromDB, 0, "");
			break;

		default:
			break;
		}

		response.setContentType("text/html");
		java.io.PrintWriter out = response.getWriter();

		Locale spain = Locale.of("es", "ES");
		NumberFormat euroFormatter = NumberFormat.getCurrencyInstance(spain);
		String formattedBalance = euroFormatter.format(myAcc.getBalance());

		String path = request.getContextPath();

		UIHelper.printHeader(out, "Dashboard", sessionUser, path);

		out.println("<div class='welcome-section'>");
		out.println("  <h1>Good morning, " + sessionUser + "</h1>");
		out.println("  <p>Here is what's happening with your accounts today.</p>");
		out.println("</div>");

		out.println("  <div class='card'>");

		out.println("  <p class='account-holder'>Account Holder: <strong>" + myAcc.getOwner() + "</strong></p>");
		out.println("  <p class='account-type'>" + myAcc.getDisplayName() + "</p>");
		out.println("  <p class='balance'>" + formattedBalance + "</p>");

		out.println("  </div>");

		UIHelper.printFooter(out);

	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
