package com.example.Transaction_Learning;

import java.math.BigDecimal;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.example.Transaction_Learning.Model.Account;
import com.example.Transaction_Learning.Repo.AccountRepository;

/**
 * Seeds a couple of demo accounts on startup so the application is immediately
 * usable without any manual SQL inserts.
 */
@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner seed(AccountRepository repo) {
        return args -> {
            if (repo.count() == 0) {
                repo.save(new Account("Alice", new BigDecimal("1000.00")));
                repo.save(new Account("Bob",   new BigDecimal("500.00")));
                repo.save(new Account("Carol",  new BigDecimal("250.00")));
            }
        };
    }
}
