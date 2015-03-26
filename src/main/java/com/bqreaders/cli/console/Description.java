/*
 * Copyright (C) 2013 StarTIC
 */
package com.bqreaders.cli.console;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to provide to documentation for console operations.
 * 
 * @author Alexander De Leon
 * 
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Description {

	String value();

}
