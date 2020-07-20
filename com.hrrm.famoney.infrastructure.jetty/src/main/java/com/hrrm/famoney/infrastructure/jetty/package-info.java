@Version("1.0.0")
@RequireConfigurator

@Requirement(filter = "(osgi.extender=osgi.serviceloader.processor)", namespace = "osgi.extender")
@Requirement(filter = "(osgi.serviceloader=org.eclipse.jetty.http.HttpFieldPreEncoder)",
        namespace = "osgi.serviceloader", cardinality = Cardinality.MULTIPLE)
package com.hrrm.famoney.infrastructure.jetty;

import org.osgi.annotation.bundle.Requirement;
import org.osgi.annotation.bundle.Requirement.Cardinality;
import org.osgi.annotation.versioning.Version;
import org.osgi.service.configurator.annotations.RequireConfigurator;
