package org.hartford.mutualinsurance;

import org.hartford.mutualinsurance.entity.*;
import org.hartford.mutualinsurance.security.AppUser;
import org.hartford.mutualinsurance.security.Role;
import org.hartford.mutualinsurance.security.UserRepository;
import org.hartford.mutualinsurance.service.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.time.LocalDate;

@SpringBootApplication
public class MutualInsuranceApplication {

    public static void main(String[] args) {
        SpringApplication.run(MutualInsuranceApplication.class, args);
    }
}
