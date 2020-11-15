package com.hrrm.famoney.api.masterdata.resource.internal;

import javax.ws.rs.core.Application;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.http.whiteboard.HttpWhiteboardConstants;
import org.osgi.service.http.whiteboard.propertytypes.HttpWhiteboardContextSelect;
import org.osgi.service.jaxrs.whiteboard.propertytypes.JSONRequired;
import org.osgi.service.jaxrs.whiteboard.propertytypes.JaxrsApplicationBase;
import org.osgi.service.jaxrs.whiteboard.propertytypes.JaxrsName;

import com.hrrm.famoney.application.api.masterdata.MasterDataApiSpecification;
import com.hrrm.famoney.infrastructure.jaxrs.ApiSpecification;

@Component(service = {
        Application.class,
        ApiSpecification.class
})
@JaxrsName("com.hrrm.famoney.application.api.master-data")
@JaxrsApplicationBase(MasterDataApiSpecification.API_PATH)
@HttpWhiteboardContextSelect("(" + HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_NAME + "=com.hrrm.famoney.apis)")
@JSONRequired
public class MasterDataApplication extends MasterDataApiSpecification {

}
