package com.lowdragmc.lowdraglib.plugin;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

// This is from GregTech Modern / JEI
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface LDLibPlugin {
}
