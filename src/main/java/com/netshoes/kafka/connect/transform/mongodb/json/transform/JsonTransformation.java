package com.netshoes.kafka.connect.transform.mongodb.json.transform;

import com.netshoes.kafka.connect.transform.mongodb.json.config.JsonTransformationConfig;
import com.netshoes.kafka.connect.transform.mongodb.json.converter.ValueConverter;
import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.connect.connector.ConnectRecord;
import org.apache.kafka.connect.transforms.Transformation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class JsonTransformation<R extends ConnectRecord<R>> implements Transformation<R> {

    private static final Logger log = LoggerFactory.getLogger(JsonTransformation.class);

    private JsonTransformationConfig jsonTransformationConfig;

    private final ValueConverter valueConverter;

    public JsonTransformation() {
        this.valueConverter = new ValueConverter();
    }

    public ConnectRecord apply(final ConnectRecord record) {
        try {
            log.debug("original message: " + record.value().toString());
            return record.newRecord(
                    record.topic(),
                    record.kafkaPartition(),
                    record.keySchema(),
                    record.key(),
                    null,
                    valueConverter.toJson(record),
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
        this.jsonTransformationConfig = new JsonTransformationConfig(configs);
    }
}
