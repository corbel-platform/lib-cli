package io.corbel.lib.cli.console

import com.thoughtworks.paranamer.BytecodeReadingParanamer
import com.thoughtworks.paranamer.Paranamer

import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * @author Alexander De Leon
 *
 */
public class Console {

	private static final Logger LOG = LoggerFactory.getLogger(Console.class)

	Map<String, Object> shells
	String welcomeMessage

	public Console(String welcomeMessage, String namespace, Object shell) {
		this(welcomeMessage, Collections.singletonMap(namespace, shell))
	}

	public Console(String welcomeMessage, Map<String, Object> shells){
		this.shells = shells
		this.welcomeMessage = welcomeMessage
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
		addShells(binding)
		addHelpClosure(binding)
		init(binding)
		LOG.info("Starting GroovyShellThread for shells: ${shells.keySet()}")
		new GroovyShell(System.in, System.out, binding, welcomeMessage)
	}

	private void addShells(Binding binding) {
		shells.each { namespace, shell ->
			binding.setVariable(namespace, shell)
		}
	}

	private void addHelpClosure(Binding binding) {
		shells.each { namespace, shell ->
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
	}

	String[] getParams(method) {
		Paranamer paranamer = new BytecodeReadingParanamer()
		paranamer.lookupParameterNames(method)
	}
}
