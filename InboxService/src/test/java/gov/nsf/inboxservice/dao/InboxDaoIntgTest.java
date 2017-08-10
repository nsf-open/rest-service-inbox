package gov.nsf.inboxservice.dao;

import gov.nsf.common.exception.ResourceNotFoundException;
import gov.nsf.common.model.ExpirationFilter;
import gov.nsf.inboxservice.api.model.Message;
import gov.nsf.inboxservice.api.model.MessagePriority;
import gov.nsf.inboxservice.api.model.MessageType;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static org.junit.Assert.*;

/**
 * Embedded DB integration tests for InboxDao
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"Embedded-InboxDaoTest-Context.xml"})
public class InboxDaoIntgTest {

    @Autowired
    private InboxDaoImpl inboxDao;


    @Before
    public void checkFieldsNotNull(){
        assertNotNull(inboxDao.getJdbcTemplate());
        assertNotNull(inboxDao.getDataSource());

    }

    @Test
    public void getMessageByIdHappyPathTest() throws Exception {
        Message message = inboxDao.getMessageById("1");
        assertNotNull(message);
        System.out.println(message);
    }

    @Test(expected= ResourceNotFoundException.class)
    public void getMessageByIdDoesNotExistTest() throws Exception {
        inboxDao.getMessageById("10000");
    }

    @Test
    public void getMessagesHappyPathTest() throws Exception {
        List<Message> messages = inboxDao.getMessagesForUser("test", ExpirationFilter.ACTIVE);
        assertNotNull(messages);
        System.out.println("MEssages ---------------###########"+ messages.toString());
        assertTrue(messages.size() > 0);
        for(Message message : messages ){
            System.out.println(ToStringBuilder.reflectionToString(message));
            assertNotNull(message);
        }
    }

    @Test
    public void getMessagesNoneExistTest() throws Exception {
        List<Message> messages = inboxDao.getMessagesForUser("LOLOL", ExpirationFilter.ACTIVE);
        assertNotNull(messages);
        assertTrue(messages.isEmpty());
    }

    @Test
    public void createMessageHappyPathTest() throws Exception {
        Message _message = new Message();
        _message.setLanId("test");
        _message.setInternal(false);
        _message.setType(MessageType.Information);
        _message.setExpirationDate("2017-01-01 23:30:32.0");
        _message.setActionLink("http://LOL.com");
        _message.setCreationDate("2017-01-01 23:30:32.0");
        _message.setSummary("LOLOL");
        _message.setPriority(MessagePriority.High);
        _message.setLastUpdtUser("MeetingSvc");

        Message returnedMessage = inboxDao.createMessage(_message);

        assertNotNull(returnedMessage);
    }

    @Test
    public void deleteMessageHappyPathTest() throws Exception {
        String messageId = "1";
        Message message = inboxDao.deleteMessage(messageId);
        assertNotNull(message);
        System.out.println(message);

        try {
            inboxDao.getMessageById(messageId);
            fail("Expected a ResourceNotFoundException");
        } catch(ResourceNotFoundException ex){
            //Expected
        }
    }

    @Test(expected= ResourceNotFoundException.class)
    public void deleteMessageNotFoundTest() throws Exception {
        inboxDao.deleteMessage("10000");
    }

    @Test
    public void deleteExpiredMessagesHappyPathTest() throws Exception {
        List<Message> messages = inboxDao.getMessagesForUser("test", ExpirationFilter.INACTIVE);
        System.out.println("MEssages ---------------###########"+ messages.toString());
        System.out.println("Size: "+ messages.size());
        for(Message message : messages ){
            System.out.println(ToStringBuilder.reflectionToString(message));
        }
        assertFalse(messages.isEmpty());
        inboxDao.deleteExpiredMessages("test");

        messages = inboxDao.getMessagesForUser("test", ExpirationFilter.INACTIVE);
        System.out.println("MEssages ---------------###########"+ messages.toString());
        System.out.println("Size: "+ messages.size());
        for(Message message : messages ){
            System.out.println(ToStringBuilder.reflectionToString(message));
        }
        assertTrue(messages.isEmpty());
    }
}
