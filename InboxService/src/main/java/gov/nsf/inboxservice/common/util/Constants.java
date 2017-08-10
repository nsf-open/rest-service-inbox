package gov.nsf.inboxservice.common.util;

/**
 * Constants for InboxService
 */
public final class Constants {

    //CreateMessagRequest JSON fields
    public static final String REQUEST_MESSAGE_FIELD = "message";
    public static final String REQUEST_LAN_IDS_FIELD = "lanIds";

    //Message JSON fields
    public static final String MSG_ID_FIELD = "msgID";
    public static final String MSG_LAN_ID_FIELD = "lanId";
    public static final String MSG_SUMMARY_FIELD = "summary";
    public static final String MSG_CREATION_DATE_FIELD = "creationDate";
    public static final String MSG_PRIORITY_FIELD = "priority";
    public static final String MSG_TYPE_FIELD = "type";
    public static final String MSG_ACTION_LINK_FIELD = "actionLink";
    public static final String MSG_ACTION_LABEL_FIELD = "actionLabel";
    public static final String MSG_INTERNAL_FIELD = "internal";
    public static final String MSG_EXP_DATE_FIELD = "expirationDate";
    public static final String MSG_LAST_UPDT_USER_FIELD = "lastUpdtUser";

    //Message DB column names
    public static final String MSG_ID_COLNAME = "inbx_msg_id";
    public static final String MSG_LAN_ID_COLNAME = "user_lan_id";
    public static final String MSG_CREATION_DATE_COLNAME = "cre_date";
    public static final String MSG_SUMMARY_COLNAME = "smry";
    public static final String MSG_PRIORITY_COLNAME = "prty";
    public static final String MSG_TYPE_COLNAME = "type";
    public static final String MSG_ACTION_LINK_COLNAME = "actn_link";
    public static final String MSG_ACTION_LABEL_COLNAME = "actn_lbl";
    public static final String MSG_INTERNAL_COLNAME = "innl";
    public static final String MSG_EXP_DATE_COLNAME = "exp_date";
    public static final String LAST_UPDT_PGM_COLNAME = "last_updt_pgm";
    public static final String LAST_UPDT_USER_COLNAME = "last_updt_user";
    public static final String LAST_UPDT_TMSP_COLNAME = "last_updt_tmsp";

    //Validation error strings
    public static final String MISSING_NULL_FIELD = "The field cannot be missing or null: ";
    public static final String UNSUPPORTED_CHARACTERS_FIELD = "The field cannot contain unsupported characters: ";
    public static final String INVALID_MESSAGE_TYPE = "Value for type can only be \'Information\' or \'Task\'";
    public static final String INVALID_MESSAGE_PRIORITY = "Value for priority can only be \'High\' or \'Low\'";
    public static final String MISSING_NULL_EMPTY_FIELD = "The field cannot be null, missing, or empty: ";
    public static final String FIELD_LENGTH_TOO_LONG = "The field cannot exceed the maximum number of characters: ";
    public static final String CONTAINS_NULL_EMPTY_STRING = "The field cannot contain a null or empty string";
    public static final String CONTAINS_UNSUPPORTED_CHARACTERS_STRING = "The field cannot contain a string with unsupported characters";


    // Exception messages
    public static final String SERVER_500_ERROR = "Server Error";
    public static final String DB_TRANSACTION_ERROR = "DB Transaction Error";
    public static final String INVALID_FORM_DATA = "Invalid/Missing Form Data";
    public static final String ACCESS_DENIED_EXCEPTION = "Access Denied";
    public static final String INVALID_REQUEST_PARAMETER = "Invalid/Missing request parameter";
    public static final String UNABLE_TO_READ_JSON = "Unable to read JSON";
    public static final String INCORRECT_FORMAT_PARAMETERS = "Incorrect format of parameters";

    //Response wrapper names
    public static final String BASE_RESPONSE_WRAPPER = "baseResponseWrapper";
    public static final String MESSAGE_RESPONSE_WRAPPER = "messageResponseWrapper";


    public static final String GET_ACTIVE_MESSAGES_FOR_USER_QUERY =
            "select * from dbo.inbx_msg " +
                    "where " + Constants.MSG_LAN_ID_COLNAME + " = " + ":" + Constants.MSG_LAN_ID_COLNAME + " and  (" +
                    Constants.MSG_TYPE_COLNAME + " != 'I'" +
                    " or ( " + Constants.MSG_TYPE_COLNAME + " = 'I' and " + Constants.MSG_EXP_DATE_COLNAME + " > getdate()))";

    public static final String GET_INACTIVE_MESSAGES_FOR_USER_QUERY =
            "select * from dbo.inbx_msg " +
                    "where " + Constants.MSG_LAN_ID_COLNAME + " = " + ":" + Constants.MSG_LAN_ID_COLNAME + " and  " +
                    "(" + Constants.MSG_TYPE_COLNAME + " = 'I' and " + Constants.MSG_EXP_DATE_COLNAME + " <= getdate())";

