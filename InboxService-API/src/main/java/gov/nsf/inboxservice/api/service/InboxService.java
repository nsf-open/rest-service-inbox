package gov.nsf.inboxservice.api.service;

import gov.nsf.common.exception.RollbackException;
import gov.nsf.common.model.BaseResponseWrapper;
import gov.nsf.common.model.ExpirationFilter;
import gov.nsf.inboxservice.api.model.Message;
import gov.nsf.inboxservice.api.model.MessageResponseWrapper;

import java.util.List;


/**
 * InboxService interface
 *
 */
public interface InboxService {

    /**
     * Retrieves the message for the passed message ID
     *
     * @param msgId
     *      - the integer message ID
     * @return MessageResponseWrapper containing the Message object
     * @throws RollbackException
     */
    public MessageResponseWrapper getMessage(String msgId) throws RollbackException;

    /**
     * Retrieves the messages for the passed user's lan ID filtered by the "active" parameter
     *
     * @param lanId
     *      - the user's lan ID
     * @param activeFilter
     *      - "ACTIVE" : returns all TASK messages and all non-expired INFORMATION messages
     *      - "INACTIVE" : returns all expired INFORMATION messages
     *      - "ALL" : returns all messages
     * @return MessageResponseWrapper containing the retrieved Message objects
     * @throws RollbackException
     */
    public MessageResponseWrapper getMessages(String lanId, ExpirationFilter activeFilter) throws RollbackException;

    /**
     * Stores the message for the given list of user lan IDs
     *
     * @param message
     *      - the message to store
     * @param lanIds
     *      - the list of user's lan IDs
     * @return MessageResponseWrapper containing the created Message objects
     * @throws RollbackException
     */
    public MessageResponseWrapper createMessage(Message message, List<String> lanIds) throws RollbackException;

    /**
     * Deletes the stored message give its message ID
     * @param msgId
     *      - the integer message ID
     * @return MessageResponseWrapper containing the deleted Message object
     * @throws RollbackException
     */
    public MessageResponseWrapper deleteMessage(String msgId) throws RollbackException;

    /**
     * Deletes all expired information messages for the passed user's lan ID
     *
     * @param lanId
     *      - the user's lan ID
     * @return BaseResponseWrapper
     * @throws RollbackException
     */
    public BaseResponseWrapper deleteExpiredMessages(String lanId) throws RollbackException;
}
