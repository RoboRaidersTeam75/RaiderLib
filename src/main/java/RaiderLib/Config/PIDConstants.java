package RaiderLib.Config;

public class PIDConstants {

  public double p;
  public double i;
  public double d;
  public double f;

  public PIDConstants(double p, double i, double d, double f) {
    this.p = p;
    this.i = i;
    this.d = d;
    this.f = f;
  }

  public PIDConstants(double p, double i, double d) {
    this.p = p;
    this.i = i;
    this.d = d;
    this.f = 0.0;
  }

  public PIDConstants(double p, double d) {
    this.p = p;
    this.i = 0.0;
    this.d = d;
    this.f = 0.0;
  }
}
