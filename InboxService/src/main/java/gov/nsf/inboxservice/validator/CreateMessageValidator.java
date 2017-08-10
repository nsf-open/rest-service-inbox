package gov.nsf.inboxservice.validator;

import gov.mynsf.common.datetime.NsfDateTimeUtil;
import gov.nsf.common.exception.FormValidationException;
import gov.nsf.common.model.BaseError;
import gov.nsf.common.util.NsfValidationUtils;
import gov.nsf.inboxservice.api.model.CreateMessageRequest;
import gov.nsf.inboxservice.api.model.Message;
import gov.nsf.inboxservice.api.model.MessagePriority;
import gov.nsf.inboxservice.api.model.MessageType;
import gov.nsf.inboxservice.common.util.Constants;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.jsoup.Jsoup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * Validator class for Create Message requests
 */
public class CreateMessageValidator {

    private CreateMessageValidator(){
    }

    public static void validateRequest(CreateMessageRequest request) throws FormValidationException {
        List<BaseError> errors = new ArrayList<BaseError>();

        errors.addAll(validateMessage(request.getMessage()));
        errors.addAll(validateLanIds(request.getLanIds()));

        if (!errors.isEmpty()) {
            throw new FormValidationException(Constants.INVALID_FORM_DATA, errors);
        }
    }

    protected static List<BaseError> validateMessage(Message message) {
        if( message == null ){
            return Collections.singletonList(new BaseError(Constants.REQUEST_MESSAGE_FIELD, Constants.MISSING_NULL_EMPTY_FIELD + Constants.REQUEST_MESSAGE_FIELD));
        }

        List<BaseError> errors = new ArrayList<BaseError>();

        //Summary cannot be null, empty, or invalid UTF8
        if (StringUtils.isEmpty(message.getSummary())) {
            errors.add(new BaseError(Constants.MSG_SUMMARY_FIELD, Constants.MISSING_NULL_EMPTY_FIELD + Constants.MSG_SUMMARY_FIELD));
        } else if (!NsfValidationUtils.isValidUTF8(message.getSummary())) {
            errors.add(new BaseError(Constants.MSG_SUMMARY_FIELD, Constants.UNSUPPORTED_CHARACTERS_FIELD + Constants.MSG_SUMMARY_FIELD));
        } else {
            String messagePlainText = Jsoup.parse(message.getSummary()).text().trim(); //Strips all HTML tags and trims trailing whitespace
            if (messagePlainText.length() > Constants.MESSAGE_PLAIN_TEXT_MAX_LENGTH) {
                errors.add(new BaseError(Constants.MSG_SUMMARY_FIELD, Constants.FIELD_LENGTH_TOO_LONG + Constants.MESSAGE_PLAIN_TEXT_MAX_LENGTH));
            }
        }

        if (message.getPriority() == null) {
            errors.add(new BaseError(Constants.MSG_PRIORITY_FIELD, Constants.MISSING_NULL_FIELD + Constants.MSG_PRIORITY_FIELD));
        } else if (message.getPriority() == MessagePriority.Invalid) {
            errors.add(new BaseError(Constants.MSG_PRIORITY_FIELD, Constants.INVALID_MESSAGE_PRIORITY));
        }

        if (message.getType() == null) {
            errors.add(new BaseError(Constants.MSG_TYPE_FIELD, Constants.MISSING_NULL_FIELD + Constants.MSG_TYPE_FIELD));
        } else if (message.getType() == MessageType.Invalid) {
            errors.add(new BaseError(Constants.MSG_TYPE_FIELD, Constants.INVALID_MESSAGE_TYPE));
        } else if (message.getType() == MessageType.Information) {
            if (StringUtils.isEmpty(message.getExpirationDate())) {
                errors.add(new BaseError(Constants.MSG_EXP_DATE_FIELD, Constants.MISSING_NULL_EMPTY_FIELD + Constants.MSG_EXP_DATE_FIELD));
            } else if (!NsfValidationUtils.isValidUTF8(message.getExpirationDate())) {
                errors.add(new BaseError(Constants.MSG_EXP_DATE_FIELD, Constants.UNSUPPORTED_CHARACTERS_FIELD + Constants.MSG_EXP_DATE_FIELD));
            } else if (!NsfDateTimeUtil.isValidDateTime(message.getExpirationDate())) {
                errors.add(new BaseError(Constants.MSG_EXP_DATE_FIELD, Constants.INVALID_DATE_TIME + Constants.MSG_EXP_DATE_FIELD));
            } else {
                String sybaseExpDate = NsfDateTimeUtil.convertToSybaseDateTimeString(message.getExpirationDate());
                String sybaseCurrDate = NsfDateTimeUtil.convertToSybaseDateTimeString(new DateTime().toString());
                if (sybaseExpDate.compareTo(sybaseCurrDate) < 0) {
                    errors.add(new BaseError(Constants.MSG_EXP_DATE_FIELD, Constants.EXP_DATE_LESS_THAN_CURRENT_DATE));
                }
                message.setExpirationDate(sybaseExpDate);
            }
        } else if (message.getType() == MessageType.Task) {
            if (StringUtils.isEmpty(message.getActionLink())) {
                errors.add(new BaseError(Constants.MSG_ACTION_LINK_FIELD, Constants.MISSING_NULL_EMPTY_FIELD + Constants.MSG_ACTION_LINK_FIELD));
            } else if (!NsfValidationUtils.isValidUTF8(message.getActionLink())) {
                errors.add(new BaseError(Constants.MSG_ACTION_LINK_FIELD, Constants.UNSUPPORTED_CHARACTERS_FIELD + Constants.MSG_ACTION_LINK_FIELD));
            }

            if (StringUtils.isEmpty(message.getActionLabel())) {
                errors.add(new BaseError(Constants.MSG_ACTION_LABEL_FIELD, Constants.MISSING_NULL_EMPTY_FIELD + Constants.MSG_ACTION_LABEL_FIELD));
            } else if (!NsfValidationUtils.isValidUTF8(message.getActionLabel())) {
                errors.add(new BaseError(Constants.MSG_ACTION_LABEL_FIELD, Constants.UNSUPPORTED_CHARACTERS_FIELD + Constants.MSG_ACTION_LABEL_FIELD));
            } else if( message.getActionLabel().length() > Constants.MSG_ACTION_LABEL_MAX_LEGNTH ){
                errors.add(new BaseError(Constants.MSG_ACTION_LABEL_FIELD, Constants.FIELD_LENGTH_TOO_LONG + Constants.MSG_ACTION_LABEL_MAX_LEGNTH));
            }
        }

        if( StringUtils.isEmpty(message.getLastUpdtUser()) ){
            errors.add(new BaseError(Constants.MSG_LAST_UPDT_USER_FIELD, Constants.MISSING_NULL_EMPTY_FIELD + Constants.MSG_LAST_UPDT_USER_FIELD));
        } else if (!NsfValidationUtils.isValidUTF8(message.getLastUpdtUser())) {
            errors.add(new BaseError(Constants.MSG_LAST_UPDT_USER_FIELD, Constants.UNSUPPORTED_CHARACTERS_FIELD + Constants.MSG_LAST_UPDT_USER_FIELD));
        }

        return errors;
    }

