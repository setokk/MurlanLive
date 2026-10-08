package org.murlan.um.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "profile_icon")
public class ProfileIconEntity {
    @Id
    @SequenceGenerator(
            name = "profileIconSeqGen",
            sequenceName = "profile_icon_seq",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "profileIconSeqGen"
    )
    @Column(name = "id", updatable = false, nullable = false)
    private Long id;

    @Column(name = "filename", nullable = false)
    private String filename;

    public ProfileIconEntity(long id) {
        this.id = id;
    }

    public static ProfileIconEntity defaultProfileIcon() {
        return new ProfileIconEntity(1L, null);
    }
}