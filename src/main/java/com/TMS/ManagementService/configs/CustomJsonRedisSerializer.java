package com.TMS.ManagementService.configs;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;

class CustomJsonRedisSerializer implements RedisSerializer<Object> {

    private final ObjectMapper objectMapper;

    public CustomJsonRedisSerializer(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public byte[] serialize(Object value) {
        if (value == null) {
            return new byte[0];
        }
        try {
            return objectMapper.writeValueAsBytes(value);
        } catch (Exception e) {
            throw new RuntimeException("Failed to serialize object", e);
        }
    }

    @Override
    public Object deserialize(byte[] bytes) {
        if (bytes == null || bytes.length == 0) {
            return null;
        }
        try {
            // CRITICAL FIX: Use JavaType to properly handle type information
            // This allows Jackson to use the @class field in the JSON
            return objectMapper.readValue(bytes, objectMapper.getTypeFactory().constructType(Object.class));
        } catch (Exception e) {
            // Enhanced error logging
            String jsonString = new String(bytes);
            System.err.println("Failed to deserialize JSON: " + jsonString);
            throw new SerializationException("Failed to deserialize object: " + e.getMessage(), e);
        }
    }
}
