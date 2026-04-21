package com.bank.web;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import bank.models.Account;
import bank.models.AccountType;
import bank.models.CheckingAccount;
import bank.models.FixedTermDeposit;
import bank.models.SavingsAccount;
import bank.models.Transaction;
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

		case "transfer":
			showTransferForm(request, response);
			break;

		case "history":
			showHistory(request, response);
			break;

		default:
			response.sendRedirect("index.html");
			break;
		}

	}

	// Fetch history methog

	private void showHistory(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		String accountIdParam = request.getParameter("accountId");

		if (accountIdParam == null) {
			response.sendRedirect("bank?action=dashboard");
			return;
		}

		List<Transaction> historyList = new ArrayList<>();
		String dbPassword = System.getenv("DB_PASSWORD");

		try {

			Class.forName("com.mysql.cj.jdbc.Driver");
			Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/amazing_bilbao_bank", "root",
					dbPassword);

			// JOIN with accounts table

			String sql = "SELECT t.* FROM transactions t " + "JOIN accounts a ON t.account_id = a.id "
					+ "WHERE a.owner_name = ? " + "ORDER BY t.transaction_date DESC";

			PreparedStatement st = conn.prepareStatement(sql);
			st.setInt(1, Integer.parseInt(accountIdParam));

			ResultSet rs = st.executeQuery();

			while (rs.next()) {
				// Map the transactions table

				Transaction trans = new Transaction(rs.getInt("id"), rs.getString("type"), rs.getDouble("amount"),
						rs.getTimestamp("transaction_date"), rs.getInt("account_id"));
				historyList.add(trans);
			}
			conn.close();

		} catch (Exception e) {
			e.printStackTrace();
		}

		request.setAttribute("transactions", historyList);
		request.getRequestDispatcher("/WEB-INF/history.jsp").forward(request, response);

	}

	// Transfer Form

	private void showTransferForm(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String sessionUser = (String) request.getSession().getAttribute("user");

		if (sessionUser == null) {
			response.sendRedirect("index.html");
			return;
		}

		List<Account> accountList = new ArrayList<>();
		String dbPassword = System.getenv("DB_PASSWORD");

		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/amazing_bilbao_bank", "root",
					dbPassword);

			String sql = "SELECT balance, account_type, owner_name FROM accounts WHERE owner_name = ?";
			PreparedStatement st = conn.prepareStatement(sql);
			st.setString(1, sessionUser);

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

				accountList.add(acc);

			}

			conn.close();

		} catch (Exception e) {
			e.printStackTrace();
		}

		request.setAttribute("accounts", accountList);
		request.getRequestDispatcher("/WEB-INF/transfer.jsp").forward(request, response);

	}

	// Show Dashboard

	private void showDashboard(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		// Session check

		String sessionUser = (String) request.getSession().getAttribute("user");

		if (sessionUser == null) {
			response.sendRedirect("index.html");
			return;
		}

		// Data
		List<Account> accountList = new ArrayList<>();
		String dbPassword = System.getenv("DB_PASSWORD");

		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/amazing_bilbao_bank", "root",
					dbPassword);

			String sql = "SELECT balance, account_type, owner_name FROM accounts WHERE owner_name = ?";
			PreparedStatement st = conn.prepareStatement(sql);
			st.setString(1, sessionUser);

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

				accountList.add(acc);

			}

			conn.close();

		} catch (Exception e) {
			e.printStackTrace();
		}

		request.setAttribute("user", sessionUser);
		request.setAttribute("accounts", accountList);
		request.getRequestDispatcher("/WEB-INF/dashboard.jsp").forward(request, response);
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

		try {
			String dbPassword = System.getenv("DB_PASSWORD");
			Class.forName("com.mysql.cj.jdbc.Driver");
			Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/amazing_bilbao_bank", "root",
					dbPassword);

			String sql = "SELECT balance, owner_name, co_owner_name FROM accounts WHERE owner_name = ? AND account_type = ?";
			PreparedStatement st = conn.prepareStatement(sql);
			st.setString(1, sessionUser);
			st.setString(2, rawType.toUpperCase());

			ResultSet rs = st.executeQuery();

			if (rs.next()) {

				request.setAttribute("balance", rs.getDouble("balance"));
				request.setAttribute("titular", rs.getString("owner_name"));
				request.setAttribute("cotitular", rs.getString("co_owner_name"));
				request.setAttribute("accountName", accountDisplayName);
			}

			conn.close();

		} catch (Exception e) {
			e.printStackTrace();

		}

		request.getRequestDispatcher("/WEB-INF/details.jsp").forward(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		String action = request.getParameter("action");

		if ("login".equals(action)) {
			handleLogin(request, response);
		} else if ("processTransfer".equals(action)) {
			handleTransfer(request, response);

		}
	}

	// Handle Transfer

	private void handleTransfer(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String user = (String) request.getSession().getAttribute("user");
		String fromAcc = request.getParameter("fromAccount");
		String amountRaw = request.getParameter("amount");
		String type = request.getParameter("transferType");

		double amount = 0;
		try {
			amount = Double.parseDouble(amountRaw);
			if (amount <= 0) {
				response.sendRedirect("bank?action=transfer&msg=invalid_amount&transferType=" + type);
				return;
			}
		} catch (NumberFormatException | NullPointerException e) {
			response.sendRedirect("bank?action=transfer&msg=invalid_amount&transferType=" + type);
			return;
		}

		String dbPassword = System.getenv("DB_PASSWORD");

		// 1. OPEN CONNECTION FIRST
		try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/amazing_bilbao_bank", "root",
				dbPassword)) {
			conn.setAutoCommit(false);

			String recipient;
			String toAcc = ""; // Initialize it

			// 2. NOW PERFORM THE LOOKUP INSIDE THE CONNECTION SCOPE
			if ("external".equals(type)) {
				recipient = request.getParameter("recipientName");
				String findAccSQL = "SELECT account_type FROM accounts WHERE owner_name = ? LIMIT 1";

				try (PreparedStatement psFind = conn.prepareStatement(findAccSQL)) {
					psFind.setString(1, recipient);
					try (ResultSet rsFind = psFind.executeQuery()) {
						if (rsFind.next()) {
							toAcc = rsFind.getString("account_type");
						} else {
							// User doesn't exist at all
							response.sendRedirect("bank?action=transfer&msg=user_not_found&transferType=external");
							return;
						}
					}
				}
			} else {
				recipient = user;
				toAcc = request.getParameter("toAccountInternal");
			}

			// 3. PROCEED WITH THE TRANSACTION
			try {
				String subSQL = "UPDATE accounts SET balance = balance - ? WHERE owner_name = ? AND account_type = ? AND balance >= ?";
				PreparedStatement psSub = conn.prepareStatement(subSQL);
				psSub.setDouble(1, amount);
				psSub.setString(2, user);
				psSub.setString(3, fromAcc);
				psSub.setDouble(4, amount);

				if (psSub.executeUpdate() > 0) {
					// Add money
					String addSQL = "UPDATE accounts SET balance = balance + ? WHERE owner_name = ? AND account_type = ?";
					PreparedStatement psAdd = conn.prepareStatement(addSQL);
					psAdd.setDouble(1, amount);
					psAdd.setString(2, recipient);
					psAdd.setString(3, toAcc);

					if (psAdd.executeUpdate() == 0) {
						conn.rollback();
						response.sendRedirect("bank?action=transfer&msg=user_not_found&transferType=" + type);
						return;
					}

					// Log Transaction
					String logSQL = "INSERT INTO transactions (type, amount, transaction_date, account_id) "
							+ "VALUES (?, ?, NOW(), (SELECT id FROM accounts WHERE owner_name = ? AND account_type = ?))";
					PreparedStatement psLog = conn.prepareStatement(logSQL);
					psLog.setString(1, "TRANSFER TO: " + recipient + " (" + toAcc + ")");
					psLog.setDouble(2, amount);
					psLog.setString(3, user);
					psLog.setString(4, fromAcc);
					psLog.executeUpdate();

					conn.commit();
					String encodedRecipient = java.net.URLEncoder.encode(recipient, "UTF-8");
					response.sendRedirect(
							"bank?action=dashboard&success=transfer&to=" + encodedRecipient + "&amt=" + amount);
				} else {
					conn.rollback();
					response.sendRedirect("bank?action=transfer&msg=low_funds");
				}
			} catch (Exception e) {
				conn.rollback();
				throw e;
			}
		} catch (Exception e) {
			e.printStackTrace();
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
