package com.netshoes.kafka.connect.transform.mongodb.json.converter;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.netshoes.kafka.connect.transform.mongodb.json.config.ObjectMapperConfig;
import com.netshoes.kafka.connect.transform.mongodb.json.transform.JsonTransformation;
import org.apache.kafka.connect.connector.ConnectRecord;
import org.apache.kafka.connect.data.Struct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class ValueConverter {

    private final ObjectMapper objectMapper = new ObjectMapperConfig().mapper();

    private static final Logger log = LoggerFactory.getLogger(JsonTransformation.class);

    public Map<String, Object> toJson(final ConnectRecord record) {
        try {
            final Struct struct = (Struct) record.value();
            final Map<String, Object> mapJson = objectMapper.readValue(extractJsonPayload(struct), new TypeReference<Map<String, Object>>() {});
            addSource(struct, mapJson);
            addOperation(struct, mapJson);
            return mapJson;

        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    private void addOperation(final Struct struct, final Map<String, Object> mapJson) {
        mapJson.put("op", struct.getString("op"));
    }

    private void addSource(final Struct struct, final Map<String, Object> mapJson) {
        findStruct(struct, "source")
                .ifPresent(source -> {
                    final Map<String, Object> values = new HashMap<>();
                    values.put("name", source.getString("name"));
                    values.put("rs", source.getString("rs"));
                    values.put("ns", source.getString("ns"));
                    values.put("sec", source.getInt32("sec"));
                    values.put("ord", source.getInt32("ord"));
                    values.put("h", source.getInt64("h"));
                    values.put("initsync", source.getBoolean("initsync"));

                    mapJson.put("source", values);
                });
    }

    private Optional<Struct> findStruct(final Struct struct, final String name) {
        try {
            return Optional.ofNullable(struct.getStruct(name));
        } catch (final Exception ex) {
            log.error(ex.getMessage(), ex);
            return Optional.empty();
        }
    }


    private String extractJsonPayload(final Struct struct) {
        return Optional.ofNullable(struct.getString("patch")).orElse(struct.getString("after"));
    }
}
