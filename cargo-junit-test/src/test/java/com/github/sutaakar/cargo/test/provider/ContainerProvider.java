package com.github.sutaakar.cargo.test.provider;

import org.codehaus.cargo.container.LocalContainer;

public interface ContainerProvider {

    LocalContainer createLocalContainer();
}
