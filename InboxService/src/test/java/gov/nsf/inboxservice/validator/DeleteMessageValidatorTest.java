package gov.nsf.inboxservice.validator;

import gov.nsf.common.exception.FormValidationException;
import gov.nsf.common.model.BaseError;
import gov.nsf.inboxservice.common.util.Constants;
import gov.nsf.inboxservice.common.util.TestUtils;
import org.h2.command.dml.Delete;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * JUnit tests for DeleteMessageValidator class
 */
public class DeleteMessageValidatorTest {


    /**
     * Validates the happy path behavior when the msgId is valid
     *
     * Expects that no exceptions are thrown
     * @throws Exception
     */
    @Test
    public void validateRequestHappyPath() throws Exception {
        DeleteMessageValidator.validateRequest("01");
    }

    /**
     * Validates the behavior when the passed msgId is null
     *
     * FormValidationException with the appropriate BaseError(s) is expected
     *
     * @throws Exception
     */
    @Test
    public void validateRequestNullMsgIdTest() throws Exception {
        try {
            DeleteMessageValidator.validateRequest(null);
            fail("Expected FormValidationException");
        } catch(FormValidationException ex){
            assertEquals(1, ex.getValidationErrors().size());
            TestUtils.assertContains(ex.getValidationErrors(),
                    new BaseError(Constants.MSG_ID_FIELD, Constants.MISSING_NULL_EMPTY_FIELD + Constants.MSG_ID_FIELD));
        }
    }

    /**
     * Validates the behavior when the passed msgId is an empty string
     *
     * FormValidationException with the appropriate BaseError(s) is expected
     *
     * @throws Exception
     */
    @Test
    public void validateRequestEmptyStringMsgIdTest() throws Exception {
        try {
            DeleteMessageValidator.validateRequest("");
            fail("Expected FormValidationException");
        } catch(FormValidationException ex){
            assertEquals(1, ex.getValidationErrors().size());
            TestUtils.assertContains(ex.getValidationErrors(),
                    new BaseError(Constants.MSG_ID_FIELD, Constants.MISSING_NULL_EMPTY_FIELD + Constants.MSG_ID_FIELD));
        }
    }

    /**
     * Validates the behavior when the passed msgId contains invalid characters
     *
     * FormValidationException with the appropriate BaseError(s) is expected
     *
     * @throws Exception
     */
    @Test
    public void validateRequestNonUTF8StringLanIdTest() throws Exception {
        try {
            DeleteMessageValidator.validateRequest("©");
            fail("Expected FormValidationException");
        } catch(FormValidationException ex){
            assertEquals(1, ex.getValidationErrors().size());
            TestUtils.assertContains(ex.getValidationErrors(),
                    new BaseError(Constants.MSG_ID_FIELD, Constants.MSG_ID_NON_NUMERIC));
        }
    }

    /**
     * Validates the behavior when the passed msgId is not a integer string
     *
     * FormValidationException with the appropriate BaseError(s) is expected
     *
     * @throws Exception
     */
    @Test
    public void validateRequestLanIdNonAlphaTest() throws Exception {
        try {
            DeleteMessageValidator.validateRequest("abc");
            fail("Expected FormValidationException");
        } catch(FormValidationException ex){
            assertEquals(1, ex.getValidationErrors().size());
            TestUtils.assertContains(ex.getValidationErrors(),
                    new BaseError(Constants.MSG_ID_FIELD, Constants.MSG_ID_NON_NUMERIC));
        }
    }

    /**
     * Validates the behavior when the passed msgId is too a decimal
     *
     * FormValidationException with the appropriate BaseError(s) is expected
     *
     * @throws Exception
     */
    @Test
    public void validateRequestMsgIdDecimalTest() throws Exception {
        try {
            DeleteMessageValidator.validateRequest("1.2");
            fail("Expected FormValidationException");
        } catch(FormValidationException ex){
            assertEquals(1, ex.getValidationErrors().size());
            TestUtils.assertContains(ex.getValidationErrors(),
                    new BaseError(Constants.MSG_ID_FIELD, Constants.MSG_ID_NON_NUMERIC));
        }
    }
}
