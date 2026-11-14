package org.murlan.um.model.view;

import java.time.LocalDateTime;

public interface PlayerDetailsView {
    Long getId();

    String getUsername();

    LocalDateTime getCreationDate();

    String getEmail();

    Long getProfileIconId();

    Double getMu();

    Double getSigma();

    Integer getRoomsPlayed();

    Long getTotalWins();
}
