package com.github.sutaakar.cargo.test.provider;

import org.codehaus.cargo.container.InstalledLocalContainer;
import org.codehaus.cargo.container.LocalContainer;

public class WildFlyContainerProvider extends CargoContainerProvider {

    private static final String wildFlyContainerId = "wildfly10x";
    private static final String wildFlyDownloadUrl = "http://download.jboss.org/wildfly/10.1.0.Final/wildfly-10.1.0.Final.zip";
    private static final String wildFlyDeployablePath = "../sample-app/target/sample-app-1.0.0-wildfly.war";

    @Override
    public LocalContainer createLocalContainer() {
        InstalledLocalContainer container = createLocalContainer(wildFlyContainerId, wildFlyDownloadUrl, wildFlyDeployablePath);

        container.getSystemProperties().put("javax.persistence.nonJtaDataSource", "java:/car/Example");

        return container;
    }
}
