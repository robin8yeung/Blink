package com.seewo.blink.fragment.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 定义页面处于非栈顶时是否保活，不保活的，返回栈顶时会重建并无法保持状态
 *
 * 对于未使用KeepAlive注解去定义的Fragment，默认为非保活
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface KeepAlive {
    boolean value() default true;
}
