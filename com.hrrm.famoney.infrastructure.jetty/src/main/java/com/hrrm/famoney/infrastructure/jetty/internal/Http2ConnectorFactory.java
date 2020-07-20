package com.hrrm.famoney.infrastructure.jetty.internal;

import java.io.IOException;

import org.apache.felix.http.jetty.ConnectorFactory;
import org.eclipse.jetty.alpn.server.ALPNServerConnectionFactory;
import org.eclipse.jetty.http.HttpVersion;
import org.eclipse.jetty.http2.server.HTTP2ServerConnectionFactory;
import org.eclipse.jetty.server.ConnectionFactory;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.SecureRequestCustomizer;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.SslConnectionFactory;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.osgi.framework.BundleContext;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.log.Logger;
import org.osgi.service.log.LoggerFactory;

@Component
public class Http2ConnectorFactory implements ConnectorFactory {

    private BundleContext context;
    private Logger logger;
    private ConfigurationAdmin configurationAdmin;

    @Activate
    public Http2ConnectorFactory(BundleContext context, @Reference(service = LoggerFactory.class) Logger logger,
            @Reference ConfigurationAdmin configurationAdmin) {
        this.context = context;
        this.logger = logger;
        this.configurationAdmin = configurationAdmin;
    }

    @Override
    public Connector createConnector(Server server) {
        try {
            final var jettyProperties = configurationAdmin.getFactoryConfiguration("org.apache.felix.http",
                    "famoney")
                .getProperties();
            final var jettyConfig = new JettyConfig(context);
            jettyConfig.update(jettyProperties);
            final var serverConnector = new ServerConnector(server,
                getConnectorFactories(jettyConfig));
            serverConnector.setPort(jettyConfig.getHttpsPort());
            return serverConnector;
        } catch (IOException e) {
            logger.error("Couldn't read properties for PID: {}.",
                    "org.apache.felix.http",
                    e);
            return null;
        }
    }

    private ConnectionFactory[] getConnectorFactories(JettyConfig jettyConfig) {
        final var httpConfig = new HttpConfiguration();
        httpConfig.addCustomizer(new SecureRequestCustomizer());

        final var h2 = new HTTP2ServerConnectionFactory(httpConfig);

        final var alpn = new ALPNServerConnectionFactory();
        alpn.setDefaultProtocol(HttpVersion.HTTP_1_1.asString());

        final var sslContextFactory = new SslContextFactory.Server();
        configureSslContextFactory(sslContextFactory,
                jettyConfig);
        sslContextFactory.setIncludeProtocols("TLSv1.2");
        sslContextFactory.setProtocol("TLSv1.2");
        sslContextFactory.setIncludeCipherSuites("TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256",
                "TLS_DHE_RSA_WITH_AES_128_GCM_SHA256");
        sslContextFactory.setEndpointIdentificationAlgorithm("HTTPS");
        return new ConnectionFactory[] {
                new SslConnectionFactory(sslContextFactory,
                    alpn.getProtocol()),
                alpn,
                h2,
                new HttpConnectionFactory(httpConfig)
        };

    }

    private void configureSslContextFactory(final SslContextFactory connector, final JettyConfig jettyConfig) {
        if (jettyConfig.getKeystoreType() != null) {
            connector.setKeyStoreType(jettyConfig.getKeystoreType());
        }

        if (jettyConfig.getKeystore() != null) {
            connector.setKeyStorePath(jettyConfig.getKeystore());
        }

        if (jettyConfig.getPassword() != null) {
            connector.setKeyStorePassword(jettyConfig.getPassword());
        }

        if (jettyConfig.getKeyPassword() != null) {
            connector.setKeyManagerPassword(jettyConfig.getKeyPassword());
        }

        if (jettyConfig.getTruststoreType() != null) {
            connector.setTrustStoreType(jettyConfig.getTruststoreType());
        }

        if (jettyConfig.getTruststore() != null) {
            connector.setTrustStorePath(jettyConfig.getTruststore());
        }

        if (jettyConfig.getTrustPassword() != null) {
            connector.setTrustStorePassword(jettyConfig.getTrustPassword());
        }

        if ("wants".equalsIgnoreCase(jettyConfig.getClientcert())) {
            connector.setWantClientAuth(true);
        } else if ("needs".equalsIgnoreCase(jettyConfig.getClientcert())) {
            connector.setNeedClientAuth(true);
        }

        if (jettyConfig.getExcludedCipherSuites() != null) {
            connector.setExcludeCipherSuites(jettyConfig.getExcludedCipherSuites());
        }

        if (jettyConfig.getIncludedCipherSuites() != null) {
            connector.setIncludeCipherSuites(jettyConfig.getIncludedCipherSuites());
        }

        if (jettyConfig.getIncludedProtocols() != null) {
            connector.setIncludeProtocols(jettyConfig.getIncludedProtocols());
        }

        if (jettyConfig.getExcludedProtocols() != null) {
            connector.setExcludeProtocols(jettyConfig.getExcludedProtocols());
        }

        connector.setRenegotiationAllowed(jettyConfig.isRenegotiationAllowed());
    }

}
