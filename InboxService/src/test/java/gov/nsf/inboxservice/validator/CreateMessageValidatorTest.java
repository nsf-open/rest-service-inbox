package gov.nsf.inboxservice.validator;

import gov.nsf.common.exception.FormValidationException;
import gov.nsf.common.model.BaseError;
import gov.nsf.inboxservice.api.model.CreateMessageRequest;
import gov.nsf.inboxservice.api.model.Message;
import gov.nsf.inboxservice.api.model.MessagePriority;
import gov.nsf.inboxservice.api.model.MessageType;
import gov.nsf.inboxservice.common.util.Constants;
import gov.nsf.inboxservice.common.util.TestUtils;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static junit.framework.TestCase.fail;
import static org.junit.Assert.assertEquals;

/**
 * JUnit tests for CreateMessageValidator
 */
public class CreateMessageValidatorTest {

    /**
     * Returns a Message object with all valid fields
     *
     * @return Message
     */
    private Message getValidMessage() {
        Message message = new Message();
        message.setLanId("test");
        message.setSummary("This is my summary");
        message.setPriority(MessagePriority.High);
        message.setType(MessageType.Information);
        message.setActionLink("http://LOL.com");
        message.setActionLabel("LOL");
        message.setExpirationDate("2090-06-06 11:00:00.0");
        message.setInternal(false);
        message.setLastUpdtUser("MeetingSvc");

        return message;
    }

    /**
     * Returns a Message object with all valid fields
     *
     * @return Message
     */
    private List<String> getValidLanIds() {
        List<String> lanIds = new ArrayList<String>();
        lanIds.add("test");
        lanIds.add("test2");
        lanIds.add("test");
        lanIds.add("test");

        return lanIds;
    }

    /**
     * Validates the happy path behavior when all message fields are valid
     * <p>
     * No exceptions should be thrown
     *
     * @throws Exception
     */
    @Test
    public void validateRequestHappyPathTest() throws Exception {
        List<String> lanIds = getValidLanIds();
        Message message = getValidMessage();
        CreateMessageValidator.validateRequest(new CreateMessageRequest(message, lanIds));
    }

    /**
     * Validates the behavior when the request fields are null
     * <p>
     * FormValidationException with the appropriate BaseError(s)
     *
     * @throws Exception
     */
    @Test
    public void validateRequestNullFieldsTest() throws Exception {

        try {
            CreateMessageValidator.validateRequest(new CreateMessageRequest(null,null));
            fail("Expected FormValidationException");
        } catch (FormValidationException ex) {
            assertEquals(2, ex.getValidationErrors().size());
            TestUtils.assertContains(ex.getValidationErrors(),
                    new BaseError(Constants.REQUEST_MESSAGE_FIELD, Constants.MISSING_NULL_EMPTY_FIELD + Constants.REQUEST_MESSAGE_FIELD),
                    new BaseError(Constants.REQUEST_LAN_IDS_FIELD, Constants.MISSING_NULL_EMPTY_FIELD + Constants.REQUEST_LAN_IDS_FIELD));
        }    }

    /**
     * Validates the behavior when MessageType.Invalid is passed
     * <p>
     * FormValidationException with the appropriate BaseError(s) is expected
     *
     * @throws Exception
     */
    @Test
    public void validateMessageInvalidTypeTest() throws Exception {
        List<String> lanIds = getValidLanIds();
        Message message = getValidMessage();
        message.setType(MessageType.Invalid);
        try {
            CreateMessageValidator.validateRequest(new CreateMessageRequest(message,lanIds));
            fail("Expected FormValidationException");
        } catch (FormValidationException ex) {
            assertEquals(1, ex.getValidationErrors().size());
            TestUtils.assertContains(ex.getValidationErrors(),
                    new BaseError(Constants.MSG_TYPE_FIELD, Constants.INVALID_MESSAGE_TYPE));
        }
    }

    /**
     * Validates the behavior when a null MessageType is passed
     * <p>
     * FormValidationException with the appropriate BaseError(s) is expected
     *
     * @throws Exception
     */
    @Test
    public void validateMessageNullTypeTest() throws Exception {
        Message message = getValidMessage();
        List<String> lanIds = getValidLanIds();
        message.setType(null);
        try {
            CreateMessageValidator.validateRequest(new CreateMessageRequest(message, lanIds));
            fail("Expected FormValidationException");
        } catch (FormValidationException ex) {
            assertEquals(1, ex.getValidationErrors().size());
            TestUtils.assertContains(ex.getValidationErrors(),
                    new BaseError(Constants.MSG_TYPE_FIELD, Constants.MISSING_NULL_FIELD + Constants.MSG_TYPE_FIELD));
        }
    }

