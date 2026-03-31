package bank.db;

import bank.logic.Bank;
import bank.models.Account;

public class BankDatabaseApp {

	public static void main(String[] args) {
		Bank myBank = new Bank("Amazing Bilbao Bank");

		System.out.println("--- 🚀 DATABASE TEST MODE ---");

		Account jon = myBank.findAccount("Jon");

		if (jon != null) {

			System.out.println("✅ Found Jon in MySQL!");
			System.out.println("Current Balance: " + jon.getBalance() + "€");

			jon.deposit(500.0);
			System.out.println("New Balance in Java: " + jon.getBalance() + "€");

		} else {
			System.out.println("❌ Jon not found. Did you run addAccounts(new SavingsAccount(\"Jon\", 9800.0)) first?");
		}

	}

}
