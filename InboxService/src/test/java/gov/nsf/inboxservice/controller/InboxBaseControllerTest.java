package gov.nsf.inboxservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import gov.nsf.common.exception.FormValidationException;
import gov.nsf.common.exception.ResourceNotFoundException;
import gov.nsf.common.exception.RollbackException;
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
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * JUnit tests for InboxController
 */
@RunWith(MockitoJUnitRunner.class)
@WebAppConfiguration
public class InboxBaseControllerTest {

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

    @Test
    public void formValidationExceptionTest() throws Exception {
        String URL = "/users/"+TEST_LAN_ID+"/messages/" + "fish";
        mockMvc.perform(delete(URL).contentType(MediaType.APPLICATION_JSON)).andExpect(status().isBadRequest());
    }

    @Test
    public void resourceNotFoundExceptionTest() throws Exception {
        String nonExistingId = "1000";
        String URL = "/auth/messages/" + nonExistingId;
        when(inboxService.deleteMessage(nonExistingId)).thenThrow(new ResourceNotFoundException("ID not found"));
        mockMvc.perform(delete(URL).contentType(MediaType.APPLICATION_JSON)).andExpect(status().isNotFound());
    }
}
