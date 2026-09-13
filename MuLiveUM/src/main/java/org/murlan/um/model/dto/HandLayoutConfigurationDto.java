package org.murlan.um.model.dto;

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
public final class HandLayoutConfigurationDto {
    private List<HandLayoutDto> handLayouts;

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static final class HandLayoutDto {
        private String name;
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
            SINGLE_CARD,
            DOUBLE_CARDS,
            TRIPLE_CARDS,
            BOMB,
            KOLOR,
            BOMB_KOLOR
        }

        public enum SortMode {
            ASC,
            DESC,
            RANDOM
        }
    }
}
