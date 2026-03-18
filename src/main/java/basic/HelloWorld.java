package basic;

public class HelloWorld {

	public static void main(String[] args) {

		SavingsAccount mySavings = new SavingsAccount("Anuska", 1000.0, 5.0);

		mySavings.deposit(500.0);

		mySavings.applyInterest();

		System.out.println(mySavings);

	}
}
