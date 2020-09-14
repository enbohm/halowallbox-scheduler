package se.enbohms.halo;


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
