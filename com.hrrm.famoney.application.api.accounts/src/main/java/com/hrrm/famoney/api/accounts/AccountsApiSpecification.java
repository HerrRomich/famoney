package com.hrrm.famoney.api.accounts;

import java.io.InputStream;

import com.hrrm.famoney.infrastructure.jaxrs.ApiSpecification;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.tags.Tag;

@OpenAPIDefinition(
        info = @Info(title = "Accounts", version = "1.0.0"),
        tags = { @Tag(name = "accounts", description = "Accounts related requests") })
public interface AccountsApiSpecification extends ApiSpecification {
    static final String API_PATH = "accounts";

    @Override
    default String getPath() {
        return API_PATH;
    }

    @Override
    default String getDescription() {
        return "API Specification regarding Accounts.";
    }

    @Override
    default InputStream getSpecificationStream() {
        return AccountsApiSpecification.class.getResourceAsStream("/accounts.json");
    }

}
