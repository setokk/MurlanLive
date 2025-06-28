package org.murlan.um.api.validation;

import org.murlan.um.error.BusinessLogicException;

/**
 * This interface is used for validations based on <b>services and components</b>.
 * It <b>separates</b> this logic from the simple {@link IValidatable} interface, since it is used only for simple validations
 * regarding fields, values and enums.
 */
public interface IValidator<T extends IValidatable> {
    void advancedValidate(T requestBody) throws BusinessLogicException;
}