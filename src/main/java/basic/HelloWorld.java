package basic;

import java.util.Arrays;

public class HelloWorld {

	public static void main(String[] args) {

		Account[] myBankAccounts = new Account[2];

		myBankAccounts[0] = new Account("Anuska", 1000.0);
		myBankAccounts[1] = new SavingsAccount("Jon", 2000.0, 5.0);

		for (Account acc : myBankAccounts) {
			acc.withdraw(100.0);
		}

		System.out.println(Arrays.toString(myBankAccounts));

	}
}