    /**
     * Validates the behavior when MessagePriority.Invalid is passed
     * <p>
     * FormValidationException with the appropriate BaseError(s) is expected
     *
     * @throws Exception
     */
    @Test
    public void validateMessageInvalidPriorityTest() throws Exception {
        List<String> lanIds = getValidLanIds();
        Message message = getValidMessage();
        message.setPriority(MessagePriority.Invalid);
        try {
            CreateMessageValidator.validateRequest(new CreateMessageRequest(message, lanIds));
            fail("Expected FormValidationException");
        } catch (FormValidationException ex) {
            assertEquals(1, ex.getValidationErrors().size());
            TestUtils.assertContains(ex.getValidationErrors(),
                    new BaseError(Constants.MSG_PRIORITY_FIELD, Constants.INVALID_MESSAGE_PRIORITY));
        }
    }

    /**
     * Validates the behavior when the MessageType is Infomation and the rest of the fields are null
     * <p>
     * FormValidationException with the appropriate BaseError(s) is expected
     *
     * @throws Exception
     */
    @Test
    public void validateMessageInformationTypeNullFieldsTest() throws Exception {
        List<String> lanIds = getValidLanIds();
        Message message = getValidMessage();
        message.setType(MessageType.Information);
        message.setSummary(null);
        message.setPriority(null);
        message.setActionLink(null);
        message.setActionLabel(null);
        message.setExpirationDate(null);
        message.setInternal(false);
        message.setLastUpdtUser(null);
        try {
            CreateMessageValidator.validateRequest(new CreateMessageRequest(message, lanIds));
            fail("Expected FormValidationException");
        } catch (FormValidationException ex) {
            assertEquals(4, ex.getValidationErrors().size());
            TestUtils.assertContains(ex.getValidationErrors(),
                    new BaseError(Constants.MSG_SUMMARY_FIELD, Constants.MISSING_NULL_EMPTY_FIELD + Constants.MSG_SUMMARY_FIELD),
                    new BaseError(Constants.MSG_PRIORITY_FIELD, Constants.MISSING_NULL_FIELD + Constants.MSG_PRIORITY_FIELD),
                    new BaseError(Constants.MSG_LAST_UPDT_USER_FIELD, Constants.MISSING_NULL_EMPTY_FIELD + Constants.MSG_LAST_UPDT_USER_FIELD),
                    new BaseError(Constants.MSG_EXP_DATE_FIELD, Constants.MISSING_NULL_EMPTY_FIELD + Constants.MSG_EXP_DATE_FIELD));
        }
    }

    /**
     * Validates the behavior when the MessageType is Task and the rest of the fields are null
     * <p>
     * FormValidationException with the appropriate BaseError(s) is expected
     *
     * @throws Exception
     */
    @Test
    public void validateMessageTaskTypeNullFieldsTest() throws Exception {
        List<String> lanIds = getValidLanIds();
        Message message = getValidMessage();
        message.setType(MessageType.Task);
        message.setSummary(null);
        message.setPriority(null);
        message.setActionLink(null);
        message.setActionLabel(null);
        message.setExpirationDate(null);
        message.setInternal(false);
        message.setLastUpdtUser(null);
        try {
            CreateMessageValidator.validateRequest(new CreateMessageRequest(message, lanIds));
            fail("Expected FormValidationException");
        } catch (FormValidationException ex) {
            assertEquals(5, ex.getValidationErrors().size());
            TestUtils.assertContains(ex.getValidationErrors(),
                    new BaseError(Constants.MSG_SUMMARY_FIELD, Constants.MISSING_NULL_EMPTY_FIELD + Constants.MSG_SUMMARY_FIELD),
                    new BaseError(Constants.MSG_PRIORITY_FIELD, Constants.MISSING_NULL_FIELD + Constants.MSG_PRIORITY_FIELD),
                    new BaseError(Constants.MSG_ACTION_LINK_FIELD, Constants.MISSING_NULL_EMPTY_FIELD + Constants.MSG_ACTION_LINK_FIELD),
                    new BaseError(Constants.MSG_LAST_UPDT_USER_FIELD, Constants.MISSING_NULL_EMPTY_FIELD + Constants.MSG_LAST_UPDT_USER_FIELD),
                    new BaseError(Constants.MSG_ACTION_LABEL_FIELD, Constants.MISSING_NULL_EMPTY_FIELD + Constants.MSG_ACTION_LABEL_FIELD));
        }
    }

