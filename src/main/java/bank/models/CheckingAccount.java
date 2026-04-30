package bank.models;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)

public class CheckingAccount extends Account {

	@Override
	public void printMonthlyReport() {
		System.out.println("--- CHECKING ACCOUNT REPORT ---");
		System.out.println("Owner: " + getOwner());
		System.out.println("Balance: " + getBalanceWithCurrency());
		System.out.println("IBAN: " + getIban());
		System.out.println("Status: Standard (No Interest)");
		System.out.println("-------------------------------");

	}

	@Override

	public double getInterestRate() {
		return 0.0;
	}

}
