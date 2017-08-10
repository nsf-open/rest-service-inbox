package gov.nsf.inboxservice.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import gov.mynsf.common.restclient.NsfRestTemplate;
import gov.nsf.common.exception.RollbackException;
import gov.nsf.common.model.BaseResponseWrapper;
import gov.nsf.common.model.ExpirationFilter;
import gov.nsf.inboxservice.api.model.CreateMessageRequest;
import gov.nsf.inboxservice.api.model.Message;
import gov.nsf.inboxservice.api.model.MessageResponseWrapper;
import gov.nsf.inboxservice.api.service.InboxService;
import org.apache.commons.codec.binary.Base64;
import org.springframework.http.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.List;


/**
 * InboxService REST Client
 */
public class InboxServiceClientImpl implements InboxService {

    private String inboxServiceURL;
    private String inboxServiceUserName;
    private String inboxServicePassword;
    private boolean authenticationRequired;
    private int requestTimeout;


    @Override
    public MessageResponseWrapper getMessage(String msgId) throws RollbackException {
        String URL = inboxServiceURL + "/messages/" + msgId;
        String responseBody = sendRequest(URL, HttpMethod.GET, null);
        MessageResponseWrapper wrapper = null;
        try {
            wrapper = (MessageResponseWrapper)extractWrapperResponse(responseBody, MessageResponseWrapper.class, "messageResponseWrapper");
        } catch (IOException ex){
            throw new RollbackException(ex);
        }

        return wrapper;
    }

    @Override
    public MessageResponseWrapper getMessages(String lanId, ExpirationFilter activeFilter) throws RollbackException {

        String URL = inboxServiceURL + "/users/" + lanId + "/messages?active=" + activeFilter.getCode();
        String responseBody = sendRequest(URL, HttpMethod.GET, null);
        MessageResponseWrapper wrapper = null;
        try {
            wrapper = (MessageResponseWrapper)extractWrapperResponse(responseBody, MessageResponseWrapper.class, "messageResponseWrapper");
        } catch (IOException ex){
            throw new RollbackException(ex);
        }

        return wrapper;
    }

    @Override
    public MessageResponseWrapper createMessage(Message message, List<String> lanIds) throws RollbackException {
        String jsonBody = null;
        try {
           jsonBody = new ObjectMapper().writeValueAsString(new CreateMessageRequest(message, lanIds));
        } catch (JsonProcessingException e) {
            throw new RollbackException(e);
        }

        String URL = inboxServiceURL + "/messages";
        String responseBody = sendRequest(URL, HttpMethod.POST, jsonBody);
        MessageResponseWrapper wrapper = null;
        try {
            wrapper = (MessageResponseWrapper)extractWrapperResponse(responseBody, MessageResponseWrapper.class, "messageResponseWrapper");
        } catch (IOException ex){
            throw new RollbackException(ex);
        }

        return wrapper;
    }

    @Override
    public MessageResponseWrapper deleteMessage(String msgId) throws RollbackException {
        String URL = inboxServiceURL + "/messages/" + msgId;
        String responseBody = sendRequest(URL, HttpMethod.DELETE, null);
        MessageResponseWrapper wrapper = null;
        try {
            wrapper = (MessageResponseWrapper)extractWrapperResponse(responseBody, MessageResponseWrapper.class, "messageResponseWrapper");
        } catch (IOException ex){
            throw new RollbackException(ex);
        }

        return wrapper;
    }

    @Override
    public BaseResponseWrapper deleteExpiredMessages(String lanId) throws RollbackException {
        String URL = inboxServiceURL + "/users/" + lanId + "/messages";
        String responseBody = sendRequest(URL, HttpMethod.DELETE, null);
        BaseResponseWrapper wrapper = null;
        try {
            wrapper = (BaseResponseWrapper) extractWrapperResponse(responseBody, BaseResponseWrapper.class, "baseResponseWrapper");
        } catch (IOException ex){
            throw new RollbackException(ex);
        }

        return wrapper;
    }


    /**
     * Helper method that extracts Response object
     *
     * @param body - json body from response
     * @param wrapperClass - response class type
     * @param jsonRoot - json root variable name
     * @return BaseResponseWrapper
     * @throws IOException
     */
    private BaseResponseWrapper extractWrapperResponse(String body, Class<? extends BaseResponseWrapper> wrapperClass, String jsonRoot)
            throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode node = mapper.readTree(body).findValue(jsonRoot) != null ? mapper.readTree(body).findValue(jsonRoot) : mapper.readTree(body).findValue("baseResponseWrapper");
        BaseResponseWrapper wrapper = (BaseResponseWrapper)mapper.readValue(node.toString(), wrapperClass);
        return wrapper;
    }

    private HttpEntity<String> createHttpEntityWithAuthAndBody(String userName, String password, String body) {
        String auth = userName + ":" + password;
        byte[] encodedAuth = Base64.encodeBase64(auth.getBytes());
        String authHeader = "Basic " + new String(encodedAuth);
        HttpHeaders headers = getBaseHeaders();
        headers.set("Authorization", authHeader);
        return new HttpEntity<String>(body, headers);
    }

    private HttpHeaders getBaseHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);
        headers.set("Content-Type", MediaType.APPLICATION_JSON_VALUE);

        return headers;
    }

    private String sendRequest(String URL, HttpMethod httpMethod, String jsonBody ){

        RestTemplate restTemplate = null;

        try {
            restTemplate = NsfRestTemplate.setupRestTemplate(authenticationRequired, requestTimeout);
        } catch (KeyStoreException | KeyManagementException | NoSuchAlgorithmException e) {

        }

        HttpEntity<String> httpEntity = null;
        if (authenticationRequired) {
            httpEntity = createHttpEntityWithAuthAndBody(inboxServiceUserName, inboxServicePassword, jsonBody);
        } else {
            httpEntity = new HttpEntity<String>(jsonBody, getBaseHeaders());
        }

        ResponseEntity<String> response = null;
        String responseBody = null;

        try {
            response = restTemplate.exchange(URL, httpMethod, httpEntity, String.class);
            responseBody = response.getBody();
        } catch (HttpClientErrorException ex) {
            responseBody = ex.getResponseBodyAsString();
        } catch (HttpServerErrorException ex){
            responseBody = ex.getResponseBodyAsString();
        }

        return responseBody;
    }

    public String getInboxServiceURL() {
        return inboxServiceURL;
    }

    public void setInboxServiceURL(String inboxServiceURL) {
        this.inboxServiceURL = inboxServiceURL;
    }

    public String getInboxServiceUserName() {
        return inboxServiceUserName;
    }

    public void setInboxServiceUserName(String inboxServiceUserName) {
        this.inboxServiceUserName = inboxServiceUserName;
    }

    public String getInboxServicePassword() {
        return inboxServicePassword;
    }

    public void setInboxServicePassword(String inboxServicePassword) {
        this.inboxServicePassword = inboxServicePassword;
    }

    public boolean isAuthenticationRequired() {
        return authenticationRequired;
    }

    public void setAuthenticationRequired(boolean authenticationRequired) {
        this.authenticationRequired = authenticationRequired;
    }

    public int getRequestTimeout() {
        return requestTimeout;
    }

    public void setRequestTimeout(int requestTimeout) {
        this.requestTimeout = requestTimeout;
    }


}
