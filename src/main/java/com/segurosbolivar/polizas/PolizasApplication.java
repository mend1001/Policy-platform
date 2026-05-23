package com.segurosbolivar.polizas;

import com.segurosbolivar.polizas.config.AppProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(AppProperties.class)
public class PolizasApplication {

    public static void main(String[] args) {
        SpringApplication.run(PolizasApplication.class, args);
    }
}
