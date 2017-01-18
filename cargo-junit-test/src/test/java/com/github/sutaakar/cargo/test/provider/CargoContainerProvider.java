package com.github.sutaakar.cargo.test.provider;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import org.codehaus.cargo.container.ContainerType;
import org.codehaus.cargo.container.InstalledLocalContainer;
import org.codehaus.cargo.container.configuration.ConfigurationType;
import org.codehaus.cargo.container.configuration.LocalConfiguration;
import org.codehaus.cargo.container.configuration.builder.ConfigurationEntryType;
import org.codehaus.cargo.container.configuration.entry.DataSource;
import org.codehaus.cargo.container.deployable.WAR;
import org.codehaus.cargo.container.installer.ZipURLInstaller;
import org.codehaus.cargo.container.property.TransactionSupport;
import org.codehaus.cargo.container.property.User;
import org.codehaus.cargo.generic.DefaultContainerFactory;
import org.codehaus.cargo.generic.configuration.DefaultConfigurationFactory;
import org.codehaus.cargo.util.log.SimpleLogger;

public abstract class CargoContainerProvider implements ContainerProvider {

    protected InstalledLocalContainer createLocalContainer(String containerId, String downloadUrl, String deployablePath) {
        String containerHome = installContainer(downloadUrl);

        LocalConfiguration configuration = (LocalConfiguration)
                new DefaultConfigurationFactory().createConfiguration(
                containerId, ContainerType.INSTALLED, ConfigurationType.STANDALONE);

        InstalledLocalContainer container = (InstalledLocalContainer)
                new DefaultContainerFactory().createContainer(
                containerId, ContainerType.INSTALLED, configuration);

        configuration.setLogger(new SimpleLogger());
        container.setLogger(new SimpleLogger());

        WAR warFile = new WAR(deployablePath);
        warFile.setContext("sample-app");
        configuration.addDeployable(warFile);

        DataSource ds = new DataSource();
        ds.setId("myDs");
        ds.setDriverClass("org.h2.Driver");
        ds.setUrl("jdbc:h2:mem:test-db");
        ds.setJndiLocation("car/Example");
        ds.setUsername("sa");
        ds.setPassword("");
        ds.setTransactionSupport(TransactionSupport.NO_TRANSACTION);
        ds.setConnectionType(ConfigurationEntryType.JDBC_DRIVER);
        configuration.addDataSource(ds);

        User john = new User();
        john.setName("john");
        john.setPassword("john123");
        john.addRole("car-owner");
        configuration.addUser(john);

        User mary = new User();
        mary.setName("mary");
        mary.setPassword("mary1234");
        mary.addRole("owner");
        configuration.addUser(mary);

        File driver = new File("src/test/resources/h2-1.3.176.jar");
        container.addSharedClasspath(driver.getAbsolutePath());

        container.setHome(containerHome);

        return container;
    }

    private String installContainer(String downloadUrlStr)
    {
        URL downloadUrl;
        try {
            downloadUrl = new URL(downloadUrlStr);
        } catch (MalformedURLException e) {
            throw new RuntimeException("Cannot parse provided URL.");
        }

        ZipURLInstaller installer = new ZipURLInstaller(downloadUrl);
        installer.install();

        return installer.getHome();
    }
}
