package com.bank.web;

import java.io.IOException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.Session;

import bank.db.AccountDAO;
import bank.db.HibernateUtil;
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

	@Override
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

		case "applyInterest":
			applyInterest(request, response);
			break;

		case "createAccount":
			request.getRequestDispatcher("/WEB-INF/create-account.jsp").forward(request, response);
			break;

		default:
			response.sendRedirect("index.html");
			break;
		}

	}

	// Apply Interest method

	private void applyInterest(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String dbPassword = System.getenv("DB_PASSWORD");
		String sessionUser = (String) request.getSession().getAttribute("user");

		try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/amazing_bilbao_bank", "root",
				dbPassword)) {
			conn.setAutoCommit(false);

			// Check it the interest it has been applied this month

			String checkSQL = "SELECT last_interest_date FROM system_config WHERE id = 1";

			try (PreparedStatement psCheck = conn.prepareStatement(checkSQL); ResultSet rs = psCheck.executeQuery()) {

				if (rs.next()) {
					java.sql.Date lastDate = rs.getDate("last_interest_date");
					java.util.Calendar cal = java.util.Calendar.getInstance();
					int currentMonth = cal.get(java.util.Calendar.MONTH);
					int currentYear = cal.get(java.util.Calendar.YEAR);

					cal.setTime(lastDate);
					if (cal.get(java.util.Calendar.MONTH) == currentMonth
							&& cal.get(java.util.Calendar.YEAR) == currentYear) {

						// already done, error!
						response.sendRedirect("bank?action=dashboard&msg=already_applied");
						return;
					}
				}

			}

			try {

				// Update the balance

				String updateSQL = "UPDATE accounts SET balance = ROUND(CASE "
						+ "WHEN account_type = 'SAVINGS' THEN balance * 1.02 "
						+ "WHEN account_type = 'FIXED-TERM DEPOSIT' THEN balance * 1.05 "
						+ "ELSE balance * 1.001 END, 2) " + "WHERE owner_name = ?";

				PreparedStatement psUpdate = conn.prepareStatement(updateSQL);
				psUpdate.setString(1, sessionUser);
				int rowsUpdated = psUpdate.executeUpdate();

				// Log the transaction + calculate interest

				String logSQL = "INSERT INTO transactions (type, amount, transaction_date, account_id) "
						+ "SELECT CONCAT('INTEREST PAYMENT (', account_type, ')'), "
						+ "ROUND(CASE WHEN account_type = 'SAVINGS' THEN balance * 0.02 "
						+ "      WHEN account_type = 'FIXED-TERM DEPOSIT' THEN balance * 0.05 "
						+ "      ELSE balance * 0.001 END, 2), " + "NOW(), id FROM accounts WHERE owner_name = ?";

				PreparedStatement psLog = conn.prepareStatement(logSQL);
				psLog.setString(1, sessionUser);
				psLog.executeUpdate();

				// Save the last_interest_date
				String updateDateSQL = "UPDATE system_config SET last_interest_date = NOW() WHERE id = 1";

				try (PreparedStatement psDate = conn.prepareStatement(updateDateSQL)) {
					psDate.executeUpdate();
				}

				conn.commit(); // Save
				response.sendRedirect("bank?action=dashboard&msg=success_interest&count=" + rowsUpdated);

			} catch (Exception e) {
				conn.rollback();
				throw e;
			}
		} catch (Exception e) {
			System.out.println("❌ Interest Error: " + e.getMessage());
			e.printStackTrace();
			response.sendRedirect("bank?action=dashboard&msg=error");
		}

	}

	// Fetch history method

	private void showHistory(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		String accountIdStr = request.getParameter("accountId");
		String sessionUser = (String) request.getSession().getAttribute("user");

		List<Transaction> internalTrans = new ArrayList<>();
		List<Transaction> externalTrans = new ArrayList<>();
		

		try (Session session = HibernateUtil.getSessionFactory().openSession()) {
			int id = Integer.parseInt(accountIdStr);
			Account account = session.get(Account.class, id);
			
			if(account != null) {
				List<Transaction> allTransactions = account.getTransactions();
		
			
			for (Transaction trans : allTransactions) {
                String desc = trans.getType();
                
              
                if (desc.contains("INTEREST PAYMENT") || 
                    desc.contains("TRANSFER TO: " + sessionUser) || 
                    desc.contains("TRANSFER FROM: " + sessionUser)) {
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

		try (Session session = HibernateUtil.getSessionFactory().openSession()) {

			List<Account> accountList = session.createQuery("FROM Account WHERE owner = :user", Account.class)
					.setParameter("user", sessionUser)
	                .list();
			
			request.setAttribute("user", sessionUser);
	        request.setAttribute("accounts", accountList);

		} catch (Exception e) {
			System.out.println("❌ Transfer Form Error: " + e.getMessage());
			e.printStackTrace();
		}

		
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

	

		try (Session session = HibernateUtil.getSessionFactory().openSession()) {
			List<Account> accountList = session.createQuery("FROM Account WHERE owner = :user", Account.class)
	                .setParameter("user", sessionUser)
	                .list();

	        request.setAttribute("user", sessionUser);
	        request.setAttribute("accounts", accountList);

		} catch (Exception e) {
			System.out.println("❌ Show Dashboard Error: " + e.getMessage());
			e.printStackTrace();
		}

		
		request.getRequestDispatcher("/WEB-INF/dashboard.jsp").forward(request, response);
	}

	// SHOW ACCOUNT DETAILS

	private void showDetails(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String accountId = request.getParameter("accountId");
		

		if (accountId == null) {
			response.sendRedirect("index.html");
			return;
		}

		try (Session session = HibernateUtil.getSessionFactory().openSession()) {
			Account acc = session.get(Account.class, Integer.parseInt(accountId));
			
					
					if (acc != null) {request.setAttribute("account", acc);}
					
		} catch (Exception e) {
			System.out.println("❌ Show Details Error: " + e.getMessage());
			e.printStackTrace();

		}

		request.getRequestDispatcher("/WEB-INF/details.jsp").forward(request, response);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		String action = request.getParameter("action");

		if ("login".equals(action)) {
			handleLogin(request, response);
		} else if ("processTransfer".equals(action)) {
			handleTransfer(request, response);

		} else if ("processCreateAccount".equals(action)) {
			handleCreateAccount(request, response);
		}
	}

	// Create new account

	private void handleCreateAccount(HttpServletRequest request, HttpServletResponse response) throws IOException {

		String sessionUser = (String) request.getSession().getAttribute("user");
		String type = request.getParameter("accountType");
		String depositStr = request.getParameter("initialDeposit");

		double deposit = 0;

		try {
			deposit = Double.parseDouble(depositStr);
		} catch (Exception e) {
			response.sendRedirect("bank?action=dashboard&msg=error");
			return;
		}

		String iban = "ES" + (int) (Math.random() * 100000000);
		Account newAcc;

		if ("SAVINGS".equalsIgnoreCase(type)) {
			newAcc = SavingsAccount.builder().owner(sessionUser).balance(deposit).iban(iban).password("")
					.interestRate(2.0).build();
		} else if ("FIXED_TERM_DEPOSIT".equalsIgnoreCase(type) || "FIXED-TERM DEPOSIT".equalsIgnoreCase(type)) {
			newAcc = FixedTermDeposit.builder().owner(sessionUser).balance(deposit).iban(iban).password("").build();
		} else {
			newAcc = CheckingAccount.builder().owner(sessionUser).balance(deposit).iban(iban).password("").build();
		}

		AccountDAO accountDAO = new AccountDAO();
		boolean success = accountDAO.insertAccount(newAcc);

		if (success) {
			response.sendRedirect("bank?action=dashboard&msg=account_created");
		} else {
			response.sendRedirect("bank?action=dashboard&msg=error");
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
