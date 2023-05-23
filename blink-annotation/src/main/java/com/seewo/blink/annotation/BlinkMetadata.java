package com.seewo.blink.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 没有任何实际作用，仅为了方便标注Uri
 */
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.TYPE)
public @interface BlinkMetadata {
    String[] value();
}
