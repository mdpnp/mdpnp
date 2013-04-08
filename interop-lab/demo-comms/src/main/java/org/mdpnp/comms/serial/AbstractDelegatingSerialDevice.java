package org.mdpnp.comms.serial;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.mdpnp.comms.Gateway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractDelegatingSerialDevice<T> extends AbstractSerialDevice {
	public AbstractDelegatingSerialDevice(Gateway gateway) {
		super(gateway);
	}
	private InputStream  inputStream;
	private OutputStream outputStream;
	private T delegate;
	
	protected synchronized void setOutputStream(OutputStream outputStream) {
		this.outputStream = outputStream;
		notifyAll();
	}
	
	protected synchronized void setInputStream(InputStream inputStream) {
		this.inputStream = inputStream;
		notifyAll();
	}
	private final Logger log = LoggerFactory.getLogger(AbstractDelegatingSerialDevice.class);
	
	protected abstract T buildDelegate(InputStream in, OutputStream out);
	protected abstract boolean delegateReceive(T delegate) throws IOException;
	
	protected synchronized T getDelegate() {
		while(null == delegate && (inputStream == null || outputStream == null)) {
			try {
				log.trace("waiting, inputStream="+inputStream+", outputStream="+outputStream);
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		if(null == delegate) {
			delegate = buildDelegate(inputStream, outputStream);
		}
		return delegate;
	}
	@Override
	protected void process(InputStream inputStream) throws IOException {
		log.trace("process inputStream="+inputStream);
		try {
			setInputStream(inputStream);
			final T delegate = getDelegate();
			boolean keepGoing = true;
			while(keepGoing) {
				keepGoing = delegateReceive(delegate);
			}
		} finally {
			this.inputStream = null;
			this.outputStream = null;
			this.delegate = null;
			log.trace("process ends");
		}
	}
}