    /**
     * Validates the behavior when the MessageType is Infomation and the String fields are empty
     * <p>
     * FormValidationException with the appropriate BaseError(s) is expected
     *
     * @throws Exception
     */
    @Test
    public void validateMessageInformationTypeEmptyStringsTest() throws Exception {
        List<String> lanIds = getValidLanIds();
        Message message = getValidMessage();
        message.setType(MessageType.Information);
        message.setSummary("");
        message.setActionLink("");
        message.setExpirationDate("");
        message.setLastUpdtUser("");
        try {
            CreateMessageValidator.validateRequest(new CreateMessageRequest(message, lanIds));
            fail("Expected FormValidationException");
        } catch (FormValidationException ex) {
            assertEquals(3, ex.getValidationErrors().size());
            TestUtils.assertContains(ex.getValidationErrors(),
                    new BaseError(Constants.MSG_SUMMARY_FIELD, Constants.MISSING_NULL_EMPTY_FIELD + Constants.MSG_SUMMARY_FIELD),
                    new BaseError(Constants.MSG_LAST_UPDT_USER_FIELD, Constants.MISSING_NULL_EMPTY_FIELD + Constants.MSG_LAST_UPDT_USER_FIELD),
                    new BaseError(Constants.MSG_EXP_DATE_FIELD, Constants.MISSING_NULL_EMPTY_FIELD + Constants.MSG_EXP_DATE_FIELD));
        }
    }

    /**
     * Validates the behavior when the MessageType is Task and the String fields are empty
     * <p>
     * FormValidationException with the appropriate BaseError(s) is expected
     *
     * @throws Exception
     */
    @Test
    public void validateMessageTaskTypeEmptyStringsTest() throws Exception {
        List<String> lanIds = getValidLanIds();
        Message message = getValidMessage();
        message.setType(MessageType.Task);
        message.setLanId("");
        message.setSummary("");
        message.setActionLink("");
        message.setActionLabel("");
        message.setExpirationDate("");
        message.setLastUpdtUser("");
        try {
            CreateMessageValidator.validateRequest(new CreateMessageRequest(message, lanIds));
            fail("Expected FormValidationException");
        } catch (FormValidationException ex) {
            assertEquals(4, ex.getValidationErrors().size());
            TestUtils.assertContains(ex.getValidationErrors(),
                    new BaseError(Constants.MSG_SUMMARY_FIELD, Constants.MISSING_NULL_EMPTY_FIELD + Constants.MSG_SUMMARY_FIELD),
                    new BaseError(Constants.MSG_ACTION_LINK_FIELD, Constants.MISSING_NULL_EMPTY_FIELD + Constants.MSG_ACTION_LINK_FIELD),
                    new BaseError(Constants.MSG_LAST_UPDT_USER_FIELD, Constants.MISSING_NULL_EMPTY_FIELD + Constants.MSG_LAST_UPDT_USER_FIELD),
                    new BaseError(Constants.MSG_ACTION_LABEL_FIELD, Constants.MISSING_NULL_EMPTY_FIELD + Constants.MSG_ACTION_LABEL_FIELD));
        }
    }

    /**
     * Validates the behavior when the MessageType is Infomation and the String fields contain invalid characters
     * <p>
     * FormValidationException with the appropriate BaseError(s) is expected
     *
     * @throws Exception
     */
    @Test
    public void validateMessageInformationTypeNonUTF8StringsTest() throws Exception {
        List<String> lanIds = getValidLanIds();
        Message message = getValidMessage();
        message.setType(MessageType.Information);
        message.setSummary("©");
        message.setActionLink("©");
        message.setExpirationDate("©");
        message.setLastUpdtUser("©");
        try {
            CreateMessageValidator.validateRequest(new CreateMessageRequest(message, lanIds));
            fail("Expected FormValidationException");
        } catch (FormValidationException ex) {
            assertEquals(3, ex.getValidationErrors().size());
            TestUtils.assertContains(ex.getValidationErrors(),
                    new BaseError(Constants.MSG_SUMMARY_FIELD, Constants.UNSUPPORTED_CHARACTERS_FIELD + Constants.MSG_SUMMARY_FIELD),
                    new BaseError(Constants.MSG_LAST_UPDT_USER_FIELD, Constants.UNSUPPORTED_CHARACTERS_FIELD + Constants.MSG_LAST_UPDT_USER_FIELD),
                    new BaseError(Constants.MSG_EXP_DATE_FIELD, Constants.UNSUPPORTED_CHARACTERS_FIELD + Constants.MSG_EXP_DATE_FIELD));
        }
    }

