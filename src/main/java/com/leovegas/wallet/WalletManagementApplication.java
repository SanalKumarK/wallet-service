package com.leovegas.wallet;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement
public class WalletManagementApplication {

	public static void main(String[] args) {
		SpringApplication.run(WalletManagementApplication.class, args);
	}

}
