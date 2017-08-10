package gov.nsf.inboxservice.dao.rowmapper;

import gov.nsf.inboxservice.api.model.Message;
import gov.nsf.inboxservice.api.model.MessagePriority;
import gov.nsf.inboxservice.api.model.MessageType;
import gov.nsf.inboxservice.common.util.Constants;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * RowMapper for retrieving Message objects
 */
public class MessageRowMapper implements RowMapper<Message>{

    /**
     * Returns Message object from SQL row
     * @param resultSet
     * @param i
     * @return Message
     * @throws SQLException
     */
    @Override
    public Message mapRow(ResultSet resultSet, int i) throws SQLException {

        Message message = new Message();
        message.setId(getStringFromColumn(resultSet, Constants.MSG_ID_COLNAME));
        message.setLanId(getStringFromColumn(resultSet, Constants.MSG_LAN_ID_COLNAME));
        message.setSummary(getStringFromColumn(resultSet, Constants.MSG_SUMMARY_COLNAME));
        message.setCreationDate(getStringFromColumn(resultSet, Constants.MSG_CREATION_DATE_COLNAME));
        message.setPriority(MessagePriority.getPriorityFromCode(resultSet.getString(Constants.MSG_PRIORITY_COLNAME)));
        message.setType(MessageType.getTypeFromCode(resultSet.getString(Constants.MSG_TYPE_COLNAME)));
        message.setActionLink(getStringFromColumn(resultSet, Constants.MSG_ACTION_LINK_COLNAME));
        message.setActionLabel(getStringFromColumn(resultSet, Constants.MSG_ACTION_LABEL_COLNAME));
        message.setInternal(resultSet.getBoolean(Constants.MSG_INTERNAL_COLNAME) );
        message.setExpirationDate(getStringFromColumn(resultSet, Constants.MSG_EXP_DATE_COLNAME));
        message.setLastUpdtUser(getStringFromColumn(resultSet, Constants.LAST_UPDT_USER_COLNAME));

        return message;
    }

    /**
     * Returns the value at the passed column name, trimming white space
     *
     * Returns empty string if column value is null
     * @param resultSet
     * @param columnName
     * @return
     * @throws SQLException
     */
    private static String getStringFromColumn(ResultSet resultSet, String columnName) throws SQLException{
        String value = resultSet.getString(columnName);
        return value != null ? value.trim()  : "";
    }
}
