package org.murlan.um.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "player")
public class PlayerEntity {
    @Id
    @SequenceGenerator(
            name = "playerSeqGen",
            sequenceName = "player_seq",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "playerSeqGen"
    )
    @Column(name = "id", updatable = false, nullable = false)
    private Long id;

    @Column(name = "username", updatable = false, nullable = false)
    private String username;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "creation_date", updatable = false, nullable = false)
    private LocalDateTime createdDate;

    public PlayerEntity(String username, String password, LocalDateTime createdDate) {
        this.username = username;
        this.password = password;
        this.createdDate = createdDate;
    }
}