    /**
     * Validates the behavior when the MessageType is Task and the String fields contain invalid characters
     * <p>
     * FormValidationException with the appropriate BaseError(s) is expected
     *
     * @throws Exception
     */
    @Test
    public void validateMessageTaskTypeNonUTF8StringsTest() throws Exception {
        List<String> lanIds = getValidLanIds();
        Message message = getValidMessage();
        message.setType(MessageType.Task);
        message.setSummary("©");
        message.setActionLink("©");
        message.setActionLabel("©");
        message.setExpirationDate("©");
        message.setLastUpdtUser("©");

        try {
            CreateMessageValidator.validateRequest(new CreateMessageRequest(message, lanIds));
            fail("Expected FormValidationException");
        } catch (FormValidationException ex) {
            assertEquals(4, ex.getValidationErrors().size());
            TestUtils.assertContains(ex.getValidationErrors(),
                    new BaseError(Constants.MSG_SUMMARY_FIELD, Constants.UNSUPPORTED_CHARACTERS_FIELD + Constants.MSG_SUMMARY_FIELD),
                    new BaseError(Constants.MSG_ACTION_LINK_FIELD, Constants.UNSUPPORTED_CHARACTERS_FIELD + Constants.MSG_ACTION_LINK_FIELD),
                    new BaseError(Constants.MSG_LAST_UPDT_USER_FIELD, Constants.UNSUPPORTED_CHARACTERS_FIELD + Constants.MSG_LAST_UPDT_USER_FIELD),
                    new BaseError(Constants.MSG_ACTION_LABEL_FIELD, Constants.UNSUPPORTED_CHARACTERS_FIELD + Constants.MSG_ACTION_LABEL_FIELD));
        }
    }

    /**
     * Validates the behavior when the MessageType is Infomation and the expirationDate field is an invalid date string
     * <p>
     * FormValidationException with the appropriate BaseError(s) is expected
     *
     * @throws Exception
     */
    @Test
    public void validateMessageInformationTypeInvalidDateTimeStringTest() throws Exception {
        List<String> lanIds = getValidLanIds();
        Message message = getValidMessage();
        message.setExpirationDate("FISH-06-06 11:00:00.0");
        try {
            CreateMessageValidator.validateRequest(new CreateMessageRequest(message, lanIds));
            fail("Expected FormValidationException");
        } catch (FormValidationException ex) {
            assertEquals(1, ex.getValidationErrors().size());
            TestUtils.assertContains(ex.getValidationErrors(),
                    new BaseError(Constants.MSG_EXP_DATE_FIELD, Constants.INVALID_DATE_TIME + Constants.MSG_EXP_DATE_FIELD));
        }
    }

    /**
     * Validates the behavior when the MessageType is Infomation and the expirationDate field is before the current date
     * <p>
     * FormValidationException with the appropriate BaseError(s) is expected
     *
     * @throws Exception
     */
    @Test
    public void validateMessageInformationTypeExpDateBeforeCurrDateTest() throws Exception {
        List<String> lanIds = getValidLanIds();
        Message message = getValidMessage();
        message.setExpirationDate("2014-06-06 11:00:00.0");
        try {
            CreateMessageValidator.validateRequest(new CreateMessageRequest(message, lanIds));
            fail("Expected FormValidationException");
        } catch (FormValidationException ex) {
            assertEquals(1, ex.getValidationErrors().size());
            TestUtils.assertContains(ex.getValidationErrors(),
                    new BaseError(Constants.MSG_EXP_DATE_FIELD, Constants.EXP_DATE_LESS_THAN_CURRENT_DATE));
        }
    }

