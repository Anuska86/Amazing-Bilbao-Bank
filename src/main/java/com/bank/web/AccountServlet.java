package com.bank.web;

import java.io.IOException;

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

		out.println("<html><body style='font-family: sans-serif; text-align: center;'>");
		out.println("<h1 style='color: #2c3e50;'>🏦 Bank Account Dashboard</h1>");
		out.println("<hr>");
		out.println("<p><strong>Account Holder:</strong>" + myAcc.getOwner() + "</p>");
		out.println("<p><strong>Current Balance:</strong> <span style='color: green;'>$" + myAcc.getBalance()
				+ "</span> </p>");
		out.println("<a href='index.html'>Back to Home</a>");
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
