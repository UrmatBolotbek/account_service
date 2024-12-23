package faang.school.accountservice.model.account;

import faang.school.accountservice.enums.AccountStatus;
import faang.school.accountservice.enums.AccountType;
import faang.school.accountservice.enums.Currency;
import faang.school.accountservice.enums.OwnerType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.Version;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name="account")
public class Account {

    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false, unique = true, length = 20)
    private String number;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OwnerType ownerType;

    @Column(nullable = false)
    private long ownerId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AccountType accountType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 3)
    private Currency currency;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AccountStatus status;

    @CreationTimestamp
    @Column(name = "created_at")
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private OffsetDateTime updatedAt;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "close_at")
    private LocalDateTime closedAt;

    @Version
    @Column(nullable = false)
    private int version;

}
