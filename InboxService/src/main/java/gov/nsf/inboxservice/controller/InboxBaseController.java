package gov.nsf.inboxservice.controller;


import gov.nsf.common.ember.model.EmberModel;
import gov.nsf.common.exception.FormValidationException;
import gov.nsf.common.exception.ResourceNotFoundException;
import gov.nsf.common.exception.RollbackException;
import gov.nsf.common.model.BaseError;
import gov.nsf.common.model.BaseResponseWrapper;

import gov.nsf.components.authorization.model.UserPrincipal;
import gov.nsf.components.rolemanager.model.Identity;
import gov.nsf.components.rolemanager.model.UserData;
import gov.nsf.exception.CommonUtilException;
import gov.nsf.inboxservice.common.util.Constants;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;


/**
 * BaseController for InboxService
 */
@RestController
public class InboxBaseController {

    private static final Logger LOGGER = Logger.getLogger(InboxBaseController.class);
    /**
     * Response handler for HttpMessageNotReadableException exceptions
     * @param ex
     * @return BaseResponseWrapper w/ empty Letter field
     */
    @ExceptionHandler({HttpMessageNotReadableException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public EmberModel processFormValidationException(HttpMessageNotReadableException ex) {
        BaseResponseWrapper response = new BaseResponseWrapper();
        response.addError(new BaseError(Constants.UNABLE_TO_READ_JSON, ex.getMessage() ));
        LOGGER.error("InboxBaseController - Bad Request : " + ex);
        return new EmberModel.Builder<BaseResponseWrapper>(Constants.BASE_RESPONSE_WRAPPER, response).build();
    }

    /**
     * Response handler for FormValidationException exceptions
     * @param ex
     * @return BaseResponseWrapper w/ empty Letter field
     */
    @ExceptionHandler({ FormValidationException.class })
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public EmberModel processFormValidationException(FormValidationException ex) {
        BaseResponseWrapper response = new BaseResponseWrapper();
        response.setErrors(ex.getValidationErrors());
        LOGGER.error("InboxBaseController - Form Validation Exception : " + ex);
        return new EmberModel.Builder<BaseResponseWrapper>(Constants.BASE_RESPONSE_WRAPPER, response).build();
    }

    /**
     * Response handler for MissingServletRequestParameterException exceptions
     * @param ex
     * @return BaseResponseWrapper w/ empty Letter field
     */
    @ExceptionHandler({ MissingServletRequestParameterException.class })
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public EmberModel processFormValidationException(MissingServletRequestParameterException ex) {
        BaseResponseWrapper response = new BaseResponseWrapper();
        response.addError(new BaseError(Constants.INVALID_REQUEST_PARAMETER, ex.getMessage()));
        LOGGER.error("InboxBaseController - Invalid Request Parameters : " + ex);
        return new EmberModel.Builder<BaseResponseWrapper>(Constants.BASE_RESPONSE_WRAPPER, response).build();
    }

    /**
     * Response handler for ResourceNotFoundException exceptions
     * @param ex
     * @return BaseResponseWrapper w/ empty Letter field
     */
    @ExceptionHandler({ResourceNotFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    public EmberModel processResourceNotFoundException(ResourceNotFoundException ex) {
        BaseResponseWrapper response = new BaseResponseWrapper();
        response.addError(new BaseError(Constants.DB_TRANSACTION_ERROR, ex.getErrMsg()));
        LOGGER.error("InboxBaseController - Missing Message ID Field : " + ex);
        return new EmberModel.Builder<BaseResponseWrapper>(Constants.BASE_RESPONSE_WRAPPER, response).build();
    }

    /**
     * Response handler for RollbackException exceptions
     * @param ex
     * @return BaseResponseWrapper w/ empty Letter field
     */
    @ExceptionHandler({RollbackException.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public EmberModel processRollbackException(RollbackException ex) {
        BaseResponseWrapper response = new BaseResponseWrapper();
        response.addError(new BaseError(Constants.DB_TRANSACTION_ERROR, ex.getErrMsg()));
        LOGGER.error("InboxBaseController - DB Transaction Error : " + ex);
        return new EmberModel.Builder<BaseResponseWrapper>(Constants.BASE_RESPONSE_WRAPPER, response).build();
    }

    /**
     * Response handler for CommonUtilException exceptions
     * @param ex
     * @return BaseResponseWrapper w/ empty Letter field
     */
    @ExceptionHandler({CommonUtilException.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public EmberModel processCommonUtilException(CommonUtilException ex) {
        BaseResponseWrapper response = new BaseResponseWrapper();
        response.addError(new BaseError(Constants.MSG_LAN_ID_FIELD, ex.getErrMsg()));
        LOGGER.error("InboxBaseController - Common Util Exception : " + ex);
        return new EmberModel.Builder<BaseResponseWrapper>(Constants.BASE_RESPONSE_WRAPPER, response).build();
    }

    /**
     * Response handler for AccessDeniedException exceptions
     * @param ex
     * @return BaseResponseWrapper w/ empty Letter field
     */
    @ExceptionHandler({AccessDeniedException.class})
    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ResponseBody
    public EmberModel processException(AccessDeniedException ex) {
        BaseResponseWrapper response = new BaseResponseWrapper();
        response.addError(new BaseError(Constants.ACCESS_DENIED_EXCEPTION, ex.getMessage()));
        LOGGER.error("InboxBaseController - Access Denied Exception : " + ex);
        return new EmberModel.Builder<BaseResponseWrapper>(Constants.BASE_RESPONSE_WRAPPER, response).build();

    }

    /**
     * Response handler for Exception exceptions
     * @param ex
     * @return BaseResponseWrapper w/ empty Letter field
     */
    @ExceptionHandler({Exception.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public EmberModel processException(Exception ex) {
        BaseResponseWrapper response = new BaseResponseWrapper();
        response.addError(new BaseError(Constants.SERVER_500_ERROR, ExceptionUtils.getFullStackTrace(ex)));
        LOGGER.error("InboxBaseController - Internal Server Error : " + ex);
        return new EmberModel.Builder<BaseResponseWrapper>(Constants.BASE_RESPONSE_WRAPPER, response).build();
    }

    protected Identity getUserIdentity() throws CommonUtilException {
        UserPrincipal userPrincipal = null;
        UserData userData = null;
        Identity identity = null;
        Object o = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        LOGGER.debug(o.getClass());
        if (o instanceof UserPrincipal) {
            userPrincipal = (UserPrincipal) (o);
            userData = userPrincipal.getUserData();
            identity = userData.getIdentity();
        } else {
            throw new CommonUtilException("User Identity Not Found");
        }

        return identity;
    }

}