    /**
     * Validates the behavior when the summary length == the max summary character limit
     *
     * Test should throw no exception
     * @throws Exception
     */
    @Test
    public void validateMessageSummaryLengthEqualsMaxTest() throws Exception {
        List<String> lanIds = getValidLanIds();
        Message message = getValidMessage();
        String summary = TestUtils.generateStringWithLength(Constants.MESSAGE_PLAIN_TEXT_MAX_LENGTH);
        summary = summary.replaceFirst(summary.charAt(0)+"", "<a href=lol.com>F</a>");
        message.setSummary(summary);
        CreateMessageValidator.validateRequest(new CreateMessageRequest(message, lanIds));
    }

    /**
     * Validates the behavior when the functional length of the summary is > the max summary character limit
     *
     * FormValidation exception with the appropriate BaseError(s) is expected
     * @throws Exception
     */
    @Test
    public void validateMessageSummaryLengthTooLongTest() throws Exception {
        List<String> lanIds = getValidLanIds();
        Message message = getValidMessage();
        String summary = TestUtils.generateStringWithLength(Constants.MESSAGE_PLAIN_TEXT_MAX_LENGTH);
        summary = summary.replaceFirst(summary.charAt(0)+"", "<a href=lol.com>FF</a>");
        message.setSummary(summary);

        try {
            CreateMessageValidator.validateRequest(new CreateMessageRequest(message, lanIds));
            fail("Expected FormValidationException");
        } catch (FormValidationException ex) {
            assertEquals(1, ex.getValidationErrors().size());
            TestUtils.assertContains(ex.getValidationErrors(),
                    new BaseError(Constants.MSG_SUMMARY_FIELD, Constants.FIELD_LENGTH_TOO_LONG + Constants.MESSAGE_PLAIN_TEXT_MAX_LENGTH));
        }

    }

    /**
     * Validates behavior when actionLabel is too l ong
     * @throws Exception
     */
    @Test
    public void validateActionLabelMaxLengthTooLongTest() throws Exception {
        List<String> lanIds = getValidLanIds();
        Message message = getValidMessage();
        message.setActionLabel(TestUtils.generateStringWithLength(Constants.MSG_ACTION_LABEL_MAX_LEGNTH+1));
        message.setType(MessageType.Task);
        try {
            CreateMessageValidator.validateRequest(new CreateMessageRequest(message, lanIds));
            fail("Expected FormValidationException");
        } catch (FormValidationException ex) {
            assertEquals(1, ex.getValidationErrors().size());
            TestUtils.assertContains(ex.getValidationErrors(),
                    new BaseError(Constants.MSG_ACTION_LABEL_FIELD, Constants.FIELD_LENGTH_TOO_LONG + Constants.MSG_ACTION_LABEL_MAX_LEGNTH));
        }
    }

    /**
     * Validates the behavior when an empty list is passed
     *
     * FormValidation exception with the appropriate BaseError(s) is expected
     * @throws Exception
     */
    @Test
    public void validateLanIdsEmptyListTest() throws Exception {
        Message message = getValidMessage();
        List<String> lanIds = Collections.emptyList();

        try {
            CreateMessageValidator.validateRequest(new CreateMessageRequest(message, lanIds));
            fail("Expected FormValidationException");
        } catch (FormValidationException ex) {
            assertEquals(1, ex.getValidationErrors().size());
            TestUtils.assertContains(ex.getValidationErrors(),
                    new BaseError(Constants.REQUEST_LAN_IDS_FIELD, Constants.MISSING_NULL_EMPTY_FIELD + Constants.REQUEST_LAN_IDS_FIELD));
        }

    }

    /**
     * Validates the behavior when a null is passed in the list
     *
     * FormValidation exception with the appropriate BaseError(s) is expected
     * @throws Exception
     */
    @Test
    public void validateLanIdsNullLanIdTest() throws Exception {
        Message message = getValidMessage();
        List<String> lanIds = getValidLanIds();
        lanIds.add(null);

        try {
            CreateMessageValidator.validateRequest(new CreateMessageRequest(message, lanIds));
            fail("Expected FormValidationException");
        } catch (FormValidationException ex) {
            assertEquals(1, ex.getValidationErrors().size());
            TestUtils.assertContains(ex.getValidationErrors(),
                    new BaseError(Constants.REQUEST_LAN_IDS_FIELD, Constants.LAN_IDS_CONTAINS_INVALID_LAN_ID));
        }

    }

