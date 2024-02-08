package net.teumteum.alert.domain;

import java.util.Map;

@FunctionalInterface
public interface AlertPublisher {

    void publish(String token, Alert alert, Map<String, String> data);

}
