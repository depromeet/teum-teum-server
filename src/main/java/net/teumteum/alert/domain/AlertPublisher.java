package net.teumteum.alert.domain;

@FunctionalInterface
public interface AlertPublisher {

    void publish(Alertable alertable);

}
