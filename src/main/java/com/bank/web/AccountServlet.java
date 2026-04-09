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

		String dbPassword = System.getenv("DB_PASSWORD");
		String path = request.getContextPath();
		response.setContentType("text/html");
		java.io.PrintWriter out = response.getWriter();

		// Formatters
		Locale spain = Locale.of("es", "ES");
		NumberFormat euroFormatter = NumberFormat.getCurrencyInstance(spain);

		// Header
		UIHelper.printHeader(out, "Dashboard", sessionUser, path);

		// Welcome section
		out.println("<div class='welcome-section'>");
		out.println("  <h1>Good morning, " + sessionUser + "</h1>");
		out.println("  <p>Here is what's happening with your accounts today.</p>");
		out.println("</div>");

		// Accounts
		out.println("<h2 class='section-title'>Your Accounts</h2>");
		out.println("<div class='accounts-grid'>");

		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/amazing_bilbao_bank", "root",
					dbPassword);

			String sql = "SELECT balance, account_type, owner_name FROM accounts WHERE owner_name LIKE ?";
			PreparedStatement st = conn.prepareStatement(sql);
			st.setString(1, "%" + sessionUser + "%");

			ResultSet rs = st.executeQuery();

			while (rs.next()) {
				double balance = rs.getDouble("balance");
				String owner = rs.getString("owner_name");
				String rawType = rs.getString("account_type").toUpperCase().replace("-", "_").replace(" ", "_");

				AccountType type = AccountType.valueOf(rawType);
				Account acc = null;

				if (type == AccountType.SAVINGS) {
					acc = new SavingsAccount(0, owner, balance, 0, "");
				} else if (type == AccountType.FIXED_TERM_DEPOSIT) {
					acc = new FixedTermDeposit(0, owner, balance, "");
				} else {
					acc = new CheckingAccount(0, owner, balance, "");
				}

				// Clickable card

				out.println("<a href='details?id=" + rawType + "' class='card clickable-card'>");
				out.println("  <p class='account-holder'>Account Holder: <strong>" + acc.getOwner() + "</strong></p>");
				out.println("  <p class='account-type'>" + acc.getDisplayName() + "</p>");
				out.println("  <p class='balance'>" + euroFormatter.format(acc.getBalance()) + "</p>");
				out.println("</a>");
			}

			conn.close();

		} catch (Exception e) {
			e.printStackTrace();
		}

		out.println("</div>");

		// Quick Actions Section
		out.println("<h2 class='section-title'>Quick Actions</h2>");
		out.println("<div class='actions-container'>");
		out.println("  <a href='transfer' class='btn'>New Transfer</a>");
		out.println("  <a href='bizum' class='btn'>Send Bizum</a>");
		out.println("</div>");

		// Footer

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
