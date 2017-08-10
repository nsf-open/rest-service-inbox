package gov.nsf.inboxservice.dao.rowmapper;

import gov.nsf.inboxservice.api.model.Message;
import org.springframework.jdbc.core.ResultSetExtractor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * MessageResultSetExtractor class for retrieving List of Messages from DB
 */
public class MessageResultSetExtractor implements ResultSetExtractor<List<Message>>{

    private MessageRowMapper rowMapper;

    public MessageResultSetExtractor(){
        this.rowMapper = new MessageRowMapper();
    }
    /**
     * Returns List of Messages stored in DB
     * @param resultSet
     * @return
     * @throws SQLException
     */
    @Override
    public List<Message> extractData(ResultSet resultSet) throws SQLException {
        List<Message> messages = new ArrayList<Message>();


        while(resultSet.next()) {
            messages.add(rowMapper.mapRow(resultSet,resultSet.getRow()));
        }
        return messages;
    }
}
