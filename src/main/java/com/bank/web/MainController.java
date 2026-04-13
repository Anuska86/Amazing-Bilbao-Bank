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
 * Servlet implementation class MainController
 */
@WebServlet("/bank")
public class MainController extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public MainController() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		// What the user wants to do

		String action = request.getParameter("action");

		if (action == null) {
			response.sendRedirect("index.html");
			return;
		}

		switch (action) {
		case "dashboard":

			showDashboard(request, response);

			break;

		case "details":

			showDetails(request, response);

			break;

		case "login":

			response.sendRedirect("index.html");

			break;

		case "logout":

			request.getSession().invalidate();
			response.sendRedirect("index.html");

			break;

		default:
			response.sendRedirect("index.html");
			break;
		}

	}

	// Show Dashboard

	private void showDashboard(HttpServletRequest request, HttpServletResponse response)
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
		UIHelper.printHeader(out, "Dashboard", sessionUser, path, null);

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

				out.println("<a href='bank?action=details&type=" + rawType + "' class='card clickable-card'>");
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
		out.println("  <div class='quick-actions-main-container'>");
		out.println("<h2 class='section-title'>Quick Actions</h2>");
		out.println("<div class='quick-actions-container'>");
		out.println("  <a href='bank?action=transfer' class='quick-actions-btn'>New Transfer</a>");
		out.println("  <a href='bank?action=bizum' class='quick-actions-btn'>Send Bizum</a>");
		out.println("</div>");
		out.println("  </div>");

		// Footer

		UIHelper.printFooter(out);
	}

	// SHOW ACCOUNT DETAILS

	private void showDetails(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String sessionUser = (String) request.getSession().getAttribute("user");
		String rawType = request.getParameter("type");

		String accountDisplayName = (rawType != null) ? rawType : "Unknown Account";

		if (sessionUser == null) {
			response.sendRedirect("index.html");
			return;
		}

		response.setContentType("text/html");
		java.io.PrintWriter out = response.getWriter();
		String path = request.getContextPath();

		// Formatter for euros

		Locale spain = Locale.of("es", "ES");
		NumberFormat euroFormatter = NumberFormat.getCurrencyInstance(spain);

		// Header
		UIHelper.printHeader(out, "Account Details", sessionUser, path, "details.css");

		// Main

		out.println("<div class='details-container'>");
		out.println("  <h1>Details for " + accountDisplayName + " account</h1>");

		try {
			String dbPassword = System.getenv("DB_PASSWORD");
			Class.forName("com.mysql.cj.jdbc.Driver");
			Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/amazing_bilbao_bank", "root",
					dbPassword);

			String sql = "SELECT balance, owner_name, co_owner_name FROM accounts WHERE owner_name = ? AND account_type = ?";
			PreparedStatement st = conn.prepareStatement(sql);
			st.setString(1, sessionUser);
			st.setString(2, rawType.toLowerCase().replace("_", "-"));

			ResultSet rs = st.executeQuery();

			if (rs.next()) {
				double balance = rs.getDouble("balance");
				String titular = rs.getString("owner_name");
				String cotitular = rs.getString("co_owner_name");

				out.println("<div class='detail-card'>");
				out.println("  <div class='account-info-header'>");
				out.println("    <span class='type-badge'>Active Account</span>");
				out.println("    <p class='label'>Available Balance:</p>");
				out.println("  </div>");
				out.println("  <h2 class='detail-balance'>" + euroFormatter.format(balance) + "</h2>");
				out.println("  <p class='iban-display'>ES91 2100 0412 8802 0103 ****</p>");

				out.println("  <div class='ownership-info'>");
				out.println("    <div class='info-group'>");
				out.println("      <span class='small-label'>Main Titular:</span>");
				out.println("      <span class='info-value'>" + titular + "</span>");
				out.println("    </div>");

				if (cotitular != null && !cotitular.trim().isEmpty()) {
					out.println("    <div class='info-group'>");
					out.println("      <span class='small-label'>Cotitular:</span>");
					out.println("      <span class='info-value'>" + cotitular + "</span>");
					out.println("    </div>");
				}

				out.println("  </div>");
				out.println("</div>");
			} else {
				out.println("<p class='error-msg'>No data found for this specific account.</p>");
			}
			conn.close();

		} catch (Exception e) {
			e.printStackTrace();
			out.println("<p style='color:red;'>Error connecting to the bank database.</p>");
		}

		out.println("  <a href='bank?action=dashboard' class='back-btn'>&larr; Back to Dashboard</a>");
		out.println("</div>");

		// Footer
		UIHelper.printFooter(out);

	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		String action = request.getParameter("action");

		if ("login".equals(action)) {
			handleLogin(request, response);
		}

	}

	// Login

	private void handleLogin(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		String user = request.getParameter("username");
		String pass = request.getParameter("password");

		String dbPassword = System.getenv("DB_PASSWORD");

		try {

			Class.forName("com.mysql.cj.jdbc.Driver");

			Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/amazing_bilbao_bank", "root",
					dbPassword);

			String sql = "SELECT * FROM accounts WHERE owner_name = ? AND password = ?";
			PreparedStatement st = conn.prepareStatement(sql);
			st.setString(1, user);
			st.setString(2, pass);

			ResultSet rs = st.executeQuery();

			if (rs.next()) {
				request.getSession().setAttribute("user", user);
				response.sendRedirect("bank?action=dashboard");

			} else {

				response.getWriter().println("<script>alert('Invalid Login!'); window.location='index.html';</script>");
			}
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
			response.getWriter().println("Database error: " + e.getMessage());
		}

	}

}
