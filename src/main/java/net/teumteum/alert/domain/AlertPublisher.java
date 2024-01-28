package net.teumteum.alert.domain;

@FunctionalInterface
public interface AlertPublisher<T extends Alertable> {

    void publish(T alertable);

}
