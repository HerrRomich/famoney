@Version("1.0.0")
@RequireConfigurator
@RequireJPAExtender

@Requirement(namespace = "com.hrrm.famoney.infrastructure.persistence.migrations",
        filter = "(migration.domain=famoney.master-data)", version = "1.0")

package com.hrrm.famoney.domain.masterdata;

import org.osgi.annotation.bundle.Requirement;
import org.osgi.annotation.versioning.Version;
import org.osgi.service.configurator.annotations.RequireConfigurator;
import org.osgi.service.jpa.annotations.RequireJPAExtender;
