package me.siansxint.hhrr;

import me.siansxint.hhrr.ability.Ability;
import me.siansxint.hhrr.application.Application;
import me.siansxint.hhrr.application.ApplicationRepository;
import me.siansxint.hhrr.employee.Employee;
import me.siansxint.hhrr.employee.EmployeeRepository;
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@SpringBootApplication
public class HHRRApplication {

    private static final List<String> LANGUAGE_NAMES = List.of(
            "German",
            "French",
            "Italian",
            "Portuguese",
            "Russian",
            "Hindi",
            "Bengali",
            "Persian (Farsi)",
            "Mandarin Chinese",
            "Cantonese",
            "Tibetan",
            "Burmese",
            "Arabic",
            "Hebrew",
            "Amharic",
            "Somali",
            "Swahili",
            "Yoruba",
            "Igbo",
            "Zulu",
            "Turkish",
            "Kazakh",
            "Uzbek",
            "Mongolian",
            "Indonesian",
            "Tagalog",
            "Malay",
            "Javanese",
            "Tamil",
            "Telugu",
            "Kannada",
            "Malayalam",
            "Nama",
            "!Xóõ",
            "Finnish",
            "Hungarian",
            "Estonian",
            "Vietnamese",
            "Thai",
            "Korean",
            "Japanese",
            "Greek",
            "Icelandic"
    );

    public static void main(String[] args) {
        SpringApplication.run(HHRRApplication.class, args);
    }

    @Bean
    public CommandLineRunner commandLineRunner(UserRepository userRepository, ApplicationRepository applicationRepository, PositionRepository positionRepository, PasswordEncoder passwordEncoder, LanguageRepository languageRepository, EmployeeRepository employeeRepository) {
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

            List<Language> languages = new ArrayList<>();
            for (String name : LANGUAGE_NAMES) {
                Language language = new Language();
                language.setName(name);
                languages.add(language);
            }

            languageRepository.saveAll(languages); // default ones

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

            Employee employee1 = new Employee();
            employee1.setOwner(employee);
            employee1.setSalary(10000);
            employee1.setStartedAt(LocalDate.now());
            employee1.setPosition(position);

            employeeRepository.save(employee1);

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