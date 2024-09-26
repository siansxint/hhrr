package me.siansxint.hhrr;

import me.siansxint.hhrr.ability.Ability;
import me.siansxint.hhrr.application.Application;
import me.siansxint.hhrr.application.ApplicationRepository;
import me.siansxint.hhrr.experience.Experience;
import me.siansxint.hhrr.language.Language;
import me.siansxint.hhrr.language.LanguageRepository;
import me.siansxint.hhrr.position.Position;
import me.siansxint.hhrr.position.PositionRepository;
import me.siansxint.hhrr.position.Risk;
import me.siansxint.hhrr.training.Training;
import me.siansxint.hhrr.user.User;
import me.siansxint.hhrr.user.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;

@SpringBootApplication
public class HHRRApplication {

    public static void main(String[] args) {
        SpringApplication.run(HHRRApplication.class, args);
    }

    @Bean
    public CommandLineRunner commandLineRunner(UserRepository userRepository, ApplicationRepository applicationRepository, PositionRepository positionRepository, PasswordEncoder passwordEncoder, LanguageRepository languageRepository) {
        return args -> {
            User user = new User();
            user.setEmail("user@example.me");
            user.setPassword(passwordEncoder.encode("1234"));
            user.setName("Joshua");
            user.setCardId("402-1287499-0");
            user.setRole(User.Role.USER);

            User employee = new User();
            employee.setEmail("employee@example.me");
            employee.setPassword(passwordEncoder.encode("1234"));
            employee.setName("Joshua");
            employee.setCardId("402-1287499-1");
            employee.setRole(User.Role.EMPLOYEE);

            User admin = new User();
            admin.setEmail("admin@example.me");
            admin.setPassword(passwordEncoder.encode("1234"));
            admin.setName("Joshua");
            admin.setCardId("402-1287499-2");
            admin.setRole(User.Role.ADMIN);

            userRepository.saveAll(List.of(user, employee, admin));

            Language spanish = new Language();
            spanish.setName("Spanish");

            Language english = new Language();
            english.setName("English");

            languageRepository.saveAll(List.of(spanish, english));

            Position position = new Position();
            position.setName("Developer");
            position.setDescription("Developer");
            position.setDepartment("Development");
            position.setMinSalary(50000);
            position.setMaxSalary(100000);
            position.setRisk(Risk.MEDIUM);

            position.getLanguages().addAll(List.of(spanish, english));

            positionRepository.save(position);

            Application application = new Application();
            application.setPosition(position);
            application.setOwner(user);
            application.setSalaryExpectation(10000);

            Experience experience = new Experience();
            experience.setApplication(application);
            experience.setStarted(LocalDate.now().minusDays(3));
            experience.setFinished(LocalDate.now());
            experience.setSalary(100000);
            experience.setCompany("Company");

            application.setExperiences(Collections.singletonList(experience));

            Training training = new Training();
            training.setApplication(application);
            training.setDescription("Udemy Fullstack Course");

            application.setTrainings(Collections.singletonList(training));

            Ability ability = new Ability();
            ability.setApplication(application);
            ability.setDescription("Fast Learning");

            application.setAbilities(Collections.singletonList(ability));

            application.setLanguages(List.of(english, spanish));

            user.getApplications().add(application);

            applicationRepository.save(application);
        };
    }
}