package me.siansxint.hhrr.language;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import me.siansxint.hhrr.application.Application;
import me.siansxint.hhrr.position.Position;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "languages")
public class Language {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotEmpty
    private @Column(nullable = false) String name;

    @ManyToMany(mappedBy = "languages", fetch = FetchType.EAGER)
    private List<Position> positions = new ArrayList<>();

    @ManyToMany(mappedBy = "languages", fetch = FetchType.EAGER)
    private List<Application> applications = new ArrayList<>();
}