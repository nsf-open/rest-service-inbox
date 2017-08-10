package gov.nsf.inboxservice.api.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonValue;

import javax.annotation.Generated;
import java.util.HashMap;
import java.util.Map;

/**
 * MessageType enum
 */
@JsonInclude(JsonInclude.Include.ALWAYS)
@JsonIgnoreProperties(ignoreUnknown = true)
@Generated("org.jsonschema2pojo")
public enum MessageType {

    Information("I"),
    Task("T"),
    Invalid("-1");

    private final String code;
    private static final Map<String,MessageType> valueToType = new HashMap<String,MessageType>();

    static{
        for(MessageType type : MessageType.values()){
            valueToType.put(type.getCode(), type);
        }
    }

    MessageType(String code)  {

        this.code = code;

    }

    @JsonCreator
    public static MessageType forValue(String value){

        MessageType type = null;
        try{
            type = MessageType.valueOf(value);
        } catch( Exception e ){
            //LOGGER.info(e);
            return MessageType.Invalid;
        }

        return type;
    }

    public String getCode() {
        return this.code;
    }

    @JsonValue
    public static MessageType getTypeFromCode(String code){
        if(!valueToType.containsKey(code)){
            throw new IllegalArgumentException("The code " + code + " does not map to a valid MessageType");
        }

        return valueToType.get(code);
    }
}
