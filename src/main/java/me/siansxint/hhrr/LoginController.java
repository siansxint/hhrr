package me.siansxint.hhrr;

import jakarta.validation.Valid;
import me.siansxint.hhrr.user.User;
import me.siansxint.hhrr.user.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/auth")
public class LoginController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public LoginController(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/login")
    public String login() {
        return "auth/login";
    }

    @GetMapping("/register")
    public String register(Model model) {
        User user = new User();
        user.setRole(User.Role.USER);

        model.addAttribute("user", user);
        return "auth/register";
    }

    @PostMapping("/register")
    public String register(@Valid @ModelAttribute("user") User user, BindingResult result) {
        if (userRepository.existsUserByCardId(user.getCardId())) {
            result.rejectValue("cardId", "error.user", "card-id is already registered");
        } else if (!validateDominicanIdCard(user.getCardId())) {
            result.rejectValue("cardId", "error.user", "card-id is invalid");
        }

        if (result.hasErrors()) {
            return "auth/register";
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
        return "/auth/login";
    }

    private boolean validateDominicanIdCard(String id) {
        if (id == null) {
            return false;
        }

        String card = id.replace("-", "");

        if (card.length() != 11) {
            return false;
        }

        int checkDigit = Character.getNumericValue(card.charAt(10));
        int sum = 0;

        for (int i = 0; i < 10; i++) {
            int digit = Character.getNumericValue(card.charAt(i));
            int product = (i % 2 == 0) ? digit : digit * 2;

            sum += (product > 9) ? (product - 9) : product;
        }

        int nextTen = ((sum / 10) + 1) * 10;
        int verifierNumber = nextTen - sum;

        return verifierNumber == checkDigit || verifierNumber % 10 == 0;
    }
}