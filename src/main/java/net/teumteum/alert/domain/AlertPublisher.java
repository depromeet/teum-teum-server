package net.teumteum.alert.domain;

@FunctionalInterface
public interface AlertPublisher {

    void publish(String token, Alert alert);

}