    /**
     * Validates the behavior when an empty string is passed in the list
     *
     * FormValidation exception with the appropriate BaseError(s) is expected
     * @throws Exception
     */
    @Test
    public void validateLanIdsEmptyStringLanIdTest() throws Exception {
        Message message = getValidMessage();
        List<String> lanIds = getValidLanIds();
        lanIds.add("");

        try {
            CreateMessageValidator.validateRequest(new CreateMessageRequest(message, lanIds));
            fail("Expected FormValidationException");
        } catch (FormValidationException ex) {
            assertEquals(1, ex.getValidationErrors().size());
            TestUtils.assertContains(ex.getValidationErrors(),
                    new BaseError(Constants.REQUEST_LAN_IDS_FIELD, Constants.LAN_IDS_CONTAINS_INVALID_LAN_ID));
        }

    }

    /**
     * Validates the behavior when both a null and empty string are passed
     *
     * FormValidation exception with the appropriate BaseError(s) is expected
     * @throws Exception
     */
    @Test
    public void validateLanIdsBothNullAndEmptyLanIdTest() throws Exception {
        Message message = getValidMessage();
        List<String> lanIds = getValidLanIds();
        lanIds.add(null);
        lanIds.add("");

        try {
            CreateMessageValidator.validateRequest(new CreateMessageRequest(message, lanIds));
            fail("Expected FormValidationException");
        } catch (FormValidationException ex) {
            assertEquals(1, ex.getValidationErrors().size());
            TestUtils.assertContains(ex.getValidationErrors(),
                    new BaseError(Constants.REQUEST_LAN_IDS_FIELD, Constants.LAN_IDS_CONTAINS_INVALID_LAN_ID));
        }

    }

    /**
     * Validates the behavior when a non-UTF8 string is passed
     *
     * FormValidation exception with the appropriate BaseError(s) is expected
     * @throws Exception
     */
    @Test
    public void validateLanIdsNonUTF8LanIdTest() throws Exception {
        Message message = getValidMessage();
        List<String> lanIds = getValidLanIds();
        lanIds.add("©");

        try {
            CreateMessageValidator.validateRequest(new CreateMessageRequest(message, lanIds));
            fail("Expected FormValidationException");
        } catch (FormValidationException ex) {
            assertEquals(1, ex.getValidationErrors().size());
            TestUtils.assertContains(ex.getValidationErrors(),
                    new BaseError(Constants.REQUEST_LAN_IDS_FIELD, Constants.LAN_IDS_CONTAINS_INVALID_LAN_ID));
        }

    }


    /**
     * Validates the behavior when a string with a number i spassed
     *
     * FormValidation exception with the appropriate BaseError(s) is expected
     * @throws Exception
     */
    @Test
    public void validateLanIdsLanIdTooLongTest() throws Exception {
        Message message = getValidMessage();
        List<String> lanIds = getValidLanIds();
        lanIds.add("AAAAAAAAA");

        try {
            CreateMessageValidator.validateRequest(new CreateMessageRequest(message, lanIds));
            fail("Expected FormValidationException");
        } catch (FormValidationException ex) {
            assertEquals(1, ex.getValidationErrors().size());
            TestUtils.assertContains(ex.getValidationErrors(),
                    new BaseError(Constants.REQUEST_LAN_IDS_FIELD, Constants.LAN_IDS_CONTAINS_INVALID_LAN_ID));
        }

    }

    /**
     * Validates the behavior when null, "", and nonUTF strings are passed
     *
     * FormValidation exception with the appropriate BaseError(s) is expected
     * @throws Exception
     */
    @Test
    public void validateLanIdsCombineNonUTF8NullAndEmptyLanIdTest() throws Exception {
        Message message = getValidMessage();
        List<String> lanIds = getValidLanIds();
        lanIds.add("©");
        lanIds.add(null);
        lanIds.add("");
        lanIds.add("©");
        lanIds.add(null);
        lanIds.add("");

        try {
            CreateMessageValidator.validateRequest(new CreateMessageRequest(message, lanIds));
            fail("Expected FormValidationException");
        } catch (FormValidationException ex) {
            assertEquals(1, ex.getValidationErrors().size());
            TestUtils.assertContains(ex.getValidationErrors(),
                    new BaseError(Constants.REQUEST_LAN_IDS_FIELD, Constants.LAN_IDS_CONTAINS_INVALID_LAN_ID));

        }

    }
}
