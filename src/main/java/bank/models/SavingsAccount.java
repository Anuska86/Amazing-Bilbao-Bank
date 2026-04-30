package bank.models;

import bank.logic.InterestBearing;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)

public class SavingsAccount extends Account implements InterestBearing {

	@Builder.Default
	private double interestRate;

	// OVERRIDES

	// Interface method

	@Override

	public void applyInterest() {

		double interest = getBalance() * (interestRate / 100);
		deposit(interest);
		System.out.println("Interest applied: " + interest + "€");

	}

	// Abstract method

	@Override

	public void printMonthlyReport() {
		System.out.println("--- SAVINGS ACCOUNT REPORT ---");
		System.out.println("Owner: " + getOwner());
		System.out.println("Current Balance: " + getBalanceWithCurrency());
		System.out.println("IBAN: " + getIban());
		System.out.println("Interest Rate: " + interestRate + "%");
		System.out.println("------------------------------");
	}

	// Standard method

	@Override

	public double getInterestRate() {
		return this.interestRate;
	}

	@Override
	public boolean withdraw(double amount) {
		double totalWithFee = amount + 2.0;

		if (totalWithFee <= getBalance()) {
			boolean success = super.withdraw(totalWithFee);

			if (success) {
				transactionHistory.add("Savings Fee applied: 2.0€");
				System.out.println("Note: A 2€ saving fee was applied");
			}
			return success;
		} else {

			return false;
		}
	}

	@Override
	public String toString() {
		return super.toString() + "| Interest Rate: " + interestRate + "%";

	}

}
