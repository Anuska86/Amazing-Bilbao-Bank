package bank.models;



import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * Represents a generic bank account.
 * This is the base class for specialized accounts like Savings and Checking.
 * Uses Hibernate Single Table inheritance strategy.
 */

@Data
@SuperBuilder
@NoArgsConstructor

@Entity
@Table(name = "accounts")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "account_type")




public abstract class Account {
	
	
	

	// Variables (Attributes)

	/** Unique database identifier for the account. */
	
	@Id 
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	
	/** Full name of the primary account holder. */
	
	@NotBlank(message = "Owner name is required")
	@Column(name = "owner_name")
	private String owner;
	
	@Column(name = "co_owner_name")
	private String coOwner;
	
	/** The current amount of money available in the account. */
	
	@PositiveOrZero(message = "Balance cannot be negative")
	private double balance;
	
	/** International Bank Account Number (Unique). */
	
	@NotBlank(message ="IBAN number is required")
	private String iban;
	
	
	/** Encrypted or plain-text password for account access. */
	
	@NotBlank(message = "Password is required")
	@Size(min = 6, max = 60, message = "Password must be between 6 and 60 characters")
	@Pattern(regexp = "^(?=.*[0-9])(?=.*[A-Z])(?=.*[!@#$%^&*])(?=\\S+$).+$",
		    message = "Password must be at least 6 characters, contain one number, one uppercase letter, and one special character (!@#$%^&*)")
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

	
	
	/**
     * Increases the account balance and logs a DEPOSIT transaction.
     * @param amount The sum of money to add. Must be greater than zero.
     * @return true if the deposit was successful, false otherwise.
     */

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

	/**
     * Decreases the account balance and logs a WITHDRAWAL transaction.
     * Note: FixedTermDeposit accounts will always return false.
     * @param amount The sum of money to remove.
     * @return true if funds were sufficient and account type allows withdrawal.
     */

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

	/**
     * Moves money from this account to another.
     * This is an atomic operation involving a withdrawal and a deposit.
     * @param amount The sum to transfer.
     * @param destinationAccount The recipient Account object.
     */
	
	public void transfer(double amount, Account destinationAccount) {
		if (amount <= balance) {
			this.withdraw(amount);
			destinationAccount.deposit(amount);
			System.out.println("Transfer successful!");
		} else {
			System.out.println("Transfer failed, put in contact with your bank ");
		}
	}
	
	
	/**
     * Calculates interest based on the specific account type's rate 
     * and deposits it into the balance.
     */
	
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
