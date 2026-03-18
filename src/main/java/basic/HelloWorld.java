package basic;

public class HelloWorld {

	public static void main(String[] args) {

		Account regularAccount = new Account("Regular User", 1500.0);
		regularAccount.withdraw(500.0);
		System.out.println(regularAccount);

		SavingsAccount mySavingsAccount = new SavingsAccount("Anuska", 1000.0, 5.0);
		mySavingsAccount.withdraw(200.0);
		System.out.println(mySavingsAccount);

	}
}
