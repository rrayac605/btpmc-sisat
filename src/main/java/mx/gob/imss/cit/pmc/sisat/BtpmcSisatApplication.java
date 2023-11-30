package mx.gob.imss.cit.pmc.sisat;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class BtpmcSisatApplication {
	public static void main(String[] args) {
		SpringApplication.run(BtpmcSisatApplication.class, args);
	}
}
