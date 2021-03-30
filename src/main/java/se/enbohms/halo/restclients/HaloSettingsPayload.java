package se.enbohms.halo.restclients;

import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public class HaloSettingsPayload {

  private String id;
  private String dimmer;
  private String downlight;

  public HaloSettingsPayload(String id, String dimmer, String downlight) {
    this.id = id;
    this.dimmer = dimmer;
    this.downlight = downlight;
  }

  public String getId() {
    return id;
  }

  public String getDimmer() {
    return dimmer;
  }

  public String getDownlight() {
    return downlight;
  }
}
