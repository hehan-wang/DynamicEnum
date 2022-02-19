package me.vukas.anno;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

/**
 * 标注为动态注解
 * @author david
 * @since 2022/2/20
 */
@Target(ElementType.TYPE)
public @interface DEnum {
}
