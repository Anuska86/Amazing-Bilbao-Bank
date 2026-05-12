package com.bank.web;

import java.io.IOException;


import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.hibernate.Session;

import bank.db.AccountDAO;
import bank.db.HibernateUtil;
import bank.models.Account;
import bank.models.CheckingAccount;
import bank.models.FixedTermDeposit;
import bank.models.SavingsAccount;
import bank.models.Transaction;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

/**
 * Servlet implementation class MainController
 * Handles all banking operations including dashboard, transfers, and account management.
 */

@Path("/bank")
@Produces(MediaType.TEXT_HTML)
public class MainController {
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
	 * 
     * Handles HTTP GET requests by routing to specific view-rendering methods.
     * @param request  The HttpServletRequest object containing client request data.
     * @param response The HttpServletResponse object for sending the view to the client.
     * @throws ServletException If a servlet-specific error occurs.
     * @throws IOException If an input or output error occurs during routing.
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

	
	/**
     * Calculates and applies monthly interest to all accounts owned by the session user.
     * Uses system_config to ensure interest is only applied once per month.
     * @param request  The HttpServletRequest containing the user session.
     * @param response The HttpServletResponse for redirection with success/error messages.
     * @throws IOException If a redirection error occurs.
     */
	
	// Apply Interest method

