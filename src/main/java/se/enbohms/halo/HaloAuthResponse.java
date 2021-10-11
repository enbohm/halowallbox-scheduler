package se.enbohms.halo;


import io.quarkus.runtime.annotations.RegisterForReflection;

import javax.json.bind.annotation.JsonbCreator;
import javax.json.bind.annotation.JsonbVisibility;

@RegisterForReflection
@JsonbVisibility(value = PrivateVisibilityStrategy.class)
public record HaloAuthResponse(String token) {
    @JsonbCreator
    public HaloAuthResponse {
    }
}
