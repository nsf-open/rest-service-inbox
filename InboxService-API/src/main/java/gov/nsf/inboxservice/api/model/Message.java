package gov.nsf.inboxservice.api.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import javax.annotation.Generated;

/**
 * Message POJO
 */
@JsonInclude(JsonInclude.Include.ALWAYS)
@JsonIgnoreProperties(ignoreUnknown = true)
@Generated("org.jsonschema2pojo")
public class Message {

    private String id; //AUTO GENERATED ID
    private String lanId; //REQUIRED non-null, non-empty, no invalid UTF8
    private String summary; //REQUIRED non-null, non-emtpy, no invalid UTF8
    private String creationDate; //AUTO GENERATED 'getdate()'
    private MessagePriority priority; //REQUIRED 'High' or 'Low'
    private MessageType type; //REQUIRED 'Information' or 'Task'
    private String actionLink; //REQUIRED If 'Task' type
    private String actionLabel;//REQUIRED If 'Task' type
    private boolean internal; //REQUIRED If 'Task' type
    private String expirationDate; //REQUIRED If 'Information' and must be valid date time string (ISO)
    private String lastUpdtUser; //REQUIRED


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLanId() {
        return lanId;
    }

    public void setLanId(String lanId) {
        this.lanId = lanId;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }

    public MessagePriority getPriority() {
        return priority;
    }

    public void setPriority(MessagePriority priority) {
        this.priority = priority;
    }

    public MessageType getType() {
        return type;
    }

    public void setType(MessageType type) {
        this.type = type;
    }

    public String getActionLink() {
        return actionLink;
    }

    public void setActionLink(String actionLink) {
        this.actionLink = actionLink;
    }

    public String getActionLabel() {
        return actionLabel;
    }

    public void setActionLabel(String actionLabel) {
        this.actionLabel = actionLabel;
    }

    public boolean isInternal() {
        return internal;
    }

    public void setInternal(boolean internal) {
        this.internal = internal;
    }

    public String getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(String expirationDate) {
        this.expirationDate = expirationDate;
    }

    public String getLastUpdtUser() {
        return lastUpdtUser;
    }

    public void setLastUpdtUser(String lastUpdtUser) {
        this.lastUpdtUser = lastUpdtUser;
    }

}
