package gov.nsf.inboxservice.validator;

import gov.nsf.common.exception.FormValidationException;

/**
 * Validator class for DeleteExpiredMessages requests
 */
public class DeleteExpiredMessagesValidator {

    private DeleteExpiredMessagesValidator(){

    }
    /**
     * Validates the request by ensuring the passed lanId is valid
     *
     * @param lanId
     * @throws FormValidationException
     */
    public static void validateRequest(String lanId) throws FormValidationException {
        GetMessagesValidator.validateRequest(lanId);
    }
}
