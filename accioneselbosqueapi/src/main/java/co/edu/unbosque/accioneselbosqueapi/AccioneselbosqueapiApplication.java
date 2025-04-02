package co.edu.unbosque.accioneselbosqueapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class AccioneselbosqueapiApplication {

	public static void main(String[] args) {
		SpringApplication.run(AccioneselbosqueapiApplication.class, args);
	}

}
