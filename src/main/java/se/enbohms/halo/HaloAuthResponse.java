package se.enbohms.halo;


import io.quarkus.runtime.annotations.RegisterForReflection;

import jakarta.json.bind.annotation.JsonbCreator;
import jakarta.json.bind.annotation.JsonbVisibility;

@RegisterForReflection
@JsonbVisibility(value = PrivateVisibilityStrategy.class)
public record HaloAuthResponse(String token) {
    @JsonbCreator
    public HaloAuthResponse {
    }
}
