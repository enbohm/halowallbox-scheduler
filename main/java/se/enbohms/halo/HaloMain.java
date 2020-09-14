package se.enbohms.halo;

import io.quarkus.runtime.Quarkus;
import io.quarkus.runtime.annotations.QuarkusMain;

@QuarkusMain
public class HaloMain {

  public static void main(String... args) {
    Quarkus.run(args);
  }
}
