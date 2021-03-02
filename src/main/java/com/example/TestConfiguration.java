package com.example;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.Configuration;

public class TestConfiguration extends Configuration {

    @JsonProperty
    private String jdbcUrl;

    @JsonProperty
    private String jdbcUser;

    @JsonProperty
    private String jdbcPassword;

    public String getJdbcUrl() {
        return jdbcUrl;
    }

    public String getJdbcUser() {
        return jdbcUser;
    }

    public String getJdbcPassword() {
        return jdbcPassword;
    }
}
