package bank.models;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)

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
