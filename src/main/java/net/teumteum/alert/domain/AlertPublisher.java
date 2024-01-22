package net.teumteum.alert.domain;

import net.teumteum.alert.domain.Alert;

@FunctionalInterface
public interface AlertPublisher {

    void publish(Alert alert);

}
