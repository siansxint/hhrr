package me.siansxint.hhrr.application;

import jakarta.validation.Valid;
import me.siansxint.hhrr.ability.Ability;
import me.siansxint.hhrr.employee.Employee;
import me.siansxint.hhrr.employee.EmployeeRepository;
import me.siansxint.hhrr.experience.Experience;
import me.siansxint.hhrr.language.LanguageRepository;
import me.siansxint.hhrr.position.Position;
import me.siansxint.hhrr.position.PositionRepository;
import me.siansxint.hhrr.training.Training;
import me.siansxint.hhrr.user.User;
import me.siansxint.hhrr.user.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashSet;

@Controller
@RequestMapping("/applications")
public class ApplicationController {

    private final ApplicationRepository applicationRepository;
    private final PositionRepository positionRepository;
    private final UserRepository userRepository;
    private final LanguageRepository languageRepository;
    private final EmployeeRepository employeeRepository;

    public ApplicationController(ApplicationRepository applicationRepository, PositionRepository positionRepository, UserRepository userRepository, LanguageRepository languageRepository, EmployeeRepository employeeRepository) {
        this.applicationRepository = applicationRepository;
        this.positionRepository = positionRepository;
        this.userRepository = userRepository;
        this.languageRepository = languageRepository;
        this.employeeRepository = employeeRepository;
    }

    @ModelAttribute("applications")
    public Collection<Application> applications(Model model) {
        Collection<Application> applications = new HashSet<>();

        User user = getCurrentUser();
        if (user == null) {
            model.addAttribute("error", "Invalid user authentication!");
        } else {
            if (user.getRole().ordinal() >= User.Role.EMPLOYEE.ordinal()) {
                applications.addAll(applicationRepository.findAll());
            } else {
                applications.addAll(applicationRepository.findApplicationsByOwnerId(user.getId()));
            }
        }

        model.addAttribute("applications", applications);
        return applications;
    }

    @GetMapping
    public String list() {
        return "application/list";
    }

    @GetMapping("/apply/{positionId}")
    public String apply(@PathVariable("positionId") long positionId, Model model) {
        Position position = positionRepository.findById(positionId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid position ID: " + positionId));

        User user = getCurrentUser();
        if (user == null) {
            model.addAttribute("error", "Invalid user authentication!");
            return "application/list";
        }

        if (user.getApplications().stream()
                .anyMatch(application -> application.getPosition().getId() == positionId)) {
            model.addAttribute("error", "You already applied to this application!");
            return "application/list";
        }

        Application application = new Application();
        application.getExperiences().add(new Experience());
        application.getTrainings().add(new Training());
        application.getAbilities().add(new Ability());
        application.setPosition(position);
        application.setOwner(user);

        model.addAttribute("app", application); // for some reason, when using application as attribute name, Thymeleaf does not recognize MY Application model

        model.addAttribute("languages", languageRepository.findAll());
        return "application/create";
    }

    @PostMapping("/create")
    public String create(@Valid @ModelAttribute("app") Application application, BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("app", application);
            model.addAttribute("languages", languageRepository.findAll());
            return "application/create";
        }

        setChildApplication(application);

        applicationRepository.save(application);
        return "redirect:/applications";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable("id") long id, Model model) {
        model.addAttribute("app", applicationRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid application ID: " + id)));
        model.addAttribute("languages", languageRepository.findAll());
        return "application/edit";
    }

    @PostMapping("/edit")
    public String edit(@Valid @ModelAttribute("application") Application application, BindingResult result) {
        if (result.hasErrors()) {
            return "application/edit";
        }

        setChildApplication(application);

        applicationRepository.save(application);
        return "redirect:/applications";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable long id) {
        Application application = applicationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid application ID: " + id));
        application.getLanguages().clear();
        applicationRepository.deleteById(id);
        return "redirect:/applications";
    }

    @GetMapping("/hire/{id}")
    public String hire(@PathVariable("id") long id, Model model) {
        model.addAttribute("app", applicationRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid application ID: " + id)));
        return "application/hire";
    }

    @PostMapping("/hire/{id}")
    public String hire(@PathVariable("id") long id, long salary, LocalDate startAt) {
        Application application = applicationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid application ID: " + id));

        User user = getCurrentUser();
        if (user == null) {
            return "application/list";
        }

        if (employeeRepository.existsByOwnerId(user.getId())) {
            throw new IllegalStateException("User already has an employee record!");
        }

        Employee employee = new Employee();
        employee.setOwner(user);
        employee.setSalary(salary);
        employee.setPosition(application.getPosition());
        employee.setStartedAt(startAt);

        employeeRepository.save(employee);
        user.setRole(User.Role.EMPLOYEE);
        user.setEmployee(employee);
        user.getApplications().remove(application);
        userRepository.save(user);

        applicationRepository.deleteById(application.getId());

        return "redirect:/employees";
    }

    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof UserDetails details)) {
            return null;
        }

        return userRepository.findUserByEmail(details.getUsername()).orElseThrow(() -> new IllegalArgumentException("Invalid user: " + details.getUsername()));
    }

    private void setChildApplication(Application application) {
        for (Experience experience : application.getExperiences()) {
            experience.setApplication(application);
        }

        for (Training training : application.getTrainings()) {
            training.setApplication(application);
        }

        for (Ability ability : application.getAbilities()) {
            ability.setApplication(application);
        }
    }
}