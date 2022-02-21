package com.david.anno;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

/**
 * mark dynamic enum.
 *
 * @author david
 * @since 2022/2/20
 */
@Target(ElementType.TYPE)
public @interface DEnum {
}
