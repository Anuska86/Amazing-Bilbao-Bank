package bank.models;

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

@Entity
@DiscriminatorValue("FIXED_TERM_DEPOSIT")

public class FixedTermDeposit extends Account {

	@Override
	public void printMonthlyReport() {
		System.out.println("Monthly Report for Fixed-Term Deposit: " + getOwner());
	}

	@Override

	public double getInterestRate() {
		return 5.0;
	}
}
