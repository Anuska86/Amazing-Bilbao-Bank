package bank.db;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class HibernateUtil {
private static final SessionFactory sessionFactory = buildSessionFactory();

private static SessionFactory buildSessionFactory() {
	try {
		
		Configuration configuration = new Configuration();
				configuration.configure("hibernate.cfg.xml");
				
				//Grabing the env variable
				
				String dbPassword = System.getenv("DB_PASSWORD");
				
				if(dbPassword !=null) {
					configuration.setProperty("hibernate.connection.password", dbPassword);
				}
				
				return configuration.buildSessionFactory();
		
		
		
	} catch (Throwable ex) {
		System.err.println("Initial SessionFactory creation failed." + ex);
        throw new ExceptionInInitializerError(ex);
	}
}

public static SessionFactory getSessionFactory() {
	return sessionFactory; 
}

}
