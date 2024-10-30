package org.topalovic.backend;

import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

/*
Diese Klasse initialisiert die Spring Boot-Anwendung für die Bereitstellung als WAR-Datei
in einem traditionellen Java EE-Servlet-Container (Tomcat).
Sie ist nur erforderlich, wenn die Anwendung als WAR-Datei bereitgestellt wird.
*/
public class ServletInitializer extends SpringBootServletInitializer {

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(BackendApplication.class);
	}

}
