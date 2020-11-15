package com.hrrm.famoney.application.api.masterdata;

import java.io.InputStream;

import javax.ws.rs.core.Application;

import com.hrrm.famoney.infrastructure.jaxrs.ApiSpecification;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.tags.Tag;

@OpenAPIDefinition(info = @Info(title = "Master data", version = "1.0.0"), tags = { @Tag(name = "master-data",
    description = "Master data requests.") })
public abstract class MasterDataApiSpecification extends Application implements ApiSpecification {

    public static final String API_PATH = "master-data-domain";

    @Override
    public final String getPath() {
        return API_PATH;
    }

    @Override
    public final String getDescription() {
        return "API Specification regarding master data.";
    }

    @Override
    public final InputStream getSpecificationStream() {
        return MasterDataApiSpecification.class.getResourceAsStream("/master-data.json");
    }

    @Override
    public String toString() {
        return "MasterDataApiSpecification [path=" + getPath() + ", description=" + getDescription() + "]";
    }

}
