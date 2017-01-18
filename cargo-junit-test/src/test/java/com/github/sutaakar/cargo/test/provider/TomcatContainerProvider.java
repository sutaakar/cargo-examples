package com.github.sutaakar.cargo.test.provider;

import org.codehaus.cargo.container.InstalledLocalContainer;
import org.codehaus.cargo.container.LocalContainer;

public class TomcatContainerProvider extends CargoContainerProvider {

    private static final String tomcatContainerId = "tomcat7x";
    private static final String tomcatDownloadUrl = "http://mirror.dkm.cz/apache/tomcat/tomcat-7/v7.0.73/bin/apache-tomcat-7.0.73.zip";
    private static final String tomcatDeployablePath = "../sample-app/target/sample-app-1.0.0-tomcat.war";

    @Override
    public LocalContainer createLocalContainer() {
        InstalledLocalContainer container = createLocalContainer(tomcatContainerId, tomcatDownloadUrl, tomcatDeployablePath);

        container.getSystemProperties().put("javax.persistence.nonJtaDataSource", "java:comp/env/car/Example");

        return container;
    }
}
