package bank.db;

import bank.models.Account;


import org.hibernate.Session;
import java.util.List;

public class TestHibernate {

	public static void main(String[] args) {
		
		try (Session session = HibernateUtil.getSessionFactory().openSession() ){
			
			session.beginTransaction();
			
			List<Account> accounts = session.createQuery("from Account ", Account.class).list();
			
			System.out.println("--- Bank Accounts Found in DB ---");
			
			for(Account acc : accounts) {
				System.out.println(acc.getOwner() + "[" + acc.getType() + "]: " +  acc.getBalance() + "€");
			}
			
			session.getTransaction().commit();
			
			
		} catch (Exception e) {
			e.printStackTrace();
			
		}

	}

}
