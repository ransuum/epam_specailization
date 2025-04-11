package org.epam.models.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    @JoinColumn(name = "id", referencedColumnName = "id")
    private User user;

    @Column(nullable = false, name = "date_of_birth")
    private LocalDate dateOfBirth;

    private String address;

    @OneToMany(mappedBy = "trainee", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Training> trainings;

    public Trainee(User users, LocalDate dateOfBirth, String address) {
        this.user = users;
        this.dateOfBirth = dateOfBirth;
        this.address = address;
    }
}
