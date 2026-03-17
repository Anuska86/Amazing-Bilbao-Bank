package basic;

import java.util.Date;

public class HelloWorld {

	public static void main(String[] args) {

		// hello + date

		Date date = new Date();

		System.out.println("Hello world! Today is " + date);

		// creating an account Instance

		Account myAccount = new Account();

		myAccount.setOwner("Anuska");
		myAccount.setBalance(1500.50);

		System.out.println("Owner:" + myAccount.getOwner());
		System.out.println("Balance: " + myAccount.getBalance() + "€");

	}

}
