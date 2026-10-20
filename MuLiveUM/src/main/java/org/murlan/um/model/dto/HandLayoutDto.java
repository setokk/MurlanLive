package org.murlan.um.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonPropertyOrder({"name", "isActive", "cardCombinationPlacements"})
public final class HandLayoutDto {
    private String name;
    @JsonProperty("isActive")
    private boolean isActive;
    private List<CardCombinationPlacementDto> cardCombinationPlacements;

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class CardCombinationPlacementDto {
        private CardCombinationPlacementType cardCombinationPlacementType;
        private SortMode sortMode;
    }

    public enum CardCombinationPlacementType {
        DEFAULT,
        DOUBLE_CARDS,
        TRIPLE_CARDS,
        KOLOR,
        BOMB,
        BOMB_KOLOR
    }

    public enum SortMode {
        ASC,
        DESC,
        RANDOM
    }
}
