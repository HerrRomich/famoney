package com.hrrm.famoney.application.api.datadirectory;

import java.io.InputStream;

import javax.ws.rs.core.Application;

import com.hrrm.famoney.infrastructure.jaxrs.ApiSpecification;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.tags.Tag;

@OpenAPIDefinition(info = @Info(title = "Data directory", version = "1.0.0"), tags = { @Tag(name = "data-directory",
    description = "Basic data requests.") })
public abstract class DataDirectoryApiSpecification extends Application implements ApiSpecification {

    public static final String API_PATH = "data-directory";

    @Override
    public final String getPath() {
        return API_PATH;
    }

    @Override
    public final String getDescription() {
        return "API Specification regarding basic data.";
    }

    @Override
    public final InputStream getSpecificationStream() {
        return DataDirectoryApiSpecification.class.getResourceAsStream("/data-directory.json");
    }

}
