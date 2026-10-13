package org.murlan.um.api.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import org.murlan.um.api.validation.IRequest;
import org.murlan.um.error.BusinessLogicException;
import org.murlan.um.model.dto.HandLayoutDto;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.stream.Collectors;

import static org.murlan.um.model.dto.HandLayoutDto.*;

@Getter
public class UpdateHandLayoutConfigurationRequest implements IRequest {
    @NotNull(message = "[UpdateHandLayoutConfigurationRequest]: handLayouts field is mandatory")
    private List<HandLayoutDto> handLayouts;

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public UpdateHandLayoutConfigurationRequest(List<HandLayoutDto> handLayouts) {
        this.handLayouts = handLayouts;
    }

    @Override
    public void preValidate() throws BusinessLogicException {
        BusinessLogicException e = new BusinessLogicException(HttpStatus.BAD_REQUEST);

        if (handLayouts.isEmpty()) {
            e.addErrorMessage("handLayouts field cannot be empty");
        }

        if (e.hasErrorMessages()) throw e;
    }

    @Override
    public void postValidate() throws BusinessLogicException {
        BusinessLogicException e = new BusinessLogicException(HttpStatus.BAD_REQUEST);

        boolean hasNoneActive = handLayouts.stream().noneMatch(HandLayoutDto::isActive);
        if (hasNoneActive) {
            e.addErrorMessage("no active hand layout found");
            throw e;
        }

        boolean hasDuplicateActive = handLayouts.stream()
                .filter(HandLayoutDto::isActive)
                .count() > 1;
        if (hasDuplicateActive) {
            e.addErrorMessage("duplicate active hand layouts found");
        }

        boolean hasDuplicateNames = handLayouts.stream()
                .collect(Collectors.groupingBy(
                        HandLayoutDto::getName,
                        Collectors.counting()
                ))
                .values()
                .stream()
                .anyMatch(count -> count > 1);
        if (hasDuplicateNames) {
            e.addErrorMessage("duplicate hand layout names found");
        }

        boolean anyDoesNotHavePlacements = handLayouts.stream()
                .anyMatch(hl -> hl.getCardCombinationPlacements().isEmpty());
        if (anyDoesNotHavePlacements) {
            e.addErrorMessage("hand layout with empty card combination placements found");
        }

        boolean isInitialConfigurationMissing = handLayouts.stream()
                .filter(hl -> hl.getCardCombinationPlacements().size() == 1)
                .map(hl -> hl.getCardCombinationPlacements().getFirst())
                .filter(placement -> CardCombinationPlacementType.DEFAULT.equals(placement.getCardCombinationPlacementType()))
                .filter(placement -> List.of(SortMode.ASC, SortMode.DESC, SortMode.RANDOM).contains(placement.getSortMode()))
                .toList()
                .size() < 3;
        if (isInitialConfigurationMissing) {
            e.addErrorMessage("initial configuration is missing");
        }

        if (e.hasErrorMessages()) throw e;
    }
}
