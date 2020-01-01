package com.hrrm.famoney.api.accounts.resource.internal;

import javax.ws.rs.core.Application;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ServiceScope;
import org.osgi.service.http.whiteboard.HttpWhiteboardConstants;
import org.osgi.service.http.whiteboard.propertytypes.HttpWhiteboardContextSelect;
import org.osgi.service.jaxrs.whiteboard.propertytypes.JSONRequired;
import org.osgi.service.jaxrs.whiteboard.propertytypes.JaxrsApplicationBase;
import org.osgi.service.jaxrs.whiteboard.propertytypes.JaxrsName;

import com.hrrm.famoney.api.accounts.AccountsApiSpecification;
import com.hrrm.famoney.infrastructure.jaxrs.ApiSpecification;

@Component(service = { Application.class, ApiSpecification.class }, scope = ServiceScope.SINGLETON)
@JaxrsName("com.hrrm.famoney.application.api.accounts")
@JaxrsApplicationBase(AccountsApplication.API_PATH)
@HttpWhiteboardContextSelect("("
        + HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_NAME
        + "=com.hrrm.famoney.api)")
@JSONRequired
public class AccountsApplication extends AccountsApiSpecification {

}
