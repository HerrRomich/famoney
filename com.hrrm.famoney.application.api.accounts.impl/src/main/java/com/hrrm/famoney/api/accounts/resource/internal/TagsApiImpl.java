package com.hrrm.famoney.api.accounts.resource.internal;

import java.util.List;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.jaxrs.whiteboard.propertytypes.JaxrsApplicationSelect;
import org.osgi.service.jaxrs.whiteboard.propertytypes.JaxrsResource;
import org.osgi.service.log.Logger;
import org.osgi.service.log.LoggerFactory;

import com.hrrm.famoney.api.accounts.resource.TagsApi;
import com.hrrm.famoney.domain.accounts.repository.AccountRepository;

import io.swagger.v3.oas.annotations.Hidden;

@Component(service = TagsApi.class)
@JaxrsResource
@JaxrsApplicationSelect("(osgi.jaxrs.name=com.hrrm.famoney.application.api.accounts)")
@Hidden
public class TagsApiImpl implements TagsApi {

    private final Logger logger;
    private final AccountRepository accountRepository;

    @Activate
    public TagsApiImpl(@Reference(service = LoggerFactory.class) Logger logger,
            @Reference AccountRepository accountRepository) {
        super();
        this.logger = logger;
        this.accountRepository = accountRepository;
    }

    @Override
    public List<String> getAllAccountTags() {
        logger.debug("Getting account tags.");
        List<String> allTags = accountRepository.findAllTags();
        logger.debug("Successfully got account tags.");
        logger.trace("Sucessfully got {} account tags.",
                allTags.size());
        return allTags;
    }

}
