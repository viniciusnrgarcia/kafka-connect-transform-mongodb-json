package com.netshoes.kafka.connect.transform.mongodb.json.transform;

import com.netshoes.kafka.connect.transform.mongodb.json.config.JsonTransformationConfig;
import com.netshoes.kafka.connect.transform.mongodb.json.converter.KeyConverter;
import com.netshoes.kafka.connect.transform.mongodb.json.converter.ValueConverter;
import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.connect.connector.ConnectRecord;
import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.data.SchemaBuilder;
import org.apache.kafka.connect.transforms.Transformation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Optional;

public class JsonTransformation<R extends ConnectRecord<R>> implements Transformation<R> {

    private static final Logger log = LoggerFactory.getLogger(JsonTransformation.class);

    private JsonTransformationConfig jsonTransformationConfig;

    private final ValueConverter valueConverter;

    private final KeyConverter keyConverter;

    private Map<String, ?> configs;

    public JsonTransformation() {
        this.valueConverter = new ValueConverter();
        this.keyConverter = new KeyConverter(valueConverter);
    }

    public ConnectRecord apply(final ConnectRecord record) {
        try {
            log.debug("original message: " + record.value().toString());
            final Optional<Object> key = keyConverter.convert(record, configs);
            Schema schemaKey = record.keySchema();
            if (key.isPresent()) {
                schemaKey = SchemaBuilder.int32();
            }

            return record.newRecord(
                    record.topic(),
                    record.kafkaPartition(),
                    schemaKey,
                    key.orElse(record.key()),
                    null,
                    valueConverter.convert(record),
                    record.timestamp()
            );
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return record;
        }
    }

    public ConfigDef config() {
        return JsonTransformationConfig.config();
    }

    public void close() {
    }

    public void configure(Map<String, ?> configs) {
        this.configs = configs;
        this.jsonTransformationConfig = new JsonTransformationConfig(configs);
    }
}
