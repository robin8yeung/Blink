package com.seewo.blink.fragment.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 默认页面方向(参考以下常量设置)
 *
 * @see android.content.pm.ActivityInfo#SCREEN_ORIENTATION_LANDSCAPE
 * @see android.content.pm.ActivityInfo#SCREEN_ORIENTATION_PORTRAIT
 * @see android.content.pm.ActivityInfo#SCREEN_ORIENTATION_UNSPECIFIED
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Orientation {
    int value();
}
