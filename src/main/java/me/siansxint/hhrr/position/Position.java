package me.siansxint.hhrr.position;

import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import me.siansxint.hhrr.application.Application;
import me.siansxint.hhrr.employee.Employee;
import me.siansxint.hhrr.language.Language;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "positions")
public class Position {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotEmpty
    private @Column(nullable = false) String name;
    @NotEmpty
    private @Column(nullable = false) String description;

    @Enumerated
    @Column(nullable = false)
    private Risk risk;

    @PositiveOrZero
    private @Column(name = "min_salary", nullable = false) long minSalary;
    @PositiveOrZero
    private @Column(name = "max_salary", nullable = false) long maxSalary;

    @NotEmpty
    private @Column(nullable = false) String department;

    @ManyToMany
    @JoinTable(
            name = "positions_languages",
            joinColumns = @JoinColumn(name = "position_id", nullable = false),
            inverseJoinColumns = @JoinColumn(name = "language_id", nullable = false))
    private Collection<Language> languages = new ArrayList<>();

    @Valid
    private @OneToMany(mappedBy = "position", cascade = CascadeType.ALL, orphanRemoval = true) List<Application> applications = new ArrayList<>();
    @Valid
    private @OneToMany(mappedBy = "position", cascade = CascadeType.ALL, orphanRemoval = true) List<Employee> employees = new ArrayList<>();

    @Override
    public String toString() {
        return "Position{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", risk=" + risk +
                ", minSalary=" + minSalary +
                ", maxSalary=" + maxSalary +
                ", department='" + department + '\'' +
                ", languages=" + languages +
                ", applications=" + applications +
                ", employees=" + employees +
                '}';
    }
}