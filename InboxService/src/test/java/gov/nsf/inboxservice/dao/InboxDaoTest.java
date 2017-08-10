package gov.nsf.inboxservice.dao;

import gov.nsf.common.exception.ResourceNotFoundException;
import gov.nsf.common.exception.RollbackException;
import gov.nsf.common.model.ExpirationFilter;
import gov.nsf.inboxservice.api.model.Message;
import gov.nsf.inboxservice.common.util.Constants;
import gov.nsf.inboxservice.common.util.TestUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyMap;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * JUnit tests for InboxDao
 */
@RunWith(MockitoJUnitRunner.class)
public class InboxDaoTest {

    @InjectMocks
    private InboxDaoImpl inboxDao;

    @Mock
    private NamedParameterJdbcTemplate jdbcTemplate;

    @Before
    public void checkFieldsNotNull(){
        assertNotNull(inboxDao);
        assertNotNull(inboxDao.getJdbcTemplate());
    }


    /**
     * Tests the expected behavior of the happy path case the dao successfully gets the Message
     *
     * @throws Exception
     */
    @Test
    public void getMessageByIdHappyPathTest() throws Exception {
        Message mockMessage = TestUtils.getMockMessageForUser("");
        when(jdbcTemplate.queryForObject(anyString(), anyMap(), any(RowMapper.class))).thenReturn(mockMessage);
        Message returnedMessage = inboxDao.getMessageById(mockMessage.getId());
        assertNotNull(returnedMessage);
    }

    /**
     * Tests the expected behavior when the DB throws an exception during the get message query
     *
     * @throws Exception
     */
    @Test(expected=RollbackException.class)
    public void getMessageByIdExceptionTest() throws Exception {
        when(jdbcTemplate.queryForObject(anyString(), anyMap(), any(RowMapper.class))).thenThrow(new DataAccessResourceFailureException("Some DB exception occurred"));
        inboxDao.getMessageById("1");
    }

    /**
     * Tests the expected behavior when the DB throws an exception because the message was not found
     *
     * @throws Exception
     */
    @Test(expected=ResourceNotFoundException.class)
    public void getMessageByIdMessageNotFoundTest() throws Exception {
        when(jdbcTemplate.queryForObject(anyString(), anyMap(), any(RowMapper.class))).thenThrow(new EmptyResultDataAccessException(0));
        inboxDao.getMessageById("999");
    }

    /**
     * Tests the expected behavior of the happy path case when the DB returns Messages for the passed lanId
     *
     * @throws Exception
     */
    @Test
    public void getMessagesHappyPathTest() throws Exception {
        List<Message> mockMessages = TestUtils.getMockMessages("test", "1", "2");
        when(jdbcTemplate.query(anyString(), anyMap(), any(ResultSetExtractor.class))).thenReturn(mockMessages);
        List<Message> returnedMessages = inboxDao.getMessagesForUser("test",ExpirationFilter.ACTIVE);
        assertNotNull(returnedMessages);
        assertEquals(mockMessages.size(), returnedMessages.size());
        for( Message message : returnedMessages ){
            assertNotNull(message);
        }
    }

    /**
     * Tests the expected behavior of the happy path case when no messages are found for the passed lanId
     *
     * @throws Exception
     */
    @Test
    public void getMessagesNoMessagesFoundTest() throws Exception {
        when(jdbcTemplate.query(anyString(), anyMap(), any(ResultSetExtractor.class))).thenReturn(Collections.emptyList());
        List<Message> returnedMessages = inboxDao.getMessagesForUser("test", ExpirationFilter.ACTIVE);
        assertNotNull(returnedMessages);
        assertTrue(returnedMessages.isEmpty());
    }

    /**
     * Tests the expected behavior when the DB throws an exception
     *
     * @throws Exception
     */
    @Test(expected= RollbackException.class)
    public void getMessagesExceptionTest() throws Exception {
        when(jdbcTemplate.query(anyString(), anyMap(), any(ResultSetExtractor.class))).thenThrow(new DataAccessResourceFailureException("Some DB exception occured"));
        inboxDao.getMessagesForUser("test", ExpirationFilter.ACTIVE);
    }


    /**
     * Tests the expected behavior of the happy path case the dao successfully inserts the Message
     *
     * @throws Exception
     */
    @Test
    public void createMessageHappyPathTest() throws Exception {
        Message mockMessage = TestUtils.getMockMessageForUser("");
        when(jdbcTemplate.update(anyString(), anyMap())).thenReturn(1);
        when(jdbcTemplate.queryForObject(anyString(), anyMap(), any(RowMapper.class))).thenReturn(mockMessage);
        Message returnedMessage = inboxDao.createMessage(mockMessage);
        assertNotNull(returnedMessage);
    }

