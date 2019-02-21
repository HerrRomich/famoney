@Version("1.0.0")
@OpenAPIDefinition(info = @Info(title = "Accounts", version = "1.0.0"), tags = {
        @Tag(name = "accounts", description = "Accounts related requests") }, servers = {
                @Server(url = "/api/accounts") })
package com.hrrm.famoney.api.accounts;

import org.osgi.annotation.versioning.Version;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;
import io.swagger.v3.oas.annotations.tags.Tag;
