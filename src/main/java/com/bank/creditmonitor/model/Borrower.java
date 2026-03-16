package com.bank.creditmonitor.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "borrowers")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Borrower {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "borrower_code", nullable = false, unique = true, length = 20)
    private String borrowerCode;

    @Column(nullable = false, length = 120)
    private String name;

    @Column(nullable = false, length = 50)
    private String segment;

    @Column(length = 60)
    private String city;

    @Column(length = 60)
    private String state;

    @Column(name = "contact_email", length = 100)
    private String contactEmail;

    @Column(name = "pan_number", length = 10, unique = true)
    private String panNumber;

    @Column(length = 15)
    private String gstin;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "borrower", fetch = FetchType.LAZY)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<CreditAccount> accounts;

    @OneToMany(mappedBy = "borrower", fetch = FetchType.LAZY)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<CibilScore> cibilScores;

    @OneToMany(mappedBy = "borrower", fetch = FetchType.LAZY)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<CreditAlert> alerts;

    @PrePersist
    protected void onCreate() { createdAt = LocalDateTime.now(); updatedAt = LocalDateTime.now(); }

    @PreUpdate
    protected void onUpdate() { updatedAt = LocalDateTime.now(); }
}
