package co.edu.unbosque.accioneselbosqueapi;

import co.elastic.apm.attach.ElasticApmAttacher;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class AccioneselbosqueapiApplication {

	public static void main(String[] args) {
		System.setProperty("elastic.apm.service_name", "acciones-elbosque-api");
		System.setProperty("elastic.apm.secret_token", "1d3EiAEWxnwH0nPfrp");
		System.setProperty("elastic.apm.server_urls", "https://d9b54a2c714141ac8e9505e7c82a52f2.apm.us-central1.gcp.cloud.es.io:443");
		System.setProperty("elastic.apm.environment", "produccion");
		System.setProperty("elastic.apm.application_packages", "co.edu.unbosque.accioneselbosqueapi");

		ElasticApmAttacher.attach();
		SpringApplication.run(AccioneselbosqueapiApplication.class, args);
	}
}
