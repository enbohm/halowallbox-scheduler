package se.enbohms.halo.service;


import io.quarkus.runtime.annotations.RegisterForReflection;

import jakarta.json.bind.annotation.JsonbCreator;
import jakarta.json.bind.annotation.JsonbVisibility;
import se.enbohms.halo.PrivateVisibilityStrategy;

@RegisterForReflection
@JsonbVisibility(value = PrivateVisibilityStrategy.class)
public record HaloTokenResponse(String token) {
    @JsonbCreator
    public HaloTokenResponse {
    }
}
