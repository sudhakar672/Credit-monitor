package com.bank.creditmonitor.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "cibil_scores")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CibilScore {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "borrower_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Borrower borrower;

    @Column(nullable = false)
    private Integer score;

    @Column(name = "score_date", nullable = false)
    private LocalDate scoreDate;

    @Column(name = "risk_band", length = 20)
    private String riskBand;

    @Column(length = 200)
    private String remarks;
}
