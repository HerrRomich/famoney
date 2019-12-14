package com.hrrm.famoney.infrastructure.jaxrs;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.immutables.annotate.InjectAnnotation;
import org.immutables.annotate.InjectAnnotation.Where;

import com.fasterxml.jackson.annotation.JsonInclude;

@InjectAnnotation(
        target = { Where.FIELD },
        type = JsonInclude.class,
        code = "(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)")
@Target({ ElementType.ANNOTATION_TYPE, ElementType.METHOD, ElementType.FIELD, ElementType.TYPE,
        ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface JsonIncludeNonNull {
}
