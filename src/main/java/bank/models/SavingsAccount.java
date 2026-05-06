package bank.models;

import bank.logic.InterestBearing;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)

@Entity //Registers the child class.
@DiscriminatorValue("SAVINGS") //Matches the text in account_type column.

public class SavingsAccount extends Account implements InterestBearing {

	

	// OVERRIDES

	// Interface method

	@Override

	public void applyInterest() {

		if (getInterestRate() != null && getInterestRate() > 0) {
	        double interest = getBalance() * (getInterestRate() / 100);
	        deposit(interest);
	        System.out.println("Interest applied: " + interest + "€");
	    }

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

	public Double getInterestRate() {
		return this.interestRate;
	}

	@Override
	public boolean withdraw(double amount) {
		double fee = 2.0;
		double totalWithFee = amount + 2.0;

		if (totalWithFee <= getBalance()) {
			boolean success = super.withdraw(totalWithFee);

			if (success) {
				
				// Subtract the fee from the balance
	            setBalance(getBalance() - fee);
				
	            this.getTransactions().add(Transaction.builder()
	                    .type("SAVINGS FEE")
	                    .amount(-fee)
	                    .date(new java.sql.Timestamp(System.currentTimeMillis()))
	                    .account(this)
	                    .build());
	            
				System.out.println("Note: A 2€ saving fee was applied");
			}
			return success;
			
		} else {
			System.out.println("❌ Savings Error: Insufficient funds for amount + 2€ fee.");
			return false;
		} 
	}

	@Override
	public String toString() {
		return super.toString() + "| Interest Rate: " + interestRate + "%";

	}

}
