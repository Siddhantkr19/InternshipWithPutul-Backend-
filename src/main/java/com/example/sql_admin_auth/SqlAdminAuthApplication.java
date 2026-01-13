package com.example.sql_admin_auth; // Your package name

import com.example.sql_admin_auth.entity.Role;
import com.example.sql_admin_auth.entity.User;
import com.example.sql_admin_auth.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.example.sql_admin_auth.entity.Internship;
import com.example.sql_admin_auth.repository.InternshipRepository;

import java.util.List;


@SpringBootApplication
@EnableScheduling
public class SqlAdminAuthApplication {

	public static void main(String[] args) {
		SpringApplication.run(SqlAdminAuthApplication.class, args);
	}

	/**
	 * This bean runs once at application startup.
	 * It creates the default admin user if one doesn't exist.
	 */
	@Bean
	CommandLineRunner commandLineRunner(UserRepository userRepository, PasswordEncoder passwordEncoder, InternshipRepository internshipRepository) {
		return args -> {
			if (userRepository.findByUsername("siddhantkumar7488@gmail.com").isEmpty()) {
				User admin = new User();
				admin.setUsername("siddhantkumar7488@gmail.com");
				admin.setEmail("siddhantkumar7488@gmail.com"); // REQUIRED now
				admin.setPassword(passwordEncoder.encode("Siddhant@123"));

				// CORRECT: Use Enum
				admin.setRole(Role.ADMIN);

				userRepository.save(admin);
				System.out.println("Admin user created successfully!");
			}
			else {
				System.out.println(">>> Admin user already exists. Skipping creation.");
			}
			if (internshipRepository.count() == 0) {
				Internship i1 = new Internship();
				i1.setCompanyName("Tech Solutions Inc.");
				i1.setRole("Frontend Developer Intern");
				i1.setEligibility("Final Year CS Students");
				i1.setStipend("₹15,000/month");
				i1.setLocation("Bangalore (Remote)");
				i1.setApplyLink("#");
				i1.setDuration("3 Months");
				i1.setLastDateToApply("20 -09-2025");

				Internship i2 = new Internship();
				i2.setCompanyName("Data Insights");
				i2.setRole("Data Analyst Intern");
				i2.setEligibility("B.Stat, M.Stat, B.Tech");
				i2.setStipend("₹20,000/month");
				i2.setLocation("Pune");
				i2.setApplyLink("#");
				i2.setDuration("6 Months");
				i2.setLastDateToApply("2025- 9- 20");


				internshipRepository.saveAll(List.of(i1));
				System.out.println("Sample internships created!");
			}
		};
	}
}