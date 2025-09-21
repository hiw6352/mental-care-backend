package com.mc.backend.ai.dto.responses;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.mc.backend.ai.dto.JsonSchema.SchemaRoot;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ResponseFormat(String type, String name, Boolean strict,
                             SchemaRoot schema) {

    public static ResponseFormat jsonSchema(String name, SchemaRoot schema,
        boolean strict) {
        return new ResponseFormat("json_schema", name, strict, schema);
    }


}
