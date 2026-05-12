/**
 * 
 */
package bank.config;

import jakarta.ws.rs.ApplicationPath;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.mvc.jsp.JspMvcFeature;


/**
 * 
 */
@ApplicationPath("/")
public class JerseyConfig extends ResourceConfig{

	public JerseyConfig() {
		packages("com.bank.web"); 
		
		register(JspMvcFeature.class);
		
		property(JspMvcFeature.TEMPLATE_BASE_PATH, "/WEB-INF/");
	}
	
	
}
