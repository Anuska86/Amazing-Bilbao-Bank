package basic;

public class HelloWorld {

	public static void main(String[] args) {

		// creating an account Instance

		Account myAccount = new Account("Anuska", 1500.50);

		System.out.println(myAccount);

		myAccount.deposit(500.0);
		System.out.println("New balance after deposit: " + myAccount.getBalanceWithCurrency());

		myAccount.withdraw(150.0);
		System.out.println("Final balance: " + myAccount.getBalanceWithCurrency());

		// creating a second account

		Account friendAccount = new Account("Jon", 5300.30);

		System.out.println("The balance in the friend account is: " + friendAccount.getBalanceWithCurrency());

		// Jon sends money to Anuska

		myAccount.transfer(300, myAccount);

		System.out.println(myAccount);
		System.out.println(friendAccount);

	}

}
