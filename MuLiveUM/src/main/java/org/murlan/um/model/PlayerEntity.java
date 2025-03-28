package org.murlan.um.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
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


}
