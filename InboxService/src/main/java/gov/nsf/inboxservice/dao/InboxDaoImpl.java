package gov.nsf.inboxservice.dao;

import gov.nsf.common.exception.ResourceNotFoundException;
import gov.nsf.common.exception.RollbackException;

import java.util.*;

import javax.sql.DataSource;

import gov.nsf.common.model.ExpirationFilter;
import gov.nsf.inboxservice.api.model.Message;
import gov.nsf.inboxservice.api.model.MessageType;
import gov.nsf.inboxservice.common.util.Constants;
import gov.nsf.inboxservice.dao.rowmapper.MessageResultSetExtractor;
import gov.nsf.inboxservice.dao.rowmapper.MessageRowMapper;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

/**
 * InboxDaoImpl implements the InboxDao methods for retrieving information from
 * the DB
 *
 */
public class InboxDaoImpl implements InboxDao {

    @Autowired
    private DataSource dataSource;
    private NamedParameterJdbcTemplate jdbcTemplate;

    private static final Logger LOGGER = Logger.getLogger(InboxDaoImpl.class);

    /**
     * DataSource getter
     *
     * @return DataSource
     */
    public DataSource getDataSource() {
        return dataSource;
    }

    /**
     * DataSource setter
     *
     * @param dataSource
     */
    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
        this.setJdbcTemplate(new NamedParameterJdbcTemplate(dataSource));
    }

    /**
     * NamedParameterJdbcTemplate getter
     *
     * @return NamedParameterJdbcTemplate
     */
    public NamedParameterJdbcTemplate getJdbcTemplate() {
        return jdbcTemplate;
    }

    /**
     * NamedParameterJdbcTemplate setter
     *
     * @param jdbcTemplate
     */
    public void setJdbcTemplate(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


    /**
     * Retrieves the Message object from the DB given its message ID
     *
     * @param msgId
     *      - the integer message ID
     * @return Message
     * @throws RollbackException
     */
    @Override
    public Message getMessageById(String msgId) throws RollbackException {
        Message message = null;

        try {
            message = this.getJdbcTemplate().queryForObject(Constants.GET_MESSAGE_BY_ID_QUERY,
                    Collections.singletonMap(Constants.MSG_ID_COLNAME, Integer.parseInt(msgId)),
                    new MessageRowMapper());
        } catch(EmptyResultDataAccessException ex){
            LOGGER.error(Constants.ERROR_GETTING_MESSAGE + Constants.MSG_NOT_FOUND + msgId + ": " + ex);
            throw new ResourceNotFoundException(Constants.MSG_NOT_FOUND + msgId);
        } catch ( Exception ex ){
            LOGGER.error(Constants.ERROR_GETTING_MESSAGE + ex);
            throw new RollbackException(Constants.ERROR_GETTING_MESSAGE + ex);
        }

        return message;
    }

    /**
     * Retrieves a list of Message objects from the DB given the user's lan ID and filtered by the "active" parameter
     *
     * Empty list returned if none exist
     *
     * @param lanId
     *      - the user's lan ID
     * @param active
     *      - "true" : returns all TASK messages and all non-expired INFORMATION messages
     *      - "false" : returns all expired INFORMATION messages
     *      - "ALL" : returns all messages     *
     * @return List of Message objects
     * @throws RollbackException
     */
    @Override
    public List<Message> getMessagesForUser(String lanId, ExpirationFilter active) throws RollbackException {
        List<Message> messages = null;
        try {
            messages = this.getJdbcTemplate().query(getMessagesQuery(active),
                    Collections.singletonMap(Constants.MSG_LAN_ID_COLNAME, lanId),
                    new MessageResultSetExtractor());
        } catch (Exception ex) {
            LOGGER.error(Constants.ERROR_GETTING_MESSAGES_FOR_USER + lanId + " : " + ex);
            throw new RollbackException(Constants.ERROR_GETTING_MESSAGES_FOR_USER + lanId);
        }

        return messages;
    }

    /**
     * Inserts the Message object into the DB
     *
     * @param message
     *      - Message object
     * @return Message
     * @throws RollbackException
     */
    @Override
    public Message createMessage(Message message) throws RollbackException {
        try {
            this.getJdbcTemplate().update(Constants.INSERT_MESSAGE_QUERY, getInsertMessageParameterMap(message));
        } catch (Exception ex) {
            LOGGER.error(Constants.ERROR_INSERTING_MESSAGE + ex);
            throw new RollbackException(Constants.ERROR_INSERTING_MESSAGE + ex);
        }

        Message storedMessage = null;
        try {
            storedMessage = this.getJdbcTemplate().queryForObject(Constants.GET_LAST_INSERTED_MESSAGE_QUERY,
                    Collections.singletonMap(Constants.MSG_LAN_ID_COLNAME, message.getLanId()),
                    new MessageRowMapper());
        } catch (Exception ex) {
              LOGGER.error(Constants.ERROR_INSERTING_MESSAGE + Constants.ERROR_GETTING_MESSAGE_AFTER_INSERT + ex);
              throw new RollbackException(Constants.ERROR_INSERTING_MESSAGE + Constants.ERROR_GETTING_MESSAGE_AFTER_INSERT + ex);
        }
        return storedMessage;
    }

    /**
     * Deletes the message from the DB for the given its message ID
     *
     * @param msgId
     *      - the message ID
     * @return Message
     * @throws RollbackException
     */
    @Override
    public Message deleteMessage(String msgId) throws RollbackException {
        Message message = null;

        try {
            message = getMessageById(msgId);
        } catch(ResourceNotFoundException ex){
            LOGGER.error(Constants.ERROR_DELETING_MESSAGE + ex);
            throw ex;
        } catch( RollbackException ex ){
            LOGGER.error(Constants.ERROR_DELETING_MESSAGE + ex);
            throw new RollbackException(Constants.ERROR_DELETING_MESSAGE + ex);
        }

        try {
            this.getJdbcTemplate().update(Constants.DELETE_MESSAGE_QUERY, Collections.singletonMap(Constants.MSG_ID_COLNAME, Integer.parseInt(msgId)));
        } catch (Exception ex) {
            LOGGER.error(Constants.ERROR_DELETING_MESSAGE + ex);
            throw new RollbackException(Constants.ERROR_DELETING_MESSAGE + ex);
        }

        return message;
    }

    /**
     * Deletes all expired messages for the passed lan ID
     *
     * @param lanId
     *      - the user's lan ID
     * @throws RollbackException
     */
    @Override
    public void deleteExpiredMessages(String lanId) throws RollbackException {
        try {
            this.getJdbcTemplate().update(Constants.DELETE_EXPIRED_MESSAGES_QUERY, Collections.singletonMap(Constants.MSG_LAN_ID_COLNAME, lanId));
        } catch (Exception ex) {
            LOGGER.error(Constants.ERROR_DELETING_MESSAGE + ex);
            throw new RollbackException(Constants.ERROR_DELETING_MESSAGE + ex);
        }
    }


    /**
     * Returns the getMessagesForUser query based on the 'active' parameter
     *
     *
     * @param activeFilter
     * @return
     */
    protected String getMessagesQuery(ExpirationFilter activeFilter){
        String query = "";
        if( activeFilter == ExpirationFilter.ACTIVE ){
            query = Constants.GET_ACTIVE_MESSAGES_FOR_USER_QUERY;
        } else if( activeFilter == ExpirationFilter.INACTIVE ) {
            query = Constants.GET_INACTIVE_MESSAGES_FOR_USER_QUERY;
        } else {
            query = Constants.GET_ALL_MESSAGES_FOR_USER_QUERY;
        }

        return query;
    }

    /**
     * Returns the parameter map for inserting a message
     *
     * @param message
     * @return Map of parameters
     */
    private static Map getInsertMessageParameterMap(Message message){
        Map parameters = new HashMap();
        parameters.put(Constants.MSG_LAN_ID_COLNAME, message.getLanId());
        parameters.put(Constants.MSG_SUMMARY_COLNAME, message.getSummary());
        parameters.put(Constants.MSG_PRIORITY_COLNAME, Integer.parseInt(message.getPriority().getCode()));
        parameters.put(Constants.MSG_TYPE_COLNAME, message.getType().getCode());
        parameters.put(Constants.MSG_ACTION_LINK_COLNAME, message.getType() == MessageType.Task ? message.getActionLink() : null );
        parameters.put(Constants.MSG_ACTION_LABEL_COLNAME, message.getType() == MessageType.Task ? message.getActionLabel() : null );
        parameters.put(Constants.MSG_INTERNAL_COLNAME, message.isInternal());
        parameters.put(Constants.MSG_EXP_DATE_COLNAME, message.getType() == MessageType.Information ? message.getExpirationDate() : null);
        parameters.put(Constants.LAST_UPDT_USER_COLNAME, message.getLastUpdtUser());

        return parameters;
    }


}
