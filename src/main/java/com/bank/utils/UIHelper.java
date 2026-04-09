package com.bank.utils;

import java.io.PrintWriter;

public class UIHelper {

	public static void printHeader(PrintWriter out, String title, String username, String path) {

		out.println("<!DOCTYPE html><html><head>");
		out.println("<title>" + title + " - Amazing Bilbao Bank</title>");
		out.println("<link rel='stylesheet' type='text/css' href='" + path + "/styles/index.css'>");
		out.println("</head><body>");

		out.println("<header class='main-header'>");
		out.println("  <div class='nav-container'>");
		out.println("    <span class='logo'>🏦 Amazing Bilbao Bank</span>");
		out.println("    <nav>");
		out.println("      <a href='account'>Dashboard</a>");
		out.println("      <a href='transfer'>Transfer</a>");
		out.println("      <a href='logout'>Logout</a>");
		out.println("    </nav>");
		out.println("    <span class='user-tag'>Welcome, " + username + "</span>");
		out.println("  </div>");
		out.println("</header>");
		out.println("<main class='content-wrapper'>");

	}

	public static void printFooter(PrintWriter out) {
		out.println("</main>");
		out.println("<footer class='main-footer'>");
		out.println("  <p>&copy; 2026 Amazing Bilbao Bank. Authorized by the Bank of Spain.</p>");
		out.println("</footer>");
		out.println("</body></html>");
	}

}
