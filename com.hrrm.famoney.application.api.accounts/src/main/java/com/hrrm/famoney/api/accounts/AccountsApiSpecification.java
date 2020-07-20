package com.hrrm.famoney.api.accounts;

import java.io.InputStream;

import javax.ws.rs.core.Application;

import com.hrrm.famoney.infrastructure.jaxrs.ApiSpecification;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.tags.Tag;

@OpenAPIDefinition(info = @Info(title = "Accounts", version = "1.0.0"), tags = {
        @Tag(name = "accounts", description = "Accounts related requests.")
})
public abstract class AccountsApiSpecification extends Application implements ApiSpecification {

    public static final String API_PATH = "accounts";

    @Override
    public final String getPath() {
        return API_PATH;
    }

    @Override
    public final String getDescription() {
        return "API Specification regarding Accounts.";
    }

    @Override
    public final InputStream getSpecificationStream() {
        return AccountsApiSpecification.class.getResourceAsStream("/accounts.json");
    }

}
