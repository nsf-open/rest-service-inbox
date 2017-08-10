package gov.nsf.inboxservice.validator;

import gov.nsf.common.exception.FormValidationException;
import gov.nsf.common.model.BaseError;
import gov.nsf.common.util.NsfValidationUtils;
import gov.nsf.inboxservice.common.util.Constants;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Validator class for GetMessage requests
 */
public class GetMessageValidator {

    private GetMessageValidator(){

    }

    /**
     * Validates the request by ensuring the passed msgId is valid
     *
     * @param msgId
     * @throws FormValidationException
     */
    public static void validateRequest(String msgId) throws FormValidationException {
        List<BaseError> errors = new ArrayList<BaseError>();

        if(StringUtils.isEmpty(msgId)){
            errors.add(new BaseError(Constants.MSG_ID_FIELD, Constants.MISSING_NULL_EMPTY_FIELD + Constants.MSG_ID_FIELD));
        } else if(!NsfValidationUtils.isValidIdNumberString(msgId)){
            errors.add(new BaseError(Constants.MSG_ID_FIELD, Constants.MSG_ID_NON_NUMERIC));
        }

        if( !errors.isEmpty() ){
            throw new FormValidationException(Constants.INVALID_FORM_DATA, errors);
        }
    }
}
