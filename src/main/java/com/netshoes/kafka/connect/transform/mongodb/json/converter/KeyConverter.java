package com.netshoes.kafka.connect.transform.mongodb.json.converter;

import com.netshoes.kafka.connect.transform.mongodb.json.transform.JsonTransformation;
import org.apache.kafka.connect.connector.ConnectRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class KeyConverter {

    private static final Logger log = LoggerFactory.getLogger(JsonTransformation.class);

    private final ValueConverter valueConverter;

    public KeyConverter(final ValueConverter valueConverter) {
        this.valueConverter = valueConverter;
    }

    public Optional<Object> convert(final ConnectRecord record, final Map<String, ?> configs) {
        try {
            final Map<String, Object> payload = valueConverter.convert(record);
            final List<String> fields = findFields(configs);
            final Optional<Object> key = findKeyAndTransform(payload, new LinkedList<>(fields));
            key.ifPresent(o -> log.info(String.format("key located %s", o)));
            return key;
        } catch (final Exception e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    private Optional<Object> findKeyAndTransform(final Map<String, Object> payload, final LinkedList<String> fields) {
        final Optional<Object> key = findKey(payload, new LinkedList<>(fields));
        if (key.isPresent()) {
            return Optional.of(key.get().hashCode());
        }
        return Optional.empty();
    }

    private List<String> findFields(Map<String, ?> configs) {
        final Optional<String> fields = Optional.ofNullable((String) configs.getOrDefault("field.key", null));
        if (fields.isPresent()) {
            log.info(String.format("field.key=%s", fields.get()));
            return Arrays.asList(fields.get().split("\\."));
        }
        log.warn("field.key is not present");
        return new ArrayList<>();
    }

    private Optional<Object> findKey(Map<String, Object> payload, Queue<String> fields) {
        final Optional<Object> value = findValueByKey(payload, fields.poll());
        if (value.isPresent() && fields.size() >= 1) {
            if (value.get() instanceof Map) {
                return findKey((Map<String, Object>) value.get(), fields);
            } else {
                return Optional.ofNullable(value.get());
            }
        }
        return value;
    }

    private Optional<Object> findValueByKey(Map<String, Object> payload, String field) {
        final Optional<Object> valueOptional = Optional.ofNullable(payload.get(field));
        if (valueOptional.isPresent()) {
            return Optional.of(valueOptional.get());
        }

        for (Map.Entry entry : payload.entrySet()) {
            if (entry.getValue() instanceof Map) {
                return findValueByKey((Map<String, Object>) entry.getValue(), field);
            }
        }
        return Optional.empty();
    }
}
