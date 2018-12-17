package com.hrrm.famoney.application.jaxrs.accounts.internal;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ServiceScope;
import org.osgi.service.jaxrs.whiteboard.propertytypes.JaxrsApplicationSelect;
import org.osgi.service.jaxrs.whiteboard.propertytypes.JaxrsResource;

import com.hrrm.famoney.application.jaxrs.accounts.AccountGroupsApi;
import com.hrrm.famoney.application.jaxrs.accounts.dto.AccountGroupDTO;

@Component(service = AccountGroupsApi.class, scope = ServiceScope.PROTOTYPE)
@JaxrsResource
@JaxrsApplicationSelect("(osgi.jaxrs.name=com.hrrm.famoney.application.jaxrs.accounts)")
public class AccountGroupsApiImpl implements AccountGroupsApi {

    @Override
    public List<AccountGroupDTO> getAllAccountGroups() {
        return Stream.of(new AccountGroupDTO().id("1"))
            .collect(Collectors.toList());
    }

}
