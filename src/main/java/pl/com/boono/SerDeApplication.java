package pl.com.boono;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import pl.com.boono.entity.PacketEntity;

import java.util.Map;

@SpringBootApplication
public class SerDeApplication {
	public static void main(String[] args) {
		ApplicationContext ctx = SpringApplication.run(SerDeApplication.class, args);
	}
}
