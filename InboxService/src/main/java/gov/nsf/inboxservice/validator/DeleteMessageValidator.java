package gov.nsf.inboxservice.validator;

import gov.nsf.common.exception.FormValidationException;

/**
 * Validator class for DeleteMessage requests
 */
public class DeleteMessageValidator {
    private DeleteMessageValidator(){

    }
    /**
     * Validates the request by ensuring the passed msgId is valid
     *
     * @param msgId
     * @throws FormValidationException
     */
    public static void validateRequest(String msgId) throws FormValidationException {
        GetMessageValidator.validateRequest(msgId);
    }
}
