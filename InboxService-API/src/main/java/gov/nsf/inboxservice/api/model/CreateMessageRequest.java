package gov.nsf.inboxservice.api.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import javax.annotation.Generated;
import java.util.List;

/**
 * Wrapper for CreateMessage requests
 */
@JsonInclude(JsonInclude.Include.ALWAYS)
@JsonIgnoreProperties(ignoreUnknown = true)
@Generated("org.jsonschema2pojo")
public class CreateMessageRequest {
    private Message message;
    private List<String> lanIds;

    public CreateMessageRequest(){

    }

    public CreateMessageRequest(Message message, List<String> lanIds){
        this.message = message;
        this.lanIds = lanIds;
    }

    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }

    public List<String> getLanIds() {
        return lanIds;
    }

    public void setLanIds(List<String> lanIds) {
        this.lanIds = lanIds;
    }
}
