package ru.naumen.naumenlocalchat.app.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import ru.naumen.naumenlocalchat.domain.User;

import java.io.IOException;

/**
 * Сериализатор Id пользователя
 */
public class UserIdSerializer extends StdSerializer<User> {

    public UserIdSerializer() {
        super(User.class);
    }

    @Override
    public void serialize(User user, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeNumber(user.getId());
    }
}
