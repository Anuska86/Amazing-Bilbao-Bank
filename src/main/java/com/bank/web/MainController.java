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

	static {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

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
		String accountId = request.getParameter("accountId");
		String sessionUser = (String) request.getSession().getAttribute("user");

		List<Transaction> internalTrans = new ArrayList<>();
		List<Transaction> externalTrans = new ArrayList<>();
		String dbPassword = System.getenv("DB_PASSWORD");

		try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/amazing_bilbao_bank", "root",
				dbPassword);

				PreparedStatement st = conn.prepareStatement(
						"SELECT * FROM transactions WHERE account_id = ? ORDER BY transaction_date DESC")) {

			st.setInt(1, Integer.parseInt(accountId));

			try (ResultSet rs = st.executeQuery()) {
				while (rs.next()) {
					Transaction trans = new Transaction(rs.getInt("id"), rs.getString("type"), rs.getDouble("amount"),
							rs.getTimestamp("transaction_date"), rs.getInt("account_id"));

					String desc = trans.getType();
					// Categorize by checking if the session user is the one mentioned in the
					// description
					if (desc.contains("TRANSFER TO: " + sessionUser)
							|| desc.contains("TRANSFER FROM: " + sessionUser)) {
						internalTrans.add(trans);
					} else {
						externalTrans.add(trans);
					}
				}
			}
		} catch (Exception e) {
			System.out.println("❌ Show History Error: " + e.getMessage());
			e.printStackTrace();
		}

		request.setAttribute("internalTransactions", internalTrans);
		request.setAttribute("externalTransactions", externalTrans);
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

		// Data

		List<Account> accountList = new ArrayList<>();
		String dbPassword = System.getenv("DB_PASSWORD");
		String sql = "SELECT id, balance,iban, account_type, owner_name FROM accounts WHERE owner_name = ?";

		try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/amazing_bilbao_bank", "root",
				dbPassword);) {

			PreparedStatement st = conn.prepareStatement(sql);
			st.setString(1, sessionUser);

			try (ResultSet rs = st.executeQuery();) {
				while (rs.next()) {
					int id = rs.getInt("id");
					double balance = rs.getDouble("balance");
					String iban = rs.getString("iban");
					String owner = rs.getString("owner_name");
					String rawType = rs.getString("account_type").toUpperCase().replace("-", "_").replace(" ", "_");

					AccountType type = AccountType.valueOf(rawType);
					Account acc = null;

					if (type == AccountType.SAVINGS) {
						acc = new SavingsAccount(id, owner, balance, iban, 0, "");
					} else if (type == AccountType.FIXED_TERM_DEPOSIT) {
						acc = new FixedTermDeposit(id, owner, balance, iban, "");
					} else {
						acc = new CheckingAccount(id, owner, balance, iban, "");
					}

					accountList.add(acc);

				}

			}

		} catch (Exception e) {
			System.out.println("❌ Transfer Form Error: " + e.getMessage());
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
		String sql = "SELECT id, balance,iban, account_type, owner_name FROM accounts WHERE owner_name = ?";

		try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/amazing_bilbao_bank", "root",
				dbPassword);) {

			PreparedStatement st = conn.prepareStatement(sql);
			st.setString(1, sessionUser);

			try (ResultSet rs = st.executeQuery()) {
				while (rs.next()) {
					int id = rs.getInt("id");
					double balance = rs.getDouble("balance");
					String iban = rs.getString("iban");
					String owner = rs.getString("owner_name");
					String rawType = rs.getString("account_type").toUpperCase().replace("-", "_").replace(" ", "_");

					AccountType type = AccountType.valueOf(rawType);
					Account acc;

					if (type == AccountType.SAVINGS) {
						acc = new SavingsAccount(id, owner, balance, iban, 0, "");
					} else if (type == AccountType.FIXED_TERM_DEPOSIT) {
						acc = new FixedTermDeposit(id, owner, balance, iban, "");
					} else {
						acc = new CheckingAccount(id, owner, balance, iban, "");
					}

					accountList.add(acc);

				}
			}

		} catch (Exception e) {
			System.out.println("❌ Show Dashboard Error: " + e.getMessage());
			e.printStackTrace();
		}

		request.setAttribute("user", sessionUser);
		request.setAttribute("accounts", accountList);
		request.getRequestDispatcher("/WEB-INF/dashboard.jsp").forward(request, response);
	}

	// SHOW ACCOUNT DETAILS

	private void showDetails(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String accountId = request.getParameter("accountId");
		String dbPassword = System.getenv("DB_PASSWORD");
		String sql = "SELECT id, balance, iban, owner_name, co_owner_name, account_type FROM accounts WHERE id = ?";

		if (accountId == null) {
			response.sendRedirect("index.html");
			return;
		}

		try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/amazing_bilbao_bank", "root",
				dbPassword);) {

			PreparedStatement st = conn.prepareStatement(sql);
			st.setInt(1, Integer.parseInt(accountId));

			try (ResultSet rs = st.executeQuery();) {
				if (rs.next()) {
					request.setAttribute("accountId", rs.getInt("id"));
					request.setAttribute("balance", rs.getDouble("balance"));
					request.setAttribute("iban", rs.getString("iban"));
					request.setAttribute("titular", rs.getString("owner_name"));
					request.setAttribute("cotitular", rs.getString("co_owner_name"));
					request.setAttribute("accountName", rs.getString("account_type"));
				}

			}

		} catch (Exception e) {
			System.out.println("❌ Show Details Error: " + e.getMessage());
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

					// Log for the SENDER (The "From" Account) - negative amount
					String logSenderSQL = "INSERT INTO transactions (type, amount, transaction_date, account_id) "
							+ "VALUES (?, ?, NOW(), (SELECT id FROM accounts WHERE owner_name = ? AND account_type = ?))";
					PreparedStatement psLogSender = conn.prepareStatement(logSenderSQL);
					psLogSender.setString(1, "TRANSFER TO: " + recipient + " (" + toAcc + ")");
					psLogSender.setDouble(2, -amount); // Notice the minus sign!
					psLogSender.setString(3, user);
					psLogSender.setString(4, fromAcc);
					psLogSender.executeUpdate();

					// Log for the RECIPIENT (The "To" Account) - positive amount
					String logRecipientSQL = "INSERT INTO transactions (type, amount, transaction_date, account_id) "
							+ "VALUES (?, ?, NOW(), (SELECT id FROM accounts WHERE owner_name = ? AND account_type = ?))";
					PreparedStatement psLogRec = conn.prepareStatement(logRecipientSQL);
					psLogRec.setString(1, "TRANSFER FROM: " + user + " (" + fromAcc + ")");
					psLogRec.setDouble(2, amount); // Positive amount
					psLogRec.setString(3, recipient);
					psLogRec.setString(4, toAcc);
					psLogRec.executeUpdate();

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
			System.out.println("❌ Handle Transfer Error: " + e.getMessage());
			e.printStackTrace();
		}
	}

	// Login

	private void handleLogin(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		String user = request.getParameter("username");
		String pass = request.getParameter("password");
		String dbPassword = System.getenv("DB_PASSWORD");

		String sql = "SELECT * FROM accounts WHERE owner_name = ? AND password = ?";

		try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/amazing_bilbao_bank", "root",
				dbPassword);) {

			PreparedStatement st = conn.prepareStatement(sql);
			st.setString(1, user);
			st.setString(2, pass);

			try (ResultSet rs = st.executeQuery();) {
				if (rs.next()) {
					request.getSession().setAttribute("user", user);
					response.sendRedirect("bank?action=dashboard");

				} else {

					response.getWriter()
							.println("<script>alert('Invalid Login!'); window.location='index.html';</script>");
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			response.getWriter().println("Database error: " + e.getMessage());
		}
	}

}
