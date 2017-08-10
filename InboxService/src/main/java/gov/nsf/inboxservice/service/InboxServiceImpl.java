package gov.nsf.inboxservice.service;

import gov.nsf.common.exception.RollbackException;
import gov.nsf.common.model.BaseResponseWrapper;
import gov.nsf.common.model.ExpirationFilter;
import gov.nsf.inboxservice.api.model.Message;
import gov.nsf.inboxservice.api.model.MessageResponseWrapper;
import gov.nsf.inboxservice.api.service.InboxService;
import gov.nsf.inboxservice.dao.InboxDao;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * InboxServiceImpl implements the InboxService interface
 *
 */
public class InboxServiceImpl implements InboxService {

    @Autowired
    private InboxDao inboxDao;

    /**
     * InboxDao getter
     *
     * @return inboxDao
     */
    public InboxDao getInboxDao() {
        return inboxDao;
    }

    /**
     * InboxDao setter
     */
    public void setInboxDao(InboxDao inboxDao) {
        this.inboxDao = inboxDao;
    }


    /**
     * Retrieves the message for the passed message ID
     *
     * @param msgId
     *      - the integer message ID
     * @return MessageResponseWrapper containing the Message object
     * @throws RollbackException
     */
    @Override
    public MessageResponseWrapper getMessage(String msgId) throws RollbackException {
        Message message = this.getInboxDao().getMessageById(msgId);
        return new MessageResponseWrapper(message);
    }

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
    @Override
    public MessageResponseWrapper getMessages(String lanId, ExpirationFilter activeFilter) throws RollbackException {
        List<Message> messages = this.getInboxDao().getMessagesForUser(lanId, activeFilter);
        return new MessageResponseWrapper(messages);
    }

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
    @Override
    @Transactional(value = "flp", readOnly = false, propagation = Propagation.REQUIRES_NEW, rollbackFor = {RuntimeException.class, RollbackException.class})
    public MessageResponseWrapper createMessage(Message message, List<String> lanIds) throws RollbackException {
        //Java 8 lambda function for removing duplicate Strings ignoring case and trailing white space
        Set<String> sanitizedLanIds = lanIds.stream().map(lanId -> lanId.toLowerCase().trim()).collect(Collectors.toSet());

        List<Message> returnedMessages = new ArrayList<Message>();
        for( String lanId : sanitizedLanIds ){
            message.setLanId(lanId);
            returnedMessages.add(this.getInboxDao().createMessage(message));
        }

        return new MessageResponseWrapper(returnedMessages);
    }

    /**
     * Deletes the stored message give its message ID
     * @param msgId
     *      - the integer message ID
     * @return MessageResponseWrapper containing the deleted Message object
     * @throws RollbackException
     */
    @Override
    @Transactional(value = "flp", readOnly = false, propagation = Propagation.REQUIRES_NEW, rollbackFor = {RuntimeException.class, RollbackException.class})
    public MessageResponseWrapper deleteMessage(String msgId) throws RollbackException {
        Message message = this.getInboxDao().deleteMessage(msgId);
        return new MessageResponseWrapper(message);
    }

    /**
     * Deletes all expired information messages for the passed user's lan ID
     *
     * @param lanId
     *      - the user's lan ID
     * @return BaseResponseWrapper
     * @throws RollbackException
     */
    @Override
    @Transactional(value = "flp", readOnly = false, propagation = Propagation.REQUIRES_NEW, rollbackFor = {RuntimeException.class, RollbackException.class})
    public BaseResponseWrapper deleteExpiredMessages(String lanId) throws RollbackException {
        this.getInboxDao().deleteExpiredMessages(lanId);
        return new BaseResponseWrapper();
    }
}
