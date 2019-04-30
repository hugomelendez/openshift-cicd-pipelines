package io.veicot.cloud;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

@Path("/")
public class HelloService {

	@GET
	@Produces("text/plain")
	public String doGet() {
		Properties properties = this.loadProperties("artifact.properties");

		return String.format("%s (Version: %s - Build Number: %s - Git Commit: %s - Environment: %s)", 
							 System.getenv("HELLO_MESSAGE"), 
							 properties.get("artifact.version"),
							 properties.get("artifact.buildNumber"),
							 properties.get("artifact.gitCommit"),
							 System.getenv("HELLO_ENVIRONMENT"));
}

	private Properties loadProperties(String fileName) {
        InputStream is = getClass().getClassLoader().getResourceAsStream(fileName);
        Properties properties = new Properties();

        if (is != null) {
            try {
                properties.load(is);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return properties;
    }
}