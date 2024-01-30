package net.teumteum.alert.domain;

public interface Alertable {

    String token();

    String title();

    String body();

    String type();
}
