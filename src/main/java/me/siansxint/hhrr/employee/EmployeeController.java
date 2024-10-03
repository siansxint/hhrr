package me.siansxint.hhrr.employee;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import me.siansxint.hhrr.position.Position;
import me.siansxint.hhrr.position.PositionRepository;
import me.siansxint.hhrr.user.User;
import me.siansxint.hhrr.user.UserRepository;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

@Controller
@RequestMapping("/employees")
public class EmployeeController {

    private final EmployeeRepository employeeRepository;
    private final PositionRepository positionRepository;
    private final UserRepository userRepository;

    public EmployeeController(EmployeeRepository employeeRepository, PositionRepository positionRepository, UserRepository userRepository) {
        this.employeeRepository = employeeRepository;
        this.positionRepository = positionRepository;
        this.userRepository = userRepository;
    }

    @ModelAttribute("employees")
    public List<Employee> employees(@RequestParam(required = false) LocalDate startDate,
                                    @RequestParam(required = false) LocalDate endDate,
                                    Model model) {
        List<Employee> employees;

        if (startDate != null && endDate != null) {
            employees = employeeRepository.findEmployeesByStartedAtBetween(startDate, endDate);
        } else {
            employees = employeeRepository.findAll();
        }

        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);

        return employees;
    }

    @GetMapping
    public String list() {
        return "employee/list";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable("id") long id, Model model) {
        model.addAttribute("employee", employeeRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid employee ID: " + id)));
        model.addAttribute("positions", positionRepository.findAll());
        return "employee/edit";
    }

    @PostMapping("/edit")
    public String edit(@Valid @ModelAttribute("employee") Employee employee, BindingResult result, Model model) {
        Position position = positionRepository.findById(employee.getPosition().getId())
                .orElse(null);

        if (position == null) {
            result.rejectValue("position", "error.position", "Invalid position selected!");
        } else {
            employee.setPosition(position);
            if (employee.getSalary() < position.getMinSalary() || employee.getSalary() > position.getMaxSalary()) {
                result.rejectValue("salary", "error.salary", "Salary must be between position salary range!");
            }
        }

        if (result.hasErrors()) {
            User user = userRepository.findById(employee.getOwner().getId())
                    .orElseThrow(() -> new IllegalArgumentException("Invalid user ID: " + employee.getOwner().getId()));
            employee.setOwner(user);
            model.addAttribute("positions", positionRepository.findAll());
            return "employee/edit";
        }

        employeeRepository.save(employee);
        return "redirect:/employees";
    }

    @GetMapping("/report")
    public ResponseEntity<Void> report(@RequestParam(required = false) LocalDate startDate,
                                       @RequestParam(required = false) LocalDate endDate,
                                       HttpServletResponse response) {
        List<Employee> employees;
        String fileName;
        if (startDate != null && endDate != null) {
            employees = employeeRepository.findEmployeesByStartedAtBetween(startDate, endDate);
            fileName = "employees-" + startDate + "-" + endDate + ".xlsx";
        } else {
            employees = employeeRepository.findAll();
            fileName = "employees.xlsx";
        }

        employees.sort(Comparator.comparing(Employee::getStartedAt));

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName);

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Employees");

            Row headerRow = sheet.createRow(0);
            String[] headers = {"ID", "Email", "Name", "Started At", "Salary", "Position"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
            }

            int rowCount = 1;
            for (Employee employee : employees) {
                Row row = sheet.createRow(rowCount++);
                row.createCell(0).setCellValue(employee.getId());
                row.createCell(1).setCellValue(employee.getOwner().getEmail());
                row.createCell(2).setCellValue(employee.getOwner().getName());
                row.createCell(3).setCellValue(employee.getStartedAt().toString());
                row.createCell(4).setCellValue(employee.getSalary());
                row.createCell(5).setCellValue(employee.getPosition().getName());
            }

            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            try (ServletOutputStream out = response.getOutputStream()) {
                workbook.write(out);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable long id) {
        employeeRepository.deleteById(id);
        return "redirect:/employees";
    }
}