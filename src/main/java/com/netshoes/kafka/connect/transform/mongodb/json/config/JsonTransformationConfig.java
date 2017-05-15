package com.netshoes.kafka.connect.transform.mongodb.json.config;

import org.apache.kafka.common.config.AbstractConfig;
import org.apache.kafka.common.config.ConfigDef;

import java.util.Map;

public class JsonTransformationConfig extends AbstractConfig {

    public JsonTransformationConfig(Map<String, ?> parsedConfig) {
        super(config(), parsedConfig, true);
    }

    public static ConfigDef config() {
        return new ConfigDef();
    }
}
