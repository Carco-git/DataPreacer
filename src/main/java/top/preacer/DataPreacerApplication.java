package top.preacer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import top.preacer.database.Test;

@SpringBootApplication
public class DataPreacerApplication {

	public static void main(String[] args) {
		SpringApplication.run(DataPreacerApplication.class, args);
		
		try {
			Test.main(null);
		}catch(Exception e) {
			e.printStackTrace();
		}
		
	}

}
