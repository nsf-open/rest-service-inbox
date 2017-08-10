package gov.nsf.inboxservice.service;

import gov.nsf.common.exception.RollbackException;
import gov.nsf.common.model.BaseResponseWrapper;
import gov.nsf.common.model.ExpirationFilter;
import gov.nsf.inboxservice.api.model.Message;
import gov.nsf.inboxservice.api.model.MessageResponseWrapper;
import gov.nsf.inboxservice.common.util.TestUtils;
import gov.nsf.inboxservice.dao.InboxDaoImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

/**
 * JUnit tests for InboxService
 */
@RunWith(MockitoJUnitRunner.class)
public class InboxServiceTest {

    @InjectMocks
    private InboxServiceImpl inboxService;

    @Mock
    private InboxDaoImpl inboxDao;


    /**
     * Tests the expected behavior of the happy path case
     *
     * Mocks the DAO to return a Message
     * @throws Exception
     */
    @Test
    public void getMessageHappyPathTest() throws Exception {
        Message mockedMessage = TestUtils.getMockMessageForUser("test");
        when(inboxDao.getMessageById(mockedMessage.getId())).thenReturn(mockedMessage);
        MessageResponseWrapper returnedWrapper = inboxService.getMessage(mockedMessage.getId());
        assertNotNull(returnedWrapper);
    }

    /**
     * Tests the expected behavior when exceptions are thrown from the DAO
     *
     * Mocks DAO to throw a RollbackException
     * @throws Exception
     */
    @Test(expected = RollbackException.class)
    public void getMessageExceptionTest() throws Exception{
        when(inboxDao.getMessageById(anyString())).thenThrow(new RollbackException("Some DAO exception occured"));
        inboxService.getMessage("10000");
    }

    /**
     * Tests the expected behavior of the happy path case
     *
     * Mocks the DAO to return a List of Messages
     * @throws Exception
     */
    @Test
    public void getMessagesHappyPathTest() throws Exception {
        List<Message> mockedMessages = TestUtils.getMockMessages("test", "1","2");
        when(inboxDao.getMessagesForUser("test", ExpirationFilter.ACTIVE)).thenReturn(mockedMessages);
        MessageResponseWrapper returnedWrapper = inboxService.getMessages("test", ExpirationFilter.ACTIVE);
        assertNotNull(returnedWrapper);
    }

    /**
     * Tests the expected behavior when exceptions are thrown from the DAO
     *
     * Mocks DAO to throw a RollbackException
     * @throws Exception
     */
    @Test(expected = RollbackException.class)
    public void getMessagesExceptionTest() throws Exception{
        when(inboxDao.getMessagesForUser("test", ExpirationFilter.ACTIVE)).thenThrow(new RollbackException("Some DAO exception occured"));
        inboxService.getMessages("test", ExpirationFilter.ACTIVE);
    }

    /**
     * Tests the expected behavior of the happy path case
     *
     * Mocks DAO to return inserted message
     * @throws Exception
     */
    @Test
    public void createMessageHappyPathTest() throws Exception {
        Message message = TestUtils.getMockMessageForUser("");
        List<String> lanIds = new ArrayList<String>();
        lanIds.add("test");
        lanIds.add("test");
        when(inboxDao.createMessage(any(Message.class))).thenReturn(TestUtils.getMockMessageForUser("test")).
                                                         thenReturn(TestUtils.getMockMessageForUser("test"));
        MessageResponseWrapper returnedMessageWrapper = inboxService.createMessage(message, lanIds);
        assertNotNull(returnedMessageWrapper);
        verify(inboxDao, times(lanIds.size())).createMessage(any(Message.class));
    }

    /**
     * Tests the expected behavior of the happy path case when duplicate lan Ids are passed
     *
     * Mocks DAO to return inserted message
     * @throws Exception
     */
    @Test
    public void createMessageDuplicateLanIdHappyPathTest() throws Exception {
        Message message = TestUtils.getMockMessageForUser("");
        List<String> lanIds = new ArrayList<String>();
        lanIds.add("test");
        lanIds.add("test ");
        when(inboxDao.createMessage(any(Message.class))).thenReturn(TestUtils.getMockMessageForUser("test")).
                thenReturn(TestUtils.getMockMessageForUser("test"));
        MessageResponseWrapper returnedMessageWrapper = inboxService.createMessage(message, lanIds);
        assertNotNull(returnedMessageWrapper);
        verify(inboxDao, times(1)).createMessage(any(Message.class));
    }

    /**
     * Tests the expected behavior when exception are thrown from the DAO
     *
     * Mocks the DAO to throw an exception
     * @throws Exception
     */
    @Test(expected = RollbackException.class)
    public void createMessageExceptionTest() throws Exception {
        Message message = TestUtils.getMockMessageForUser("");
        List<String> lanIds = new ArrayList<String>();
        lanIds.add("test");
        lanIds.add("test");
        when(inboxDao.createMessage(any(Message.class))).thenThrow(new RollbackException("Some DAO exception occured"));
        inboxService.createMessage(message, lanIds);
    }


    /**
     * Tests the expected behavior of the happy path case
     *
     * Mocks the DAO to return a Message
     * @throws Exception
     */
    @Test
    public void deleteMessageHappyPathTest() throws Exception {
        Message mockedMessage = TestUtils.getMockMessageForUser("test");
        when(inboxDao.deleteMessage(mockedMessage.getId())).thenReturn(mockedMessage);
        MessageResponseWrapper returnedWrapper = inboxService.deleteMessage(mockedMessage.getId());
        assertNotNull(returnedWrapper);
    }

    /**
     * Tests the expected behavior when exceptions are thrown from the DAO
     *
     * Mocks DAO to throw a RollbackException
     * @throws Exception
     */
    @Test(expected = RollbackException.class)
    public void deleteMessageExceptionTest() throws Exception{
        when(inboxDao.deleteMessage(anyString())).thenThrow(new RollbackException("Some DAO exception occured"));
        inboxService.deleteMessage("10000");
    }

    /**
     * Tests the expected behavior of the happy path case the dao successfully deletes the Messages
     *
     * @throws Exception
     */
    @Test
    public void deleteExpiredMessagesHappyPathTest() throws Exception {
        String lanId = "test";
        doNothing().when(inboxDao).deleteExpiredMessages(lanId);
        BaseResponseWrapper wrapper = inboxService.deleteExpiredMessages(lanId);
        assertNotNull(wrapper);

    }

    /**
     * Tests the expected behavior when an exception is thrown from the dao
     *
     * @throws Exception
     */
    @Test(expected=RollbackException.class)
    public void deleteExpiredMessagesExceptionTest() throws Exception {
        String lanId = "test";
        doThrow(new RollbackException("Some DAO exception occured")).when(inboxDao).deleteExpiredMessages(lanId);
        inboxService.deleteExpiredMessages(lanId);
    }
}
