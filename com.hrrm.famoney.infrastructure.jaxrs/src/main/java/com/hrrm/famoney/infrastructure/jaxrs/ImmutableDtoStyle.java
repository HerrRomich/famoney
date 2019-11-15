package com.hrrm.famoney.infrastructure.jaxrs;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.immutables.value.Value;

@Target({ ElementType.PACKAGE, ElementType.TYPE })
@Retention(RetentionPolicy.CLASS)
@Value.Style(packageGenerated = "*.internal", typeImmutable = "*Impl", overshadowImplementation = true)
public @interface ImmutableDtoStyle {

}
