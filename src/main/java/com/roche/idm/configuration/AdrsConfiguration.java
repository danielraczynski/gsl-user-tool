package com.roche.idm.configuration;


import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

@Configuration
@ImportResource("classpath:/adrs-config.xml")
public class AdrsConfiguration {
}
