package me.siansxint.hhrr.position;

import jakarta.validation.Valid;
import me.siansxint.hhrr.employee.Employee;
import me.siansxint.hhrr.language.LanguageRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/")
public class PositionController {

    private final PositionRepository positionRepository;
    private final LanguageRepository languageRepository;

    public PositionController(PositionRepository positionRepository, LanguageRepository languageRepository) {
        this.positionRepository = positionRepository;
        this.languageRepository = languageRepository;
    }

    @ModelAttribute("positions")
    public List<Position> positions() {
        return positionRepository.findAll();
    }

    @GetMapping
    public String list() {
        return "position/list";
    }

    @GetMapping("/create")
    public String create(Model model) {
        model.addAttribute("position", new Position());
        model.addAttribute("languages", languageRepository.findAll());
        return "position/create";
    }

    @PostMapping("/create")
    public String create(@Valid @ModelAttribute("position") Position position) {
        positionRepository.save(position);
        return "redirect:/";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable("id") long id, Model model) {
        model.addAttribute("position", positionRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid position ID: " + id)));
        model.addAttribute("languages", languageRepository.findAll());
        return "position/edit";
    }

    @PostMapping("/edit")
    public String edit(@Valid @ModelAttribute("position") Position position) {
        positionRepository.save(position);
        return "redirect:/";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable long id) {
        Position position = positionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid position ID: " + id));
        for (Employee employee : position.getEmployees()) {
            employee.setPosition(null);
        }

        positionRepository.delete(position);
        return "redirect:/";
    }
}