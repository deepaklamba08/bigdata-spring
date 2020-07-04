package com.bigdata.app;


import com.bigdata.util.ServiceProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan({"com.bigdata"})
@EnableAutoConfiguration
@SpringBootApplication
@EnableConfigurationProperties({ServiceProperties.class})
public class DataServiceApp {
    public static void main(String[] args) {
        SpringApplication.run(DataServiceApp.class, args);
    }
}
