package com.bank.creditmonitor.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "credit_alerts")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreditAlert {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "borrower_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Borrower borrower;

    @Column(name = "alert_type", nullable = false, length = 30)
    private String alertType;

    @Column(nullable = false, length = 10)
    private String severity;  // CRITICAL, WARNING, INFO, OK

    @Column(columnDefinition = "TEXT")
    private String message;

    @Column(name = "is_resolved")
    @Builder.Default
    private Boolean isResolved = false;

    @Column(name = "alert_time")
    private LocalDateTime alertTime;

    @Column(name = "resolved_at")
    private LocalDateTime resolvedAt;
}