    /**
     * Tests the expected behavior when the DB throws an exception during the insert message query
     *
     * @throws Exception
     */
    @Test(expected=RollbackException.class)
    public void createMessageExceptionDuringInsertTest() throws Exception {
        Message mockMessage = TestUtils.getMockMessageForUser("");
        when(jdbcTemplate.update(anyString(), anyMap())).thenThrow(new DataAccessResourceFailureException("Some exception occured"));
        inboxDao.createMessage(mockMessage);

    }

    /**
     * Tests the expected behavior when the DB throws an exception during the retrieve after insert message query
     *
     * @throws Exception
     */
    @Test(expected=RollbackException.class)
    public void createMessageExceptionDuringRetrieveTest() throws Exception {
        Message mockMessage = TestUtils.getMockMessageForUser("");
        when(jdbcTemplate.update(anyString(), anyMap())).thenReturn(1);
        when(jdbcTemplate.queryForObject(anyString(), anyMap(), any(RowMapper.class))).thenThrow(new DataAccessResourceFailureException("Some exception occured"));
        inboxDao.createMessage(mockMessage);
    }

    /**
     * Tests that the getMessagesQuery method returns the appropriate query based on the active value
     */
    @Test
    public void getMessagesQueryTest(){
        assertEquals(Constants.GET_ACTIVE_MESSAGES_FOR_USER_QUERY, inboxDao.getMessagesQuery(ExpirationFilter.ACTIVE));
        assertEquals(Constants.GET_INACTIVE_MESSAGES_FOR_USER_QUERY, inboxDao.getMessagesQuery(ExpirationFilter.INACTIVE));
        assertEquals(Constants.GET_ALL_MESSAGES_FOR_USER_QUERY, inboxDao.getMessagesQuery(ExpirationFilter.ALL));
    }

    /**
     * Tests the expected behavior of the happy path case the dao successfully deletes the Message
     *
     * @throws Exception
     */
    @Test
    public void deleteMessageHappyPathTest() throws Exception {
        Message mockMessage = TestUtils.getMockMessageForUser("");
        when(jdbcTemplate.queryForObject(anyString(), anyMap(), any(RowMapper.class))).thenReturn(mockMessage);
        when(jdbcTemplate.update(anyString(), anyMap())).thenReturn(1);
        Message returnedMessage = inboxDao.deleteMessage(mockMessage.getId());
        assertNotNull(returnedMessage);
    }

    /**
     * Tests the expected behavior when the DB throws an exception during the get message query
     *
     * @throws Exception
     */
    @Test(expected=RollbackException.class)
    public void deleteMessageDuringGetTest() throws Exception {
        when(jdbcTemplate.queryForObject(anyString(), anyMap(), any(RowMapper.class))).thenThrow(new DataAccessResourceFailureException("Some DB exception occurred"));
        inboxDao.deleteMessage("1");
    }

    /**
     * Tests the expected behavior when the DB throws an exception because the message was not found
     *
     * @throws Exception
     */
    @Test(expected=ResourceNotFoundException.class)
    public void deleteMessageMessageNotFoundTest() throws Exception {
        when(jdbcTemplate.queryForObject(anyString(), anyMap(), any(RowMapper.class))).thenThrow(new EmptyResultDataAccessException(0));
        inboxDao.deleteMessage("999");
    }

    /**
     * Tests the expected behavior when the DB throws an exception because the message was not found
     *
     * @throws Exception
     */
    @Test(expected=RollbackException.class)
    public void deleteMessageExceptionOnDeleteTest() throws Exception {
        Message mockMessage = TestUtils.getMockMessageForUser("");
        when(jdbcTemplate.queryForObject(anyString(), anyMap(), any(RowMapper.class))).thenReturn(mockMessage);
        when(jdbcTemplate.update(anyString(), anyMap())).thenThrow(new DataAccessResourceFailureException("Some DB exception occurred"));
        inboxDao.deleteMessage("1");
    }


    /**
     * Tests the expected behavior of the happy path case the dao successfully deletes the Messages
     *
     * @throws Exception
     */
    @Test
    public void deleteExpiredMessagesHappyPathTest() throws Exception {
        when(jdbcTemplate.update(anyString(), anyMap())).thenReturn(1);
        inboxDao.deleteExpiredMessages("test");
    }

    /**
     * Tests the expected behavior when an exception is thrown from the DB
     *
     * @throws Exception
     */
    @Test(expected=RollbackException.class)
    public void deleteExpiredMessagesExceptionTest() throws Exception {
        when(jdbcTemplate.update(anyString(), anyMap())).thenThrow(new DataAccessResourceFailureException("Some DB exception occured"));
        inboxDao.deleteExpiredMessages("test");
    }
}