	private void applyInterest(HttpServletRequest request, HttpServletResponse response) throws IOException {

		String sessionUser = (String) request.getSession().getAttribute("user");

		try (Session session = HibernateUtil.getSessionFactory().openSession()) {
			org.hibernate.Transaction tx = session.beginTransaction();

			// Check it the interest it has been applied this month

			java.sql.Timestamp lastDate = (java.sql.Timestamp) session.createNativeQuery(
	                "SELECT last_interest_date FROM system_config WHERE id = 1", java.sql.Timestamp.class)
	                .uniqueResult();


				if (lastDate != null) {
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

				
				//Fetch all the accounts for the user
				
				List<Account> accounts = session.createQuery("FROM Account WHERE owner = :user", Account.class)
		                .setParameter("user", sessionUser)
		                .list();
				
				//Loop and apply interest
				
				for(Account acc: accounts) {
					acc.applyInterest();
					session.merge(acc);
				}
				
				//Update the interest rate
				
				session.createNativeQuery("UPDATE system_config SET last_interest_date = NOW() WHERE id = 1", Object.class)
	               .executeUpdate();

	        tx.commit();
	        response.sendRedirect("bank?action=dashboard&msg=success_interest&count=" + accounts.size());
	
		} catch (Exception e) {
			System.out.println("❌ Interest Error: " + e.getMessage());
			e.printStackTrace();
			response.sendRedirect("bank?action=dashboard&msg=error");
		}

	}

	
	/**
     * Fetches and displays the transaction history for a specific account.
     * Segregates transactions into internal (interest/self-transfers) and external categories.
     * @param request  Request containing 'accountId' parameter.
     * @param response Response object to forward to the history JSP.
     * @throws IOException      If forwarding fails.
     * @throws ServletException If a servlet error occurs.
     */
	
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

			if (account != null) {
				List<Transaction> allTransactions = account.getTransactions();

				for (Transaction trans : allTransactions) {
					String desc = trans.getType();

					if (desc.contains("INTEREST PAYMENT") || desc.contains("TRANSFER TO: " + sessionUser)
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

		try (Session session = HibernateUtil.getSessionFactory().openSession()) {

			List<Account> accountList = session.createQuery("FROM Account WHERE owner = :user", Account.class)
					.setParameter("user", sessionUser).list();

			request.setAttribute("user", sessionUser);
			request.setAttribute("accounts", accountList);

		} catch (Exception e) {
			System.out.println("❌ Transfer Form Error: " + e.getMessage());
			e.printStackTrace();
		}

		request.getRequestDispatcher("/WEB-INF/transfer.jsp").forward(request, response);

	}

	
	/**
     * Prepares and displays the main dashboard with all accounts belonging to the user.
     * @param request  Request containing the session user.
     * @param response Response object to forward to dashboard.jsp.
     * @throws ServletException If forwarding fails.
     * @throws IOException      If an I/O error occurs.
     */
	
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
					.setParameter("user", sessionUser).list();

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

			if (acc != null) {
				request.setAttribute("account", acc);
			}

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

		String password = request.getParameter("password");

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
			newAcc = SavingsAccount.builder().owner(sessionUser).balance(deposit).iban(iban).password(password)
					.interestRate(2.0).build();
		} else if ("FIXED_TERM_DEPOSIT".equalsIgnoreCase(type) || "FIXED-TERM DEPOSIT".equalsIgnoreCase(type)) {
			newAcc = FixedTermDeposit.builder().owner(sessionUser).balance(deposit).iban(iban).password(password)
					.build();
		} else {
			newAcc = CheckingAccount.builder().owner(sessionUser).balance(deposit).iban(iban).password(password)
					.build();
		}

		// VALIDATION

		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		Validator validator = factory.getValidator();
		Set<ConstraintViolation<Account>> violations = validator.validate(newAcc);

		if (!violations.isEmpty()) {
			String message = violations.iterator().next().getMessage();
			response.sendRedirect("bank?action=createAccount&msg=" + java.net.URLEncoder.encode(message, "UTF-8"));
			;
		}

		AccountDAO accountDAO = new AccountDAO();
		boolean success = accountDAO.insertAccount(newAcc);

		if (success) {
			response.sendRedirect("bank?action=dashboard&msg=account_created");
		} else {
			response.sendRedirect("bank?action=dashboard&msg=error");
		}

	}

	
	/**
     * Executes funds transfer between accounts using Hibernate transactions.
     * Supports both internal (own accounts) and external (via IBAN) transfers.
     * @param request  Contains 'fromAccount', 'amount', 'transferType', and 'recipientIBAN'.
     * @param response Redirects to dashboard on success or transfer form on failure.
     * @throws IOException If redirection fails.
     */
	
	// Handle Transfer

	private void handleTransfer(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String user = (String) request.getSession().getAttribute("user");
		String fromAccType = request.getParameter("fromAccount");
		double amount = Double.parseDouble(request.getParameter("amount"));
		String transferType = request.getParameter("transferType"); // internal or external

		// Connection

		try (Session session = HibernateUtil.getSessionFactory().openSession()) {
			org.hibernate.Transaction tx = session.beginTransaction();

			// Source Account

			Account fromAccount = session
					.createQuery("FROM Account WHERE owner = :user AND account_type = :type", Account.class)
					.setParameter("user", user).setParameter("type", fromAccType).uniqueResult();

			// Recipient

			Account toAccount = null;

			if ("external".equals(transferType)) {
				String targetIban = request.getParameter("recipientIBAN");
				toAccount = session.createQuery("FROM Account WHERE iban = :iban", Account.class)
						.setParameter("iban", targetIban).uniqueResult();
			} else {
				// Internal

				String toType = request.getParameter("toAccountInternal");
				toAccount = session.createQuery("FROM Account WHERE owner = :user AND type = :type", Account.class)
						.setParameter("user", user).setParameter("type", toType).uniqueResult();
			}

			// Validate & Execution

			if (fromAccount != null && toAccount != null && fromAccount.getBalance() >= amount) {
				fromAccount.transfer(amount, toAccount);

				session.merge(fromAccount);
				session.merge(toAccount);

				tx.commit();
				response.sendRedirect("bank?action=dashboard&success=transfer");
			} else {
				if (tx != null)
					tx.rollback();
				String errorMsg = (toAccount == null) ? "user_not_found" : "low_funds";
				response.sendRedirect("bank?action=transfer&msg=" + errorMsg);
			}

		} catch (Exception e) {
			System.out.println("❌ Transfer Error: " + e.getMessage());
			e.printStackTrace();
			response.sendRedirect("bank?action=transfer&msg=error");
		}

	}

	
	/**
     * Authenticates the user by checking the username and password against the database.
     * @param request  Contains 'username' and 'password' parameters.
     * @param response Redirects to dashboard on success or returns an alert on failure.
     * @throws ServletException If authentication logic fails.
     * @throws IOException If redirection or writer access fails.
     */
	
	
	// Login

	private void handleLogin(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		String user = request.getParameter("username");
		String password = request.getParameter("password");

		try (Session session = HibernateUtil.getSessionFactory().openSession()) {

			List<Account> accounts = session
					.createQuery("FROM Account WHERE owner = :user AND password = :password", Account.class)
					.setParameter("user", user).setParameter("password", password)
					.list();

			if (!accounts.isEmpty()) {
				request.getSession().setAttribute("user", user);
				response.sendRedirect("bank?action=dashboard");
			} else {
				response.getWriter().print("<script>alert('Invalid Login!'); window.location='index.html';</script>");
			}

		} catch (Exception e) {
			e.printStackTrace();
			response.getWriter().println("Database error: " + e.getMessage());
		}
	}

}
