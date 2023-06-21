package com.seewo.blink.fragment.annotation;

import androidx.annotation.ColorInt;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 定义页面背景颜色，未设置则取窗口背景颜色，取不到则为白色
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Background {
    @ColorInt int value();
}
