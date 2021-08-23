package com.bpkim.studyrestapi.common;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/*
 * 테스트 코드의 할일을 정의할 애노테이션
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.SOURCE)
public @interface TestDescription {

    String value();
}
