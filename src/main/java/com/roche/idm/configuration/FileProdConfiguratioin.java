package com.roche.idm.configuration;

import java.io.IOException;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

@Configuration
@Profile("prod")
public class FileProdConfiguratioin {

	@Bean
	public Resource usersFile() {
		Resource res = new FileSystemResource("users.txt");
		System.out.println("Creating resource");
		System.out.println("Exists " + res.exists());
		try {
			System.out.println("Path : " + res.getFile().getAbsolutePath());
		} catch (IOException e) {
			e.printStackTrace();
		}
		return res;
	}
}
