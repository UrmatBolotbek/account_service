package faang.school.accountservice.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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

    @Column(name = "number", nullable = false, unique = true, length = 20)
    private String number;

    @Column(name = "owner_type", nullable = false, unique = true, length = 20)
    private String owner_type;

    @Column(name = "owner_id", nullable = false)
    private long owner_id;

    @Column(name = "account_type", nullable = false, unique = true, length = 30)
    private String account_type;

    private









}
