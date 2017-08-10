package gov.nsf.inboxservice.dao;

import gov.nsf.common.exception.RollbackException;
import gov.nsf.common.model.ExpirationFilter;
import gov.nsf.inboxservice.api.model.Message;

import java.util.List;

/**
 * InboxDao interface
 *
 */
public interface InboxDao {

    /**
     * Retrieves the Message object from the DB given its message ID
     *
     * @param msgId
     *      - the integer message ID
     * @return Message
     * @throws RollbackException
     */
    public Message getMessageById(String msgId) throws RollbackException;

    /**
     * Retrieves a list of Message objects from the DB given the user's lan ID and filtered by the "active" parameter
     *
     * @param lanId
     *      - the user's lan ID
     * @param active
     *      - "true" : returns all TASK messages and all non-expired INFORMATION messages
     *      - "false" : returns all expired INFORMATION messages
     *      - "ALL" : returns all messages     *
     * @return List of Message objects (empty if none exist)
     * @throws RollbackException
     */
    public List<Message> getMessagesForUser(String lanId, ExpirationFilter active) throws RollbackException;

    /**
     * Inserts the Message object into the DB
     *
     * @param message
     *      - the Message object
     * @return Message
     * @throws RollbackException
     */
    public Message createMessage(Message message) throws RollbackException;

    /**
     * Deletes the message from the DB for the given its message ID
     *
     * @param msgId
     *      - the message ID
     * @return Message
     * @throws RollbackException
     */
    public Message deleteMessage(String msgId) throws RollbackException;

    /**
     * Deletes all expired messages for the passed lan ID
     *
     * @param lanId
     *      - the user's lan ID
     * @throws RollbackException
     */
    public void deleteExpiredMessages(String lanId) throws RollbackException;
}
