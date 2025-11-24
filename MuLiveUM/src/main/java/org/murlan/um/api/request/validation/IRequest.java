package org.murlan.um.api.request.validation;

import org.murlan.um.error.BusinessLogicException;

/**
 * This interface <b>should</b> be implemented by all classes that represent the request bodies of all endpoints.
 * <br/><br/>
 * It provides an easy way for request data to be validated
 * by implementing {@link #preValidate()} and {@link #postValidate()} methods.
 * <br/><br/>
 * <b>NOTE:</b> This class methods should only be used for validations that are not supported by {@link jakarta.validation.constraints}.
 */
public interface IRequest {
    default void validate() throws BusinessLogicException {
        preValidate();
        postValidate(); // Request body validation was successful, proceed with post validation actions
    }

    /**
     * Checks for basic errors (empty lists, field lengths etc.)
     * @throws BusinessLogicException if any error in the request body was detected
     */
    private void preValidate() throws BusinessLogicException {
    }

    /**
     * Performs various actions if {@link #preValidate()} was successful (no errors were found).
     * <br/><br/>
     * Examples of such actions are:
     * <ul>
     *  <li>Set default values for unspecified fields</li>
     *  <li>Check if values correspond to certain enums</li>
     * </ul>
     * @throws BusinessLogicException if any error in the values themselves was found (ex. value does not exist for enum)
     */
    private void postValidate() throws BusinessLogicException {
    }
}
