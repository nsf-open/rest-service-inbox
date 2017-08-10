package gov.nsf.inboxservice.api.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import gov.nsf.common.model.BaseResponseWrapper;

import javax.annotation.Generated;
import java.util.Collections;
import java.util.List;

/**
 * MessageResponsdWrapper
 */
@JsonInclude(JsonInclude.Include.ALWAYS)
@JsonIgnoreProperties(ignoreUnknown = true)
@Generated("org.jsonschema2pojo")
public class MessageResponseWrapper extends BaseResponseWrapper {

    private List<Message> messages;


    public MessageResponseWrapper(){

    }

    public MessageResponseWrapper(Message message){
        this(Collections.<Message>singletonList(message));
    }

    public MessageResponseWrapper(List<Message> messages){
        this.messages = messages;
    }

    public List<Message> getMessages() {
        return messages;
    }

    public void setMessage(List<Message> messages) {
        this.messages = messages;
    }
}
