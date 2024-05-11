package ru.artemiyandarina.blps_lab2.models;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Petition {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Temporal(TemporalType.TIMESTAMP)
    private Timestamp creationDate = new Timestamp(System.currentTimeMillis());

    @Enumerated(EnumType.STRING)
    private Country country = Country.RUSSIA;

    @Column(nullable = false, length = 50)
    private String title;

    @Column(nullable = false, length = 512)
    private String description;

    @ManyToOne
    @JoinColumn(name = "owner_id", referencedColumnName = "id")
    private User owner;

    @Column(name = "is_approved", nullable = false)
    private Boolean is_approved = false;
}
