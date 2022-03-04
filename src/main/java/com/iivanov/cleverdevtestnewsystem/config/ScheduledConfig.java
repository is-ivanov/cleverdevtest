package com.iivanov.cleverdevtestnewsystem.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableScheduling
@ComponentScan("com.iivanov.cleverdevtestnewsystem.services")
public class ScheduledConfig {
}
