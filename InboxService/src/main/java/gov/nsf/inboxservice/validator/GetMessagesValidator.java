package gov.nsf.inboxservice.validator;

import gov.nsf.common.exception.FormValidationException;
import gov.nsf.common.model.BaseError;
import gov.nsf.inboxservice.common.util.Constants;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Validator class for GetMessages requests
 */
public class GetMessagesValidator {

    private GetMessagesValidator(){
    }

    /**
     * Validates the request by ensuring the passed lanId is valid
     *
     * @param lanId
     * @throws FormValidationException
     */
    public static void validateRequest(String lanId) throws FormValidationException {
        List<BaseError> errors = new ArrayList<BaseError>();

        if (StringUtils.isEmpty(lanId)) {
            errors.add(new BaseError(Constants.MSG_LAN_ID_FIELD, Constants.INVALID_LAN_ID + Constants.MSG_LAN_ID_FIELD));
        } else if(!StringUtils.isAlphanumeric(lanId)){
            errors.add(new BaseError(Constants.MSG_LAN_ID_FIELD, Constants.INVALID_LAN_ID + Constants.MSG_LAN_ID_FIELD));
        } else if( lanId.length() > Constants.LAN_ID_MAX_LENGTH ){
            errors.add(new BaseError(Constants.MSG_LAN_ID_FIELD, Constants.INVALID_LAN_ID + Constants.MSG_LAN_ID_FIELD));
        }

        if (!errors.isEmpty()) {
            throw new FormValidationException(Constants.INVALID_FORM_DATA, errors);
        }
    }

}
