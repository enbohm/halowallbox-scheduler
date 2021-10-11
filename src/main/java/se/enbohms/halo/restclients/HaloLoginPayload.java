package se.enbohms.halo.restclients;

import io.quarkus.runtime.annotations.RegisterForReflection;
import se.enbohms.halo.PrivateVisibilityStrategy;

import javax.json.bind.annotation.JsonbCreator;
import javax.json.bind.annotation.JsonbVisibility;


@RegisterForReflection
@JsonbVisibility(value = PrivateVisibilityStrategy.class)
public record HaloLoginPayload(String email, String password) {

    @JsonbCreator
    public HaloLoginPayload {
    }
}