    public static final String GET_ALL_MESSAGES_FOR_USER_QUERY =
            "select * from dbo.inbx_msg " +
                    "where " + Constants.MSG_LAN_ID_COLNAME + " = " + ":" + Constants.MSG_LAN_ID_COLNAME;

    public static final String GET_LAST_INSERTED_MESSAGE_QUERY =
            "select * from dbo.inbx_msg " +
                    "where inbx_msg_id in(select max(inbx_msg_id) from dbo.inbx_msg) " +
                    "and user_lan_id = :" + Constants.MSG_LAN_ID_COLNAME;


    public static final String INSERT_MESSAGE_QUERY =
            "INSERT INTO dbo.inbx_msg" +
                    "(" +
                    Constants.MSG_LAN_ID_COLNAME + ", " +
                    Constants.MSG_SUMMARY_COLNAME + ", " +
                    Constants.MSG_CREATION_DATE_COLNAME + ", " +
                    Constants.MSG_PRIORITY_COLNAME + ", " +
                    Constants.MSG_TYPE_COLNAME + ", " +
                    Constants.MSG_ACTION_LINK_COLNAME + ", " +
                    Constants.MSG_ACTION_LABEL_COLNAME + ", " +
                    Constants.MSG_INTERNAL_COLNAME + ", " +
                    Constants.MSG_EXP_DATE_COLNAME + ", " +
                    Constants.LAST_UPDT_PGM_COLNAME + ", " +
                    Constants.LAST_UPDT_USER_COLNAME + ", " +
                    Constants.LAST_UPDT_TMSP_COLNAME + " " +
                    ") VALUES" +
                    "(" +
                    ":" + Constants.MSG_LAN_ID_COLNAME + ", " +
                    ":" + Constants.MSG_SUMMARY_COLNAME + ", " +
                    "getdate(), " +
                    ":" + Constants.MSG_PRIORITY_COLNAME + ", " +
                    ":" + Constants.MSG_TYPE_COLNAME + ", " +
                    ":" + Constants.MSG_ACTION_LINK_COLNAME + ", " +
                    ":" + Constants.MSG_ACTION_LABEL_COLNAME + ", " +
                    ":" + Constants.MSG_INTERNAL_COLNAME + ", " +
                    ":" + Constants.MSG_EXP_DATE_COLNAME + ", " +
                    "'InboxService', " +
                    ":" + Constants.LAST_UPDT_USER_COLNAME + ", " +
                    "getdate()" +
                    ")";

    public static final String GET_MESSAGE_BY_ID_QUERY = "select * " +
            "from dbo.inbx_msg " +
            "where " + Constants.MSG_ID_COLNAME + " = " + ":" + Constants.MSG_ID_COLNAME;

    public static final String DELETE_MESSAGE_QUERY = "delete from dbo.inbx_msg " +
            "where " + Constants.MSG_ID_COLNAME + " = " + ":" + Constants.MSG_ID_COLNAME;

    public static final String DELETE_EXPIRED_MESSAGES_QUERY = "delete from dbo.inbx_msg " +
            "where " + Constants.MSG_LAN_ID_COLNAME + " = " + ":" + Constants.MSG_LAN_ID_COLNAME + " " +
            "and " + Constants.MSG_TYPE_COLNAME + " = 'I' " +
            "and " + Constants.MSG_EXP_DATE_COLNAME + " < getdate()";


    public static final String ERROR_GETTING_MESSAGES_FOR_USER = "Could not retrieve messages for lanId: ";
    public static final String ERROR_INSERTING_MESSAGE = "Could not insert message into the database: ";
    public static final String ERROR_GETTING_MESSAGE = "Could not retrieve message: ";
    public static final String ERROR_GETTING_MESSAGE_AFTER_INSERT = "Error retrieving the inserted message: ";
    public static final String ERROR_DELETING_MESSAGE = "Could not delete message: ";
    public static final String INVALID_DATE_TIME = "Invalid date/time format: ";
    public static final String EXP_DATE_LESS_THAN_CURRENT_DATE = "Expiration date/time must be after current date/time";
    public static final String LAN_IDS_CONTAINS_INVALID_LAN_ID = "The field cannot contain an invalid LAN ID format";
    public static final String INVALID_LAN_ID = "The field cannot have an invalid LAN ID format";
    public static final String ERROR_ACCESS_DENIED = "You are not allowed to access this information";

    public static final int LAN_ID_MAX_LENGTH = 8;
    public static final int MSG_ACTION_LABEL_MAX_LEGNTH = 25;
    public static final int MESSAGE_PLAIN_TEXT_MAX_LENGTH = 140;
    public static final String MSG_ID_NON_NUMERIC = "Message ID has to be a valid integer";
    public static final String MSG_NOT_FOUND = "Message ID does not exist: ";
}
