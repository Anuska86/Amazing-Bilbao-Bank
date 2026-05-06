package bank.models;

import jakarta.persistence.*;
import lombok.*;
import java.sql.Timestamp;


@Entity
@Table(name="transactions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class Transaction {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	
	private String type;
	private double amount;
	
	@Column(name="transaction_date")
	private Timestamp date;
	
	@ManyToOne
	@JoinColumn(name = "account_id")
	private Account account; 



}
