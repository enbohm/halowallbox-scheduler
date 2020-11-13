package se.enbohms.halo;


import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public class HaloAuthResponse {

  private String token;

  public HaloAuthResponse() {
  }

  public HaloAuthResponse(String token) {
    this.token = token;
  }

  public String getToken() {
    return token;
  }

  public void setToken(String token) {
    this.token = token;
  }
}
