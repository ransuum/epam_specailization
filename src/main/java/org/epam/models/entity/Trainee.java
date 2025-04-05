package org.epam.models.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDate;
import java.util.List;

@NoArgsConstructor
@Entity
@Table(name = "trainee")
@Data
@Builder
@AllArgsConstructor
public class Trainee {
    @Id
    @Column(name = "id")
    private String id;

    @OneToOne
    @MapsId
    @JoinColumn(name = "id")
    private Users users;

    @Column(nullable = false, name = "date_of_birth")
    private LocalDate dateOfBirth;

    private String address;

    @OneToMany(mappedBy = "trainee", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Training> trainings;

    public Trainee(Users users, LocalDate dateOfBirth, String address) {
        this.users = users;
        this.dateOfBirth = dateOfBirth;
        this.address = address;
    }
}
