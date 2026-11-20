package com.jcondotta.banking.infrastructure.adapters.config.jackson;

import com.jcondotta.domain.identity.EntityId;
import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.ser.std.StdSerializer;

public class EntityIdSerializer extends StdSerializer<EntityId<?>> {

  public EntityIdSerializer() {
    super(EntityId.class);
  }

  @Override
  public void serialize(EntityId<?> value, JsonGenerator jsonGenerator, SerializationContext serializationContext) throws JacksonException {
    serializationContext.writeValue(jsonGenerator, value.value());
  }
}
