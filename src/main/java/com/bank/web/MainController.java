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

		default:
			response.sendRedirect("index.html");
			break;
		}

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
			st.setString(2, rawType.toLowerCase().replace("_", "-"));

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
		String toAcc = request.getParameter("toAccount");
		String amountRaw = request.getParameter("amount");

		if (user == null || amountRaw == null) {
			response.sendRedirect("index.html");
			return;
		}

		double amount = Double.parseDouble(amountRaw);

		// Stop transfering to the same account

		if (fromAcc.equals(toAcc)) {
			response.sendRedirect("bank?action=transfer&msg=same_account");
			return;
		}

		String dbPassword = System.getenv("DB_PASSWORD");

		try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/amazing_bilbao_bank", "root",
				dbPassword)) {

			conn.setAutoCommit(false);

			try {

				String subSQL = "UPDATE accounts SET balance = balance - ? WHERE owner_name = ? AND account_type = ? AND balance >= ?";
				PreparedStatement psSub = conn.prepareStatement(subSQL);
				psSub.setDouble(1, amount);
				psSub.setString(2, user);
				psSub.setString(3, fromAcc);
				psSub.setDouble(4, amount);

				int rows = psSub.executeUpdate();

				if (rows > 0) {

					// Add the money

					String addSQL = "UPDATE accounts SET balance = balance + ? WHERE owner_name = ? AND account_type = ?";
					PreparedStatement psAdd = conn.prepareStatement(addSQL);
					psAdd.setDouble(1, amount);
					psAdd.setString(2, user);
					psAdd.setString(3, toAcc);
					psAdd.executeUpdate();

					conn.commit();
					response.sendRedirect("bank?action=dashboard&success=transfer");

				} else {

					// FAIL (not enough money)

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
