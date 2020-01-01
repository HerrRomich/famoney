package com.hrrm.famoney.api.datadirectory.resource.internal;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ServiceScope;
import org.osgi.service.jaxrs.whiteboard.propertytypes.JaxrsApplicationSelect;
import org.osgi.service.jaxrs.whiteboard.propertytypes.JaxrsResource;

import com.hrrm.famoney.application.api.datadirectory.dto.EntryCategoriesDTO;
import com.hrrm.famoney.application.api.datadirectory.dto.EntryCategoryDataDTO;
import com.hrrm.famoney.application.api.datadirectory.resources.EntryCategoriesApi;

import io.swagger.v3.oas.annotations.Hidden;

@Component(service = EntryCategoriesApi.class, scope = ServiceScope.BUNDLE)
@JaxrsResource
@JaxrsApplicationSelect("(osgi.jaxrs.name=com.hrrm.famoney.application.api.data-dictionary)")
@Hidden
public class EntryCategoriessApiImpl implements EntryCategoriesApi {

    @Override
    public EntryCategoriesDTO getEntryCategories() {
        // TODO Auto-generated method stub
        // return null;
        throw new UnsupportedOperationException();
    }

    @Override
    public void addEntryCategory(EntryCategoryDataDTO entryCategory) {
        // TODO Auto-generated method stub
        //
        throw new UnsupportedOperationException();
    }

    @Override
    public EntryCategoryDataDTO changeEntryCategory(Integer id, EntryCategoryDataDTO entryCategory) {
        // TODO Auto-generated method stub
        // return null;
        throw new UnsupportedOperationException();
    }

    @Override
    public void deleteEntryCategory(Integer id) {
        // TODO Auto-generated method stub
        //
        throw new UnsupportedOperationException();
    }

}
