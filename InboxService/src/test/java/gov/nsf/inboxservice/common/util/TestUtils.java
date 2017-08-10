package gov.nsf.inboxservice.common.util;

import gov.nsf.common.model.BaseError;
import gov.nsf.inboxservice.api.model.Message;
import gov.nsf.inboxservice.api.model.MessagePriority;
import gov.nsf.inboxservice.api.model.MessageType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static junit.framework.TestCase.fail;

/**
 * Created by jacklinden on 2/14/17.
 */
public class TestUtils {

    public static List<Message> getMockMessages(String ... lanIds){
        List<Message> messages = new ArrayList<Message>();
        for( String lanId : lanIds ){
            messages.add(getMockMessageForUser(lanId));
        }

        return messages;
    }

    public static List<Message> getMockMessagesForUser(String lanId, int count){

        List<Message> messages = new ArrayList<Message>();
        for( int i = 0; i < count; i++ ){
            Message message = getMockMessageForUser(lanId);
            message.setId(i+"");
            messages.add(message);
        }

        return messages;
    }

    public static Message getMockMessageForUser(String lanId) {
        Message message = new Message();
        message.setLanId(lanId);
        message.setId("0");
        message.setInternal(false);
        message.setType(MessageType.Information);
        message.setExpirationDate("2090-01-01 23:30:32.0");
        message.setActionLink("http://LOL.com");
        message.setActionLink("LOL WebSite");
        message.setCreationDate("2050-01-01 23:30:32.0");
        message.setSummary("Message for " + lanId);
        message.setPriority(MessagePriority.High);
        message.setLastUpdtUser("MeetingSvc");

        return message;
    }

    public static String generateStringWithLength(int length){
        char[] arr = new char[length];
        Arrays.fill(arr, 'D');
        return new String(arr);
    }

    public static void assertContains(List<BaseError> observedErrors, BaseError... expectedErrors){
        ArrayList<BaseError> _observedErrors = new ArrayList<BaseError>(observedErrors);
        for( BaseError expectedError : expectedErrors ){
            int indexFound = -1;
            for( int i = 0; i < _observedErrors.size(); i++ ){
                if( expectedError.equals(_observedErrors.get(i)) ){
                    indexFound = i;
                    break;
                }

            }
            if( indexFound < 0 ){
                fail("Expected " + expectedError + " to be in the errors list but was not");
            } else {
                _observedErrors.remove(indexFound);
            }
        }
    }
}
