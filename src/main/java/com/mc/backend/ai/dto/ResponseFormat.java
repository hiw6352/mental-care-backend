package com.mc.backend.ai.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ResponseFormat(String type, String name, Boolean strict,
                             JsonSchema.SchemaRoot schema) {

    public static ResponseFormat jsonSchema(String name, JsonSchema.SchemaRoot schema,
        boolean strict) {
        return new ResponseFormat("json_schema", name, strict, schema);
    }


}
