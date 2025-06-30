package ru.naumen.naumenlocalchat.app.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import ru.naumen.naumenlocalchat.domain.User;

import java.io.IOException;
import java.util.Set;

/**
 * Сериализатор участников чата
 */
public class MembersSerializer extends StdSerializer<Set<User>> {

    public MembersSerializer() {
        this(null);
    }

    public MembersSerializer(Class<Set<User>> t) {
        super(t);
    }

    @Override
    public void serialize(Set<User> members, JsonGenerator jsonGenerator, SerializerProvider provider) throws IOException {
        jsonGenerator.writeStartArray();
        for (User member : members) {
            jsonGenerator.writeNumber(member.getId());
        }
        jsonGenerator.writeEndArray();
    }
}
