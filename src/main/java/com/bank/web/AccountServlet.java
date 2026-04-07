package com.bank.web;

import java.io.IOException;
import java.text.NumberFormat;
import java.util.Locale;

import bank.models.Account;
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

		response.setContentType("text/html");

		java.io.PrintWriter out = response.getWriter();

		Account myAcc = new SavingsAccount(0, "Susazhina", 48000.0, 0, "K8454w$");

		// Balance format

		Locale spain = Locale.of("es", "ES");
		NumberFormat euroFormatter = NumberFormat.getCurrencyInstance(spain);
		String formattedBalance = euroFormatter.format(myAcc.getBalance());

		out.println("<html>");
		out.println("<head>");
		String path = request.getContextPath();
		out.println("<link rel='stylesheet' type='text/css' href='" + path + "/styles/index.css'>");
		out.println("</head>");

		out.println("<body>");
		out.println("  <div class='card'>");
		out.println("    <h1>🏦 Bank Dashboard</h1>");
		out.println("    <p>Account Holder: <strong>" + myAcc.getOwner() + "</strong></p>");
		out.println("<p class='balance'>" + formattedBalance + "</p>");
		out.println("    <a href='index.html' class='btn'>Back to Home</a>");
		out.println("  </div>");
		out.println("</body></html>");
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
