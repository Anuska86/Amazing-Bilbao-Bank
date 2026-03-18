package basic;

import java.util.ArrayList;

public class HelloWorld {

	public static void main(String[] args) {

		ArrayList<Account> myBank = new ArrayList<>();

		myBank.add(new Account("Anuska", 1000.0));
		myBank.add(new SavingsAccount("Jon", 2000.0, 5.0));
		myBank.add(new Account("Lauren", 1300.0));
		myBank.add(new Account("Ione", 1200.0));

		for (Account acc : myBank) {
			acc.withdraw(100.0);
		}

		System.out.println(myBank);

		myBank.remove(3);

		int total = myBank.size();

		System.out.println(total);
	}
}
