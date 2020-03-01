package com.hrrm.famoney.domain.datadirectory.migration;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.immutables.value.Value;

@Target({
        ElementType.PACKAGE,
        ElementType.TYPE
})
@Retention(RetentionPolicy.CLASS)
@Value.Style(packageGenerated = "*", typeImmutable = "*Impl", overshadowImplementation = true)
public @interface ImmutableDataObjectStyle {

}
