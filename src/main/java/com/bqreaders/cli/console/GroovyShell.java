/*
 * Copyright (C) 2013 StarTIC
 */
package com.bqreaders.cli.console;

import groovy.lang.Binding;
import groovy.lang.GroovyClassLoader;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.List;

import org.codehaus.groovy.control.CompilerConfiguration;
import org.codehaus.groovy.tools.shell.Groovysh;
import org.codehaus.groovy.tools.shell.IO;

/**
 * @author Alexander De Leon
 * 
 */
public class GroovyShell {
	private static final String SEPARATOR_BAR = "-----------------------------------------------------------";

	public static final String OUT_KEY = "out";

	private final InputStream in;
	private final OutputStream out;
	private final PrintStream writer;

	private final Binding binding;
	private final String welcomeMessage;

	public GroovyShell(InputStream in, OutputStream out, Binding binding) {
		this(in, out, binding, null);
	}

	public GroovyShell(InputStream in, OutputStream out, Binding binding, String welcomeMessage) {
		this.in = in;
		this.out = out;
		this.binding = binding;
		this.welcomeMessage = welcomeMessage;
		writer = new PrintStream(out);
	}

	public void run() throws GroovyShellException {
		try {
			final Groovysh shell = createShell();
			shell.run((String) null);
		} catch (Exception e) {
			throw new GroovyShellException(e);
		}
	}

	private Groovysh createShell() {
		if (welcomeMessage != null) {
			writer.println(SEPARATOR_BAR);
			writer.println(welcomeMessage);
			writer.println(SEPARATOR_BAR);
		}
		binding.setVariable(OUT_KEY, out);
		final CompilerConfiguration config = new CompilerConfiguration();

		final GroovyClassLoader loader = new GroovyClassLoader(this.getClass().getClassLoader(), config);
		final Groovysh shell = new Groovysh(loader, binding, new IO(in, out, out));
		writer.flush();
		return shell;
	}

	public void runScripts(List<File> files) {

		final Groovysh shell = createShell();
		try {
			for (File file : files) {
				if (!file.exists() || !file.canRead() || !file.isFile()) {
					throw new GroovyShellException("Unable to open script file: " + file);
				}
				shell.execute(":load " + file.getAbsolutePath());
			}
		} catch (Exception e) {
			throw new GroovyShellException(e);
		}
	}

}
