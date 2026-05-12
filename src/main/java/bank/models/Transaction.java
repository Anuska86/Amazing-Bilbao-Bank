package bank.models;

import jakarta.persistence.*;
import lombok.*;
import java.sql.Timestamp;

/**
 * Represents a single financial record associated with an Account.
 * This entity stores the audit trail for deposits, withdrawals, transfers, and interest payments.
 */


@Entity
@Table(name="transactions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class Transaction {
	
	/** Unique identifier for the transaction record. */
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	
	/** * The category of the transaction.
     * Examples: "DEPOSIT", "WITHDRAWAL", "INTEREST PAYMENT", "TRANSFER TO: [User]".
     */
	
	private String type;
	
	/** * The monetary value of the transaction. 
     * Typically negative for withdrawals/outgoing transfers and positive for deposits/interest.
     */
	
	private double amount;
	
	/** The exact date and time when the transaction was executed. */
	
	@Column(name="transaction_date")
	private Timestamp date;
	
	
	/** * The account to which this transaction belongs.
     * Establishes a Many-to-One relationship with the Account entity.
     */
	
	@ManyToOne
	@JoinColumn(name = "account_id")
	private Account account; 



}
