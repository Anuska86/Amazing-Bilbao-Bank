package basic;

public class Bank {
	private String bankName;
	private Account account1;
	private Account account2;

//Constructor

	public Bank(String name) {
		this.bankName = name;
	}

//Method open accounts

	public void addAccounts(Account acc1, Account acc2) {
		this.account1 = acc1;
		this.account2 = acc2;
	}

	public void showStatus() {
		System.out.println("--- Welcome to " + bankName + "---");
		System.out.println(account1);
		System.out.println(account2);
		System.out.println("-------------------------------------");
	}

}
