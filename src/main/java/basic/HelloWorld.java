package basic;

import java.util.ArrayList;

public class HelloWorld {

	public static void main(String[] args) {

		ArrayList<InterestBearing> investments = new ArrayList<>();

		investments.add(new SavingsAccount("Jon", 2000, 5.0));

		for (InterestBearing item : investments) {
			item.applyInterest();
		}

	}
}
