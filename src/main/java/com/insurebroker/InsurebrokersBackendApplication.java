package com.insurebroker;

import com.insurebroker.entity.*;
import com.insurebroker.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.time.LocalDate;

@SpringBootApplication
public class InsurebrokersBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(InsurebrokersBackendApplication.class, args);
    }

    @Bean
    CommandLineRunner initDatabase(UserRepository userRepository,
                                   UserRoleRepository userRoleRepository,
                                   BrokerRepository brokerRepository,
                                   InsurerRepository insurerRepository,
                                   ProductRepository productRepository,
                                   ProductCustomFieldRepository customFieldRepository,
                                   PasswordEncoder passwordEncoder) {
        return args -> {

            if (userRepository.findByEmail("admin@insurebroker.com").isEmpty()) {
                System.out.println("Creating default admin account...");
                User admin = new User();
                admin.setFirstName("Admin");
                admin.setLastName("User");
                admin.setEmail("admin@insurebroker.com");
                admin.setPasswordHash(passwordEncoder.encode("admin123"));
                admin.setActive(true);
                userRepository.save(admin);

                UserRoleEntity role = new UserRoleEntity();
                role.setUser(admin);
                role.setRole(Role.ADMINISTRATOR);
                userRoleRepository.save(role);

                Broker brokerProfile = new Broker();
                brokerProfile.setUser(admin);
                brokerProfile.setFirstName("Admin");
                brokerProfile.setLastName("User");
                brokerProfile.setEmail("admin@insurebroker.com");
                brokerProfile.setPhone("0700000000");
                brokerProfile.setLicenseNumber("ADM-001");
                brokerProfile.setHireDate(LocalDate.now());
                brokerProfile.setCommissionRate(new BigDecimal("10.00"));
                brokerProfile.setRole(Role.ADMINISTRATOR);
                brokerProfile.setActive(true);
                brokerRepository.save(brokerProfile);
            }

            if (insurerRepository.count() == 0) {
                System.out.println("Populating database with default Insurers and Products...");

                Insurer allianz = new Insurer();
                allianz.setName("Allianz Țiriac");
                allianz.setCode("ALLIANZ");
                allianz.setContactEmail("contact@allianztiriac.ro");
                allianz.setContactPhone("021 20 20 200");
                allianz.setAddress("Strada Căderea Bastiliei 80-84, București");
                allianz.setActive(true);
                allianz = insurerRepository.save(allianz);

                Insurer omniasig = new Insurer();
                omniasig.setName("Omniasig VIG");
                omniasig.setCode("OMNIASIG");
                omniasig.setContactEmail("office@omniasig.ro");
                omniasig.setContactPhone("021 405 90 09");
                omniasig.setAddress("Aleea Alexandru 51, București");
                omniasig.setActive(true);
                omniasig = insurerRepository.save(omniasig);

                InsuranceProduct autoProduct = new InsuranceProduct();
                autoProduct.setName("Casco Premium Auto");
                autoProduct.setCode("CASCO-PREM-01");
                autoProduct.setDescription("Asigurare CASCO completă, acoperire extinsă inclusiv vandalism.");
                autoProduct.setCategory("AUTO");
                autoProduct.setInsurerId(omniasig.getId());
                autoProduct.setBasePremium(new BigDecimal("50.00"));
                autoProduct.setBaseRate(new BigDecimal("0.002"));
                autoProduct.setActive(true);
                autoProduct = productRepository.save(autoProduct);

                ProductCustomField driversField = new ProductCustomField();
                driversField.setProduct(autoProduct);
                driversField.setName("number_of_drivers");
                driversField.setLabel("Number of drivers");
                driversField.setType("number");
                driversField.setPlaceholder("e.g. 2");
                driversField.setFactorMultiplier(new BigDecimal("1.1"));
                driversField.setRequired(true);
                customFieldRepository.save(driversField);

                InsuranceProduct lifeProduct = new InsuranceProduct();
                lifeProduct.setName("Life Protect Plus");
                lifeProduct.setCode("LIFE-PLUS-01");
                lifeProduct.setDescription("Asigurare de viață cu componentă de protecție la accidente severe.");
                lifeProduct.setCategory("LIFE");
                lifeProduct.setInsurerId(allianz.getId());
                lifeProduct.setBasePremium(new BigDecimal("100.00"));
                lifeProduct.setBaseRate(new BigDecimal("0.0005"));
                lifeProduct.setActive(true);
                lifeProduct = productRepository.save(lifeProduct);

                ProductCustomField smokerField = new ProductCustomField();
                smokerField.setProduct(lifeProduct);
                smokerField.setName("is_smoker");
                smokerField.setLabel("Is the client a smoker?");
                smokerField.setType("checkbox");
                smokerField.setFactorMultiplier(new BigDecimal("1.25"));
                smokerField.setRequired(false);
                customFieldRepository.save(smokerField);

                ProductCustomField occupationField = new ProductCustomField();
                occupationField.setProduct(lifeProduct);
                occupationField.setName("occupation_risk");
                occupationField.setLabel("Occupation Risk Category");
                occupationField.setType("select");
                occupationField.setOptions("[\"Low Risk (Office)\", \"Medium Risk (Construction)\", \"High Risk (Industrial/Mining)\"]");
                occupationField.setFactorCondition("High Risk (Industrial/Mining)");
                occupationField.setFactorMultiplier(new BigDecimal("1.5"));
                occupationField.setRequired(true);
                customFieldRepository.save(occupationField);

                System.out.println("Default Seed Data created successfully!");
            }
        };
    }
}