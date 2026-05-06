package bank.models;



import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor

@Entity
@Table(name = "accounts")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "account_type")




public abstract class Account {
	
	
	

	// Variables (Attributes)

	@Id 
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	
	@Column(name = "owner_name")
	private String owner;
	
	private double balance;
	private String iban;
	private String password;
	
	@Column(name = "interestRate")
	protected Double interestRate; 

	
	@OneToMany(mappedBy = "account", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@Builder.Default
	protected List<Transaction> transactions = new ArrayList<>();
	


	// Methods (Actions)

	// Abstract Methods

	public abstract void printMonthlyReport();

	// Other Methods

	// Method String (account data)
	public String toString() {
		return "Account Owner: " + owner + " | Balance " + balance + "€";
	}

	// Method to do a deposit

	public boolean deposit(double amount) {
		if (amount <= 0) {
			System.out.println("❌ Error: Deposit amount must be positive.");
			return false;
		}
		this.balance += amount;
		
		
		this.transactions.add(Transaction.builder()
				.type("DEPOSIT")
				.amount(amount)
				.date(new java.sql.Timestamp(System.currentTimeMillis()))
				.account(this)
				.build());
		
		
		return true;
	}

	// Method to do a withdraw

	public boolean withdraw(double amount) {

		if (this instanceof FixedTermDeposit) {
			System.out
					.println("❌ ERROR: Access Denied. Fixed-Term Deposits cannot be withdrawn until the term expires!");
			return false;
		}

		if (amount > 0 && amount <= balance) {
			balance -= amount;
		
			this.transactions.add(Transaction.builder()
		            .type("WITHDRAWAL")
		            .amount(-amount) // Negative 
		            .date(new java.sql.Timestamp(System.currentTimeMillis()))
		            .account(this)
		            .build());
			
			return true;
		} else {
			System.out.println("FAILED Withdrawal: " + amount + "€ (Insufficient funds)");
			return false;
		}
	}

	// Method to do a transfer
	public void transfer(double amount, Account destinationAccount) {
		if (amount <= balance) {
			this.withdraw(amount);
			destinationAccount.deposit(amount);
			System.out.println("Transfer successful!");
		} else {
			System.out.println("Transfer failed, put in contact with your bank ");
		}
	}
	
	//Method to apply interest
	
	public void applyInterest() {
		if(getInterestRate() !=null && getInterestRate()>0) {
			double interest = getBalance()*(getInterestRate()/100);
			deposit(interest);
			System.out.println("Interest applied: " + interest + "€");
		}
	}

	// Method to print the account history

	public void printHistory() {
		System.out.println("--- Transaction History for " + owner + " ---");
		
		for (Transaction t : transactions) {
			System.out.printf("- [%s] %-20s : %.2f€%n", 
		            t.getDate(), 
		            t.getType(), 
		            t.getAmount());
		}
		System.out.printf("Current Balance: %.2f€%n", balance);
	}

	// Method to default interest rate

	public Double getInterestRate() {
		return (this.interestRate != null) ? this.interestRate : 0.0;
	}

	// Method to verify the password

	public boolean verifyPassword(String input) {
		if (input == null)
			return false;

		return this.password.equals(input);
	}

	// Method to set the password

	public void setPassword(String newPassword) {
		if (newPassword != null && !newPassword.trim().isEmpty()) {
			this.password = newPassword;
			System.out.println("✅ Password updated in memory.");
		}
	}

	

	// GET Balance with currency
	public String getBalanceWithCurrency() {
		return balance + "€";
	}
	
	//GET transactions
	
	public List<Transaction> getTransactions() {
	    return transactions;
	}

	// Method to display the name of the account type nicely

	public String getDisplayName() {
		String accTypeName = this.getClass().getSimpleName();

		if (accTypeName.equals("FixedTermDeposit"))
			return "Fixed-Term Deposit";

		return accTypeName.replace("Account", "") + " Account";
	}

	// GET Account Type

	public String getType() {

		return this.getClass().getSimpleName().replace("Account", "").toUpperCase();
	}


}