    /**
     * Validates the List of lanIds
     *
     * @param lanIds
     * @return List of BaseErrors
     */
    protected static List<BaseError> validateLanIds(List<String> lanIds) {

        if (lanIds == null || lanIds.isEmpty()) {
            return Collections.singletonList(new BaseError(Constants.REQUEST_LAN_IDS_FIELD, Constants.MISSING_NULL_EMPTY_FIELD + Constants.REQUEST_LAN_IDS_FIELD));
        }

        List<BaseError> errors = new ArrayList<BaseError>();
        for (String lanId : lanIds) {
            if( !errors.isEmpty()){
                break;
            }
            //LanId cannot be null, empty, invalid UTF8, greater than MAX_LENGTH
            if (StringUtils.isEmpty(lanId) ) {
                errors.add(new BaseError(Constants.REQUEST_LAN_IDS_FIELD, Constants.LAN_IDS_CONTAINS_INVALID_LAN_ID));
            } else if (!StringUtils.isAlphanumeric(lanId) ) {
                errors.add(new BaseError(Constants.REQUEST_LAN_IDS_FIELD, Constants.LAN_IDS_CONTAINS_INVALID_LAN_ID));
            } else if(lanId.length() > Constants.LAN_ID_MAX_LENGTH){
                errors.add(new BaseError(Constants.REQUEST_LAN_IDS_FIELD, Constants.LAN_IDS_CONTAINS_INVALID_LAN_ID));
            }
        }
        return errors;
    }

}
