package gov.nsf.inboxservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import gov.nsf.common.exception.RollbackException;
import gov.nsf.common.model.BaseResponseWrapper;
import gov.nsf.common.model.ExpirationFilter;
import gov.nsf.components.authorization.model.UserPrincipal;
import gov.nsf.components.rolemanager.model.Identity;
import gov.nsf.components.rolemanager.model.UserData;
import gov.nsf.inboxservice.api.model.CreateMessageRequest;
import gov.nsf.inboxservice.api.model.Message;
import gov.nsf.inboxservice.api.model.MessageResponseWrapper;
import gov.nsf.inboxservice.api.service.InboxService;
import gov.nsf.inboxservice.common.util.TestUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;
import java.util.List;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * JUnit tests for InboxController
 */
@RunWith(MockitoJUnitRunner.class)
@WebAppConfiguration
public class InboxControllerTest {

    @InjectMocks
    private InboxController controller;

    @Mock
    private InboxService inboxService;

    private MockMvc mockMvc;

    @Before
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    private final String TEST_LAN_ID = "test";

    /**
     * Tests that the getMessageById happy path behavior returns with a 200 response for the auth route
     *
     * @throws Exception
     */
    @Test
    public void getMessageByIdRequestWithAuthHappyPathTest() throws Exception {
        Message mockedMessage = TestUtils.getMockMessageForUser(TEST_LAN_ID);
        when(inboxService.getMessage(mockedMessage.getId())).thenReturn(new MessageResponseWrapper(mockedMessage));
        String URL = "/auth/messages/" + mockedMessage.getId();
        mockMvc.perform(get(URL).contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk()).andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8));
        verify(inboxService, times(1)).getMessage(mockedMessage.getId());
    }

    /**
     * Tests that the getMessagesById exception behavior returns with a 500 response for the auth route
     *
     * @throws Exception
     */
    @Test
    public void getMessageByIdRequestWithAuthServerErrorTest() throws Exception {
        Message mockedMessage = TestUtils.getMockMessageForUser(TEST_LAN_ID);
        when(inboxService.getMessage(mockedMessage.getId())).thenThrow(new RollbackException("Some service exception was thrown"));
        String URL = "/auth/messages/" + mockedMessage.getId();
        mockMvc.perform(get(URL).contentType(MediaType.APPLICATION_JSON)).andExpect(status().isInternalServerError());
        verify(inboxService, times(1)).getMessage(mockedMessage.getId());
    }

    /**
     * Tests that the getMessageById happy path behavior returns with a 200 response for the non-auth route
     *
     * @throws Exception
     */
    @Test
    public void getMessageByIdRequestWithoutAuthHappyPathTest() throws Exception {
        Message mockedMessage = TestUtils.getMockMessageForUser(TEST_LAN_ID);
        when(inboxService.getMessage(mockedMessage.getId())).thenReturn(new MessageResponseWrapper(mockedMessage));
        setupMockSecurityContext(TEST_LAN_ID);
        String URL = "/users/"+mockedMessage.getLanId()+"/messages/" + mockedMessage.getId();
        mockMvc.perform(get(URL).contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk()).andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8));
        verify(inboxService, times(1)).getMessage(mockedMessage.getId());
    }

    /**
     * Tests that the getMessagesById exception behavior returns with a 500 response for the non-auth route
     *
     * @throws Exception
     */
    @Test
    public void getMessageByIdRequestWithoutAuthServerErrorTest() throws Exception {
        Message mockedMessage = TestUtils.getMockMessageForUser(TEST_LAN_ID);
        when(inboxService.getMessage(mockedMessage.getId())).thenThrow(new RollbackException("Some service exception was thrown"));
        setupMockSecurityContext(TEST_LAN_ID);
        String URL = "/users/"+mockedMessage.getLanId()+"/messages/" + mockedMessage.getId();
        mockMvc.perform(get(URL).contentType(MediaType.APPLICATION_JSON)).andExpect(status().isInternalServerError());
        verify(inboxService, times(1)).getMessage(mockedMessage.getId());
    }

    /*
     * Tests that the getMessageById exception behavior returns with a 403 response for the non-auth route
     *
     * @throws Exception
     */
    @Test
    public void getMessageByIdRequestWithoutAuthAccessDeniedTest() throws Exception {
        Message mockedMessage = TestUtils.getMockMessageForUser(TEST_LAN_ID);
        mockedMessage.setLanId("SomeOthe");
        when(inboxService.getMessage(mockedMessage.getId())).thenReturn(new MessageResponseWrapper(mockedMessage));
        setupMockSecurityContext(TEST_LAN_ID);
        String URL = "/users/"+mockedMessage.getLanId()+"/messages/" + mockedMessage.getId();
        mockMvc.perform(get(URL).contentType(MediaType.APPLICATION_JSON)).andExpect(status().isForbidden());
        verify(inboxService, times(1)).getMessage(mockedMessage.getId());
    }

    /**
     * Tests that the getMessagesForUser happy path behavior returns with a 200 response for the auth route
     *
     * @throws Exception
     */
    @Test
    public void getMessagesRequestWithAuthHappyPathTest() throws Exception {
        List<Message> mockedMessages = TestUtils.getMockMessagesForUser(TEST_LAN_ID, 2);
        when(inboxService.getMessages(TEST_LAN_ID, ExpirationFilter.ACTIVE)).thenReturn(new MessageResponseWrapper(mockedMessages));
        String URL = "/auth/users/" + TEST_LAN_ID + "/messages?active=true";
        mockMvc.perform(get(URL).contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk()).andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8));
        verify(inboxService, times(1)).getMessages(TEST_LAN_ID, ExpirationFilter.ACTIVE);
    }

    /**
     * Tests that the getMessagesForUser exception behavior returns with a 500 response for the auth route
     *
     * @throws Exception
     */
    @Test
    public void getMessagesRequestWithAuthServerErrorTest() throws Exception {
        List<Message> mockedMessages = TestUtils.getMockMessagesForUser(TEST_LAN_ID, 2);
        when(inboxService.getMessages(TEST_LAN_ID, ExpirationFilter.ACTIVE)).thenThrow(new RollbackException("Some service exception was thrown"));
        String URL = "/auth/users/" + TEST_LAN_ID + "/messages?active=true";
        mockMvc.perform(get(URL).contentType(MediaType.APPLICATION_JSON)).andExpect(status().isInternalServerError());
        verify(inboxService, times(1)).getMessages(TEST_LAN_ID, ExpirationFilter.ACTIVE);
    }

    /**
     * Tests that the getMessagesForUser happy path behavior returns with a 200 response for the non-auth route
     *
     * @throws Exception
     */
    @Test
    public void getMessagesRequestWithoutAuthHappyPathTest() throws Exception {
        List<Message> mockedMessages = TestUtils.getMockMessagesForUser(TEST_LAN_ID, 2);
        when(inboxService.getMessages(TEST_LAN_ID, ExpirationFilter.ACTIVE)).thenReturn(new MessageResponseWrapper(mockedMessages));
        setupMockSecurityContext(TEST_LAN_ID);
        String URL = "/users/" + TEST_LAN_ID + "/messages?active=true";
        mockMvc.perform(get(URL).contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk()).andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8));
        verify(inboxService, times(1)).getMessages(TEST_LAN_ID, ExpirationFilter.ACTIVE);
    }

    /**
     * Tests that the getMessagesForUser exception behavior returns with a 500 response for the non-auth route
     *
     * @throws Exception
     */
    @Test
    public void getMessagesRequestWithoutAuthServerErrorTest() throws Exception {
        List<Message> mockedMessages = TestUtils.getMockMessagesForUser(TEST_LAN_ID, 2);
        when(inboxService.getMessages(TEST_LAN_ID, ExpirationFilter.ACTIVE)).thenThrow(new RollbackException("Some service exception was thrown"));
        setupMockSecurityContext(TEST_LAN_ID);
        String URL = "/users/" + TEST_LAN_ID + "/messages?active=true";
        mockMvc.perform(get(URL).contentType(MediaType.APPLICATION_JSON)).andExpect(status().isInternalServerError());
        verify(inboxService, times(1)).getMessages(TEST_LAN_ID, ExpirationFilter.ACTIVE);
    }

    /**
     * Tests that the getMessagesForUser exception behavior returns with a 403 when the passed lanId != sessionId
     *
     * @throws Exception
     */
    @Test
    public void getMessagesRequestWithoutAuthSessionIdNotEqualsPassedLanIdTest() throws Exception {
        setupMockSecurityContext("differentSessionId");
        String URL = "/users/" + TEST_LAN_ID + "/messages?active=true";
        mockMvc.perform(get(URL).contentType(MediaType.APPLICATION_JSON)).andExpect(status().isForbidden());
        verify(inboxService, times(0)).getMessages(TEST_LAN_ID, ExpirationFilter.ACTIVE);
    }

    /**
     * Tests that the createMessage happy path behavior returns with a 200 response
     *
     * @throws Exception
     */
    @Test
    public void createMessageRequestHappyPathTest() throws Exception {
        List<Message> mockedResponseMessages = TestUtils.getMockMessagesForUser(TEST_LAN_ID, 1);
        Message message = TestUtils.getMockMessageForUser(TEST_LAN_ID);
        List<String> lanIds = Collections.singletonList(TEST_LAN_ID);
        String jsonBody = new ObjectMapper().writeValueAsString(new CreateMessageRequest(message, lanIds));
        when(inboxService.createMessage(any(Message.class), any(List.class))).thenReturn(new MessageResponseWrapper(mockedResponseMessages));
        String URL = "/messages";
        mockMvc.perform(post(URL).content(jsonBody).contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk());
        verify(inboxService, times(1)).createMessage(any(Message.class), any(List.class));
    }

    /**
     * Tests that the createMessage server error (RollbackException) returns with a 500 response
     *
     * @throws Exception
     */
    @Test
    public void createMessageRequestServerErrorTest() throws Exception {
        Message message = TestUtils.getMockMessageForUser(TEST_LAN_ID);
        List<String> lanIds = Collections.singletonList(TEST_LAN_ID);
        String jsonBody = new ObjectMapper().writeValueAsString(new CreateMessageRequest(message, lanIds));
        when(inboxService.createMessage(any(Message.class), any(List.class))).thenThrow(new RollbackException("Some service exception occured"));
        String URL = "/messages";
        mockMvc.perform(post(URL).content(jsonBody).contentType(MediaType.APPLICATION_JSON)).andExpect(status().isInternalServerError());
        verify(inboxService, times(1)).createMessage(any(Message.class), any(List.class));
    }

    /**
     * Tests that the createMessage validation error (FormValidationException) returns with a 400 response
     *
     * @throws Exception
     */
    @Test
    public void createMessageRequestBadRequestTest() throws Exception {
        Message message = TestUtils.getMockMessageForUser(TEST_LAN_ID);
        List<String> lanIds = Collections.emptyList();
        String jsonBody = new ObjectMapper().writeValueAsString(new CreateMessageRequest(message, lanIds));
        String URL = "/messages";
        mockMvc.perform(post(URL).content(jsonBody).contentType(MediaType.APPLICATION_JSON)).andExpect(status().isBadRequest());
        verify(inboxService, times(0)).createMessage(any(Message.class), any(List.class));
    }

    /**
     * Tests that the deleteMessage happy path behavior returns with a 200 response for the non-auth route
     *
     * @throws Exception
     */
    @Test
    public void deleteMessageByIdRequestWithoutAuthHappyPathTest() throws Exception {
        Message mockedMessage = TestUtils.getMockMessageForUser(TEST_LAN_ID);
        when(inboxService.getMessage(mockedMessage.getId())).thenReturn(new MessageResponseWrapper(mockedMessage));
        when(inboxService.deleteMessage(mockedMessage.getId())).thenReturn(new MessageResponseWrapper(mockedMessage));
        setupMockSecurityContext(TEST_LAN_ID);
        String URL = "/users/"+mockedMessage.getLanId()+"/messages/" + mockedMessage.getId();
        mockMvc.perform(delete(URL).contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk()).andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8));
        verify(inboxService, times(1)).deleteMessage(mockedMessage.getId());
    }

    /**
     * Tests that the deleteMessage exception behavior returns with a 500 response for the non-auth route
     *
     * @throws Exception
     */
    @Test
    public void deleteMessageByIdRequestWithoutAuthServerErrorTest() throws Exception {
        Message mockedMessage = TestUtils.getMockMessageForUser(TEST_LAN_ID);
        when(inboxService.getMessage(mockedMessage.getId())).thenReturn(new MessageResponseWrapper(mockedMessage));
        when(inboxService.deleteMessage(mockedMessage.getId())).thenThrow(new RollbackException("Some service exception was thrown"));
        setupMockSecurityContext(TEST_LAN_ID);
        String URL = "/users/"+mockedMessage.getLanId()+"/messages/" + mockedMessage.getId();
        mockMvc.perform(delete(URL).contentType(MediaType.APPLICATION_JSON)).andExpect(status().isInternalServerError());
        verify(inboxService, times(1)).deleteMessage(mockedMessage.getId());
    }


    /**
     * Tests that the deleteMessage exception behavior returns with a 403 response for the non-auth route
     *
     * @throws Exception
     */
    @Test
    public void deleteMessageByIdRequestWithoutAuthAccessDeniedTest() throws Exception {
        Message mockedMessage = TestUtils.getMockMessageForUser(TEST_LAN_ID);
        mockedMessage.setLanId("SomeOthe");
        when(inboxService.getMessage(mockedMessage.getId())).thenReturn(new MessageResponseWrapper(mockedMessage));
        setupMockSecurityContext(TEST_LAN_ID);
        String URL = "/users/"+mockedMessage.getLanId().trim()+"/messages/" + mockedMessage.getId();
        System.out.println(URL);
        mockMvc.perform(delete(URL).contentType(MediaType.APPLICATION_JSON)).andExpect(status().isForbidden());
        
        verify(inboxService, times(0)).deleteMessage(mockedMessage.getId());
    }

    /**
     * Tests that the deleteMessage happy path behavior returns with a 200 response for the auth route
     *
     * @throws Exception
     */
    @Test
    public void deleteMessageByIdRequestWithAuthHappyPathTest() throws Exception {
        Message mockedMessage = TestUtils.getMockMessageForUser(TEST_LAN_ID);
        when(inboxService.deleteMessage(mockedMessage.getId())).thenReturn(new MessageResponseWrapper(mockedMessage));
        String URL = "/auth/messages/" + mockedMessage.getId();
        mockMvc.perform(delete(URL).contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk()).andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8));
        verify(inboxService, times(1)).deleteMessage(mockedMessage.getId());
    }

    /**
     * Tests that the deleteMessage exception behavior returns with a 500 response for the auth route
     *
     * @throws Exception
     */
    @Test
    public void deleteMessageByIdRequestWithAuthServerErrorTest() throws Exception {
        Message mockedMessage = TestUtils.getMockMessageForUser(TEST_LAN_ID);
        when(inboxService.deleteMessage(mockedMessage.getId())).thenThrow(new RollbackException("Some service exception was thrown"));
        String URL = "/auth/messages/" + mockedMessage.getId();
        mockMvc.perform(delete(URL).contentType(MediaType.APPLICATION_JSON)).andExpect(status().isInternalServerError());
        verify(inboxService, times(1)).deleteMessage(mockedMessage.getId());
    }

    /**
     * Tests that the deleteExpiredMessages happy path behavior returns with a 200 response
     *
     * @throws Exception
     */
    @Test
    public void deleteMessagesRequestHappyPathTest() throws Exception {
        when(inboxService.deleteExpiredMessages(TEST_LAN_ID)).thenReturn(new BaseResponseWrapper());
        setupMockSecurityContext(TEST_LAN_ID);
        String URL = "/users/" + TEST_LAN_ID + "/messages";
        mockMvc.perform(delete(URL).contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk()).andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8));
        verify(inboxService, times(1)).deleteExpiredMessages(TEST_LAN_ID);
    }

    /**
     * Tests that the deleteExpiredMessages exception behavior returns with a 500 response
     *
     * @throws Exception
     */
    @Test
    public void deleteMessagesRequestServerErrorTest() throws Exception {
        when(inboxService.deleteExpiredMessages(TEST_LAN_ID)).thenThrow(new RollbackException("Some service exception was thrown"));
        setupMockSecurityContext(TEST_LAN_ID);
        String URL = "/users/" + TEST_LAN_ID + "/messages";
        mockMvc.perform(delete(URL).contentType(MediaType.APPLICATION_JSON)).andExpect(status().isInternalServerError());
        verify(inboxService, times(1)).deleteExpiredMessages(TEST_LAN_ID);
    }

    /**
     * Tests that the deleteExpiredMessages exception behavior returns with a 403 when the passed lanId != sessionId
     *
     * @throws Exception
     */
    @Test
    public void deleteMessagesRequestSessionIdNotEqualsPassedLanIdTest() throws Exception {
        setupMockSecurityContext("differentSessionId");
        String URL = "/users/" + TEST_LAN_ID + "/messages";
        mockMvc.perform(delete(URL).contentType(MediaType.APPLICATION_JSON)).andExpect(status().isForbidden());
        verify(inboxService, times(0)).deleteExpiredMessages(TEST_LAN_ID);
    }

    /**
     * Mocks the security context to be that passed user Id
     *
     * @param sessionId
     */
    private void setupMockSecurityContext(String sessionId) {
        Identity id = new Identity();
        id.setId(sessionId);
        Authentication auth = Mockito.mock(Authentication.class);
        SecurityContext ctx = Mockito.mock(SecurityContext.class);
        when(ctx.getAuthentication()).thenReturn(auth);
        when((auth.getPrincipal())).thenReturn(new UserPrincipal("username", "pwd", Collections.emptyList(), new UserData(id)));
        SecurityContextHolder.setContext(ctx);
    }
}
