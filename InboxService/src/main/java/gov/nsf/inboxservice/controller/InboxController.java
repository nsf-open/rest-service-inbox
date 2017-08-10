package gov.nsf.inboxservice.controller;

import gov.nsf.common.exception.FormValidationException;
import gov.nsf.common.exception.RollbackException;
import gov.nsf.common.model.BaseResponseWrapper;
import gov.nsf.common.model.ExpirationFilter;
import gov.nsf.exception.CommonUtilException;
import gov.nsf.inboxservice.api.model.*;
import gov.nsf.inboxservice.common.util.Constants;
import gov.nsf.inboxservice.validator.*;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.*;

import gov.nsf.common.ember.model.EmberModel;

import gov.nsf.inboxservice.api.service.InboxService;

/**
 * Controller to serve /inboxservice requests
 *
 */
@RestController
public class InboxController extends InboxBaseController {

    @Autowired
    private InboxService inboxService;

    /**
     * GET handler /auth/messages/{msgId}
     *
     * @return JSON MessageResponseWrapper
     */
    @RequestMapping(value = "/auth/messages/{msgId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Get Message by ID with authentication",
            notes = "This API returns the Message given its message ID.",
            response = MessageResponseWrapper.class)
    @ApiResponses(value = {
            @ApiResponse(code = 500, message = "Internal Server Error"),
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 404, message = "Not Found")})
    @ResponseBody
    public EmberModel getMessageById(@PathVariable("msgId") String msgId) throws RollbackException, FormValidationException, CommonUtilException {
        GetMessageValidator.validateRequest(msgId);

        MessageResponseWrapper wrapper = inboxService.getMessage(msgId);
        return new EmberModel.Builder<MessageResponseWrapper>(Constants.MESSAGE_RESPONSE_WRAPPER, wrapper).build();

    }

    /**
     * GET handler /messages/{msgId}
     *
     * @return JSON MessageResponseWrapper
     */
    @RequestMapping(value = "/users/{lanId}/messages/{msgId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Get Message by ID for the session user",
            notes = "This API returns the Message given its message ID. Message must be owned by the session user",
            response = MessageResponseWrapper.class)
    @ApiResponses(value = {
            @ApiResponse(code = 500, message = "Internal Server Error"),
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 404, message = "Not Found"),
            @ApiResponse(code = 403, message = "Access Denied")})
    @ResponseBody
    public EmberModel getMessageByIdForSessionUser(@PathVariable("lanId") String lanId,@PathVariable("msgId") String msgId) throws RollbackException, FormValidationException, CommonUtilException {
        GetMessagesValidator.validateRequest(lanId);
        GetMessageValidator.validateRequest(msgId);
       

        MessageResponseWrapper wrapper = inboxService.getMessage(msgId);
        String sessionId = getUserIdentity().getId();
        if( !lanId.equalsIgnoreCase(sessionId) ){
            throw new AccessDeniedException(Constants.ERROR_ACCESS_DENIED);
        }

        return new EmberModel.Builder<MessageResponseWrapper>(Constants.MESSAGE_RESPONSE_WRAPPER, wrapper).build();

    }

    /**
     * GET handler for /auth/users/{lanId}/messages/
     *
     * @return JSON MessageResponseWrapper
     */
    @RequestMapping(value = "/auth/users/{lanId}/messages", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @ApiOperation(value = "Get Message for user",
            notes = "This API returns Messages for user.",
            response = MessageResponseWrapper.class)
    @ApiResponses(value = {
            @ApiResponse(code = 500, message = "Internal Server Error"),
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 404, message = "Not Found")})
    public EmberModel getMessagesForUser(@PathVariable("lanId") String lanId, @RequestParam(value="active", required = false, defaultValue = "ALL") String active) throws RollbackException, FormValidationException {
        GetMessagesValidator.validateRequest(lanId);

        MessageResponseWrapper wrapper = inboxService.getMessages(lanId, ExpirationFilter.forValue(active));
        return new EmberModel.Builder<MessageResponseWrapper>(Constants.MESSAGE_RESPONSE_WRAPPER, wrapper).build();

    }

    /**
     * GET handler for /users/{lanId}/messages/
     *
     * @return JSON MessageResponseWrapper
     */
    @RequestMapping(value = "/users/{lanId}/messages", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @ApiOperation(value = "Get Message for session user",
            notes = "This API returns Messages for session user.",
            response = MessageResponseWrapper.class)
    @ApiResponses(value = {
            @ApiResponse(code = 500, message = "Internal Server Error"),
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 404, message = "Not Found"),
            @ApiResponse(code = 403, message = "Access Denied")})
    public EmberModel getMessagesForSessionUser(@PathVariable("lanId") String lanId, @RequestParam(value="active", required = false, defaultValue = "ALL") String active) throws RollbackException, CommonUtilException, FormValidationException {
        GetMessagesValidator.validateRequest(lanId);

        //Compare lanId w/ sessionId
        String sessionId = getUserIdentity().getId();
        if( !lanId.equalsIgnoreCase(sessionId) ){
            throw new AccessDeniedException(Constants.ERROR_ACCESS_DENIED);
        }
        MessageResponseWrapper wrapper = inboxService.getMessages(lanId, ExpirationFilter.forValue(active));
        return new EmberModel.Builder<MessageResponseWrapper>(Constants.MESSAGE_RESPONSE_WRAPPER, wrapper).build();

    }

    /**
     * POST handler for /messages
     *
     * @return JSON MessageResponseWrapper
     */
    @RequestMapping(value = {"/auth/messages","/messages"}, method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @ApiOperation(value = "Create Message for single and multiple user",
            notes = "This API creates Messages for single and multiple user.",
            response = MessageResponseWrapper.class)
    @ApiResponses(value = {
            @ApiResponse(code = 500, message = "Internal Server Error"),
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 404, message = "Not Found")})
    public EmberModel createMessage(@RequestBody CreateMessageRequest request) throws RollbackException, FormValidationException {
        CreateMessageValidator.validateRequest(request);

        MessageResponseWrapper wrapper = inboxService.createMessage(request.getMessage(), request.getLanIds());
        return new EmberModel.Builder<MessageResponseWrapper>(Constants.MESSAGE_RESPONSE_WRAPPER, wrapper).build();
    }


    /**
     * DELETE handler /auth/messages/{msgId} (auth route)
     *
     * @return JSON MessageResponseWrapper
     */
    @RequestMapping(value = "/auth/messages/{msgId}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Deletes Message by ID",
            notes = "This API deletes the Message given its message ID",
            response = MessageResponseWrapper.class)
    @ApiResponses(value = {
            @ApiResponse(code = 500, message = "Internal Server Error"),
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 404, message = "Not Found")})
    @ResponseBody
    public EmberModel deleteMessageById(@PathVariable("msgId") String msgId) throws RollbackException, FormValidationException {
        DeleteMessageValidator.validateRequest(msgId);

        MessageResponseWrapper wrapper = inboxService.deleteMessage(msgId);
        return new EmberModel.Builder<MessageResponseWrapper>(Constants.MESSAGE_RESPONSE_WRAPPER, wrapper).build();

    }

    /**
     * DELETE handler /messages/{msgId} (non-auth route)
     *
     * @return JSON MessageResponseWrapper
     */
    @RequestMapping(value = "/users/{lanId}/messages/{msgId}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Deletes Message by ID for the session user",
            notes = "This API deletes the Message given its message ID. Message must be owned by the session user",
            response = MessageResponseWrapper.class)
    @ApiResponses(value = {
            @ApiResponse(code = 500, message = "Internal Server Error"),
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 404, message = "Not Found"),
            @ApiResponse(code = 403, message = "Access Denied")})
    @ResponseBody
    public EmberModel deleteMessageByIdForSessionUser(@PathVariable("lanId") String lanId,@PathVariable("msgId") String msgId) throws RollbackException, FormValidationException, CommonUtilException {
        GetMessagesValidator.validateRequest(lanId);
        DeleteMessageValidator.validateRequest(msgId);
        

        Message message = inboxService.getMessage(msgId).getMessages().get(0);

        String sessionId = getUserIdentity().getId();
        if( !lanId.equalsIgnoreCase(sessionId) || message.getType() == MessageType.Task){
            throw new AccessDeniedException(Constants.ERROR_ACCESS_DENIED);
        }

        MessageResponseWrapper wrapper = inboxService.deleteMessage(msgId);
        return new EmberModel.Builder<MessageResponseWrapper>(Constants.BASE_RESPONSE_WRAPPER, wrapper).build();

    }

    /**
     * DELETE handler /users/{lanId}/messages (non-auth route)
     *
     * @return JSON BaseResponseWrapper
     */
    @RequestMapping(value = "/users/{lanId}/messages", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Deletes Expired Messages for the session user",
            notes = "This API deletes the expired Information Messages for the session user",
            response = MessageResponseWrapper.class)
    @ApiResponses(value = {
            @ApiResponse(code = 500, message = "Internal Server Error"),
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 403, message = "Access Denied")})
    @ResponseBody
    public EmberModel deleteAllExpiredMessagesForSessionUser(@PathVariable("lanId") String lanId) throws RollbackException, FormValidationException, CommonUtilException {
        DeleteExpiredMessagesValidator.validateRequest(lanId);

        String sessionId = getUserIdentity().getId();
        if( !lanId.equalsIgnoreCase(sessionId) ){
            throw new AccessDeniedException(Constants.ERROR_ACCESS_DENIED);
        }

        BaseResponseWrapper wrapper = inboxService.deleteExpiredMessages(lanId);
        return new EmberModel.Builder<BaseResponseWrapper>(Constants.BASE_RESPONSE_WRAPPER, wrapper).build();

    }



}
