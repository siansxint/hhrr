package me.siansxint.hhrr.application;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.Valid;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import me.siansxint.hhrr.ability.Ability;
import me.siansxint.hhrr.experience.Experience;
import me.siansxint.hhrr.language.Language;
import me.siansxint.hhrr.position.Position;
import me.siansxint.hhrr.training.Training;
import me.siansxint.hhrr.user.User;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "applications")
public class Application {

    @Id
    @GeneratedValue
    private long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner")
    private User owner;

    @PositiveOrZero
    private @Column(name = "salary_expectation", nullable = false) long salaryExpectation;

    @Valid
    private @OneToMany(mappedBy = "application", cascade = CascadeType.ALL, orphanRemoval = true) List<Experience> experiences = new ArrayList<>();
    @Valid
    private @OneToMany(mappedBy = "application", cascade = CascadeType.ALL, orphanRemoval = true) List<Training> trainings = new ArrayList<>();
    @Valid
    private @OneToMany(mappedBy = "application", cascade = CascadeType.ALL, orphanRemoval = true) List<Ability> abilities = new ArrayList<>();

    @ManyToMany
    @JoinTable(
            name = "applications_languages",
            joinColumns = @JoinColumn(name = "applicant_id", nullable = false),
            inverseJoinColumns = @JoinColumn(name = "language_id", nullable = false))
    private List<Language> languages = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "position")
    private Position position;
}