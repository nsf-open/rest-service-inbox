package gov.nsf.inboxservice.api.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonValue;

import javax.annotation.Generated;
import java.util.HashMap;
import java.util.Map;

/**
 * MessagePriority enum
 */
@JsonInclude(JsonInclude.Include.ALWAYS)
@JsonIgnoreProperties(ignoreUnknown = true)
@Generated("org.jsonschema2pojo")
public enum MessagePriority {
    High("1"),
    Low("0"),
    Invalid("-1");

    private final String code;
    private static final Map<String,MessagePriority> valueToPriority = new HashMap<String,MessagePriority>();

    static{
        for(MessagePriority priority : MessagePriority.values()){
            valueToPriority.put(priority.getCode(), priority);
        }
    }

    MessagePriority(String code)  {

        this.code = code;

    }

    @JsonCreator
    public static MessagePriority forValue(String value){

        MessagePriority priority = null;
        try{
            priority = MessagePriority.valueOf(value);
        } catch( Exception e ){
            //LOGGER.info(e);
            return MessagePriority.Invalid;
        }

        return priority;
    }

    public String getCode() {
        return this.code;
    }

    @JsonValue
    public static MessagePriority getPriorityFromCode(String code){
        if(!valueToPriority.containsKey(code)){
            throw new IllegalArgumentException("The code " + code + " does not map to a valid MessagePriority");
        }

        return valueToPriority.get(code);
    }
}
