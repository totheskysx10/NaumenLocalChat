package ru.naumen.naumenlocalchat.app.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import ru.naumen.naumenlocalchat.domain.Chat;

import java.io.IOException;
import java.util.List;

/**
 * Сериализатор Id чатов
 */
public class ChatIdSerializer extends StdSerializer<List<Chat>> {

    public ChatIdSerializer() {
        this(null);
    }

    public ChatIdSerializer(Class<List<Chat>> t) {
        super(t);
    }

    @Override
    public void serialize(List<Chat> chats, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeStartArray();
        for (Chat chat : chats) {
            jsonGenerator.writeNumber(chat.getId());
        }
        jsonGenerator.writeEndArray();
    }
}
