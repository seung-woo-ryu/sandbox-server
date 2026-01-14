package com.sandbox.playgroundapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {
	"com.sandbox"
})
public class PlaygroundApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(PlaygroundApiApplication.class, args);
	}

}
