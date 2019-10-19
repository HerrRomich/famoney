package com.hrrm.famoney.api.accounts.resource.internal;

import java.io.InputStream;

import javax.ws.rs.core.Application;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ServiceScope;
import org.osgi.service.http.whiteboard.HttpWhiteboardConstants;
import org.osgi.service.http.whiteboard.propertytypes.HttpWhiteboardContextSelect;
import org.osgi.service.jaxrs.whiteboard.propertytypes.JSONRequired;
import org.osgi.service.jaxrs.whiteboard.propertytypes.JaxrsApplicationBase;
import org.osgi.service.jaxrs.whiteboard.propertytypes.JaxrsName;

import com.hrrm.famoney.infrastructure.jaxrs.ApiSpecification;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.tags.Tag;

@Component(service = { Application.class, ApiSpecification.class }, scope = ServiceScope.SINGLETON)
@JaxrsName("com.hrrm.famoney.application.api.accounts")
@JaxrsApplicationBase(AccountsApplication.API_PATH)
@HttpWhiteboardContextSelect("(" + HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_NAME + "=com.hrrm.famoney.api)")
@JSONRequired
@OpenAPIDefinition(info = @Info(title = "Accounts", version = "1.0.0"), tags = {
        @Tag(name = "accounts", description = "Accounts related requests") })
public class AccountsApplication extends Application implements ApiSpecification {

    static final String API_PATH = "accounts";

    @Override
    public String getPath() {
        return API_PATH;
    }

    @Override
    public String getDescription() {
        return "API Specification regarding Accounts.";
    }

    @Override
    public InputStream getSpecificationStream() {
        return this.getClass()
            .getResourceAsStream("/accounts.json");
    }

}
