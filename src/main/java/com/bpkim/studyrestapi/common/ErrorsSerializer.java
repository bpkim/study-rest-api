package com.bpkim.studyrestapi.common;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.springframework.boot.jackson.JsonComponent;
import org.springframework.validation.Errors;

import java.io.IOException;

// errors 를 시리얼라이제이션 할때 알아서 사용한다.
@JsonComponent
public class ErrorsSerializer extends JsonSerializer<Errors> {
    @Override
    public void serialize(Errors errors, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeFieldName("errors");
        jsonGenerator.writeStartArray();

        // 필드 에러 메시지
        errors.getFieldErrors().stream().forEach(e ->{
            try{
                jsonGenerator.writeStartObject();;
                jsonGenerator.writeStringField("field", e.getField());
                jsonGenerator.writeStringField("objectName", e.getObjectName());
                jsonGenerator.writeStringField("code", e.getCode());
                jsonGenerator.writeStringField("defaultMessage", e.getDefaultMessage());
                Object rejectedValue = e.getRejectedValue();
                if(rejectedValue != null){
                    jsonGenerator.writeStringField("rejectedValue", e.getDefaultMessage());
                }

                jsonGenerator.writeEndObject();
            }catch (IOException e1){
                e1.printStackTrace();
            }
        });

        // 글로벌 에러 메시지
        errors.getGlobalErrors().forEach(e->{
            try{{
                jsonGenerator.writeStartObject();;
                jsonGenerator.writeStringField("objectName", e.getObjectName());
                jsonGenerator.writeStringField("code", e.getCode());
                jsonGenerator.writeStringField("defaultMessage", e.getDefaultMessage());
                jsonGenerator.writeEndObject();
            }

            }catch (IOException e2){
                e2.printStackTrace();;
            }
        });
        jsonGenerator.writeEndArray();;
    }
}
