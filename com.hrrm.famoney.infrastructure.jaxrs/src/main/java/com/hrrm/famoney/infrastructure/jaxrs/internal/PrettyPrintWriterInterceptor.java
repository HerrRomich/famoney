package com.hrrm.famoney.infrastructure.jaxrs.internal;

import java.io.IOException;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.WriterInterceptor;
import javax.ws.rs.ext.WriterInterceptorContext;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.jaxrs.whiteboard.propertytypes.JSONRequired;
import org.osgi.service.jaxrs.whiteboard.propertytypes.JaxrsApplicationSelect;
import org.osgi.service.jaxrs.whiteboard.propertytypes.JaxrsExtension;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.jaxrs.cfg.EndpointConfigBase;
import com.fasterxml.jackson.jaxrs.cfg.ObjectWriterInjector;
import com.fasterxml.jackson.jaxrs.cfg.ObjectWriterModifier;

@Component(service = {
		WriterInterceptor.class
})
@JaxrsExtension
@JSONRequired
@JaxrsApplicationSelect("(osgi.jaxrs.name=com.hrrm.famoney.application.api.*)")
public class PrettyPrintWriterInterceptor implements WriterInterceptor {

	private static final DefaultPrettyPrinter PRETTY_PRINTER = new DefaultPrettyPrinter();

	@Override
	public void aroundWriteTo(WriterInterceptorContext context) throws IOException, WebApplicationException {
		ObjectWriterInjector.set(new ObjectWriterModifier() {

			@Override
			public ObjectWriter modify(EndpointConfigBase<?> endpoint,
					MultivaluedMap<String, Object> responseHeaders, Object valueToWrite, ObjectWriter w,
					JsonGenerator g) throws IOException {
				return w.with(PRETTY_PRINTER);
			}
		});
		context.proceed();
	}

}
