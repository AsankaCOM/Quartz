package com.example.quartzscheduler;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages="com.example.quartzdemo")
public class QuartzSchedulerApplication {

	public static void main(String[] args) {
		SpringApplication.run(QuartzSchedulerApplication.class, args);
	}

}
