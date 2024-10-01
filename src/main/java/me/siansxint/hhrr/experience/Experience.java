package me.siansxint.hhrr.experience;

import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import me.siansxint.hhrr.application.Application;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "experiences")
public class Experience {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @PositiveOrZero
    private @Column(nullable = false) double salary;

    @NotNull
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private @Column(nullable = false) LocalDate started;

    @Nullable
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private @Column LocalDate finished;

    @NotEmpty
    private @Column(nullable = false) String company;

    @ManyToOne
    @JoinColumn(name = "application_id")
    private Application application;
}