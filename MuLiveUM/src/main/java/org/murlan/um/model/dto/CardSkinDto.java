package org.murlan.um.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public final class CardSkinDto {
    private long id;
    private String name;
    private long totalRequiredScore;
}
