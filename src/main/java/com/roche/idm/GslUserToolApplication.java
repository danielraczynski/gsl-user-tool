package com.roche.idm;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.roche.idm.repository.CsvDataRepository;

@SpringBootApplication
public class GslUserToolApplication {

	@Autowired
	public CsvDataRepository dataLoader;

	private final Logger logger = LoggerFactory.getLogger(GslUserToolApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(GslUserToolApplication.class, args);
	}

	@Bean
	CommandLineRunner runner() {
		return args -> {};
	}
}

