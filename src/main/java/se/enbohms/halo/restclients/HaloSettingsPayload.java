package se.enbohms.halo.restclients;

import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public record HaloSettingsPayload(String id, String dimmer, String downLight) {
}
