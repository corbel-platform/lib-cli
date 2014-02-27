/*
 * Copyright (C) 2013 StarTIC
 */
package com.bqreaders.cli.console

import com.thoughtworks.paranamer.BytecodeReadingParanamer
import com.thoughtworks.paranamer.Paranamer
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * @author Alexander De Leon
 *
 */
public class Console<E> {

	private static final Logger LOG = LoggerFactory.getLogger(Console.class)

	E shell
	String welcomeMessage
	String namespace

	public Console(String welcomeMessage, String namespace, E shell) {
		this.shell = shell
		this.welcomeMessage = welcomeMessage
		this.namespace = namespace
	}


	public void launch() {
		getGroovyShell().run()
	}

	public void runScripts(String... filePaths) {
		getGroovyShell().runScripts(filePaths.collect{ new File(it)})
	}

	/**
	 * Override by subclass to do initialization stuff
	 */
	protected void init(Binding binding) {
	}

	private GroovyShell getGroovyShell() {
		Binding binding = new Binding()
		addShell(binding)
		addHelpClosure(binding)
		init(binding)
		LOG.info("Starting GroovyShellThread for ${shell.class.name}")
		new GroovyShell(System.in, System.out, binding, welcomeMessage)
	}

	private void addShell(Binding binding) {
		binding.setVariable(namespace, shell)
	}

	private void addHelpClosure(Binding binding) {
		final StringBuffer buffer = new StringBuffer('\n')
		shell.class.methods.each {
			Description desc = it.getAnnotation(Description.class)
			if (desc) {
				buffer.append(namespace).append('.').append(it.name).append('(')
				getParams(it).each { paramType ->
					buffer.append(paramType).append(',')
				}
				if (buffer.getAt(buffer.length() - 1) == ',') {
					buffer.deleteCharAt(buffer.length() - 1)
				}
				buffer.append('): ')
				buffer.append(desc.value()).append('\n')
			}
		}
		shell.metaClass.help { buffer.toString() }
	}

	String[] getParams(method) {
		Paranamer paranamer = new BytecodeReadingParanamer()
		paranamer.lookupParameterNames(method)
	}
}
