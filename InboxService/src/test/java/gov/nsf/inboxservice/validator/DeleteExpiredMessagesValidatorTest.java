package gov.nsf.inboxservice.validator;

import gov.nsf.common.exception.FormValidationException;
import gov.nsf.common.model.BaseError;
import gov.nsf.inboxservice.common.util.Constants;
import gov.nsf.inboxservice.common.util.TestUtils;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * JUnit tests for DeleteExpiredMessages class
 */
public class DeleteExpiredMessagesValidatorTest {


    /**
     * Validates the happy path behavior when the lanId is valid
     *
     * Expects that no exceptions are thrown
     * @throws Exception
     */
    @Test
    public void validateRequestHappyPath() throws Exception {
        DeleteExpiredMessagesValidator.validateRequest("test");
    }

    /**
     * Validates the behavior when the passed lanId is null
     *
     * FormValidationException with the appropriate BaseError(s) is expected
     *
     * @throws Exception
     */
    @Test
    public void validateRequestNullLanIdTest() throws Exception {
        try {
            DeleteExpiredMessagesValidator.validateRequest(null);
            fail("Expected FormValidationException");
        } catch(FormValidationException ex){
            assertEquals(1, ex.getValidationErrors().size());
            TestUtils.assertContains(ex.getValidationErrors(),
                    new BaseError(Constants.MSG_LAN_ID_FIELD, Constants.INVALID_LAN_ID + Constants.MSG_LAN_ID_FIELD));
        }
    }

    /**
     * Validates the behavior when the passed lanId is an empty string
     *
     * FormValidationException with the appropriate BaseError(s) is expected
     *
     * @throws Exception
     */
    @Test
    public void validateRequestEmptyStringLanIdTest() throws Exception {
        try {
            DeleteExpiredMessagesValidator.validateRequest("");
            fail("Expected FormValidationException");
        } catch(FormValidationException ex){
            assertEquals(1, ex.getValidationErrors().size());
            TestUtils.assertContains(ex.getValidationErrors(),
                    new BaseError(Constants.MSG_LAN_ID_FIELD, Constants.INVALID_LAN_ID + Constants.MSG_LAN_ID_FIELD));
        }
    }

    /**
     * Validates the behavior when the passed lanId contains invalid characters
     *
     * FormValidationException with the appropriate BaseError(s) is expected
     *
     * @throws Exception
     */
    @Test
    public void validateRequestNonUTF8StringLanIdTest() throws Exception {
        try {
            DeleteExpiredMessagesValidator.validateRequest("©");
            fail("Expected FormValidationException");
        } catch(FormValidationException ex){
            assertEquals(1, ex.getValidationErrors().size());
            TestUtils.assertContains(ex.getValidationErrors(),
                    new BaseError(Constants.MSG_LAN_ID_FIELD, Constants.INVALID_LAN_ID + Constants.MSG_LAN_ID_FIELD));
        }
    }

    /**
     * Validates the behavior when the passed lanId is too long
     *
     * FormValidationException with the appropriate BaseError(s) is expected
     *
     * @throws Exception
     */
    @Test
    public void validateRequestLanIdTooLongTest() throws Exception {
        try {
            DeleteExpiredMessagesValidator.validateRequest("AAAAAAAAA");
            fail("Expected FormValidationException");
        } catch(FormValidationException ex){
            assertEquals(1, ex.getValidationErrors().size());
            TestUtils.assertContains(ex.getValidationErrors(),
                    new BaseError(Constants.MSG_LAN_ID_FIELD, Constants.INVALID_LAN_ID + Constants.MSG_LAN_ID_FIELD));
        }
    }
}
