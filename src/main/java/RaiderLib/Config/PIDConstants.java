package RaiderLib.Config;

public class PIDConstants {

  public double kP = 0.0;
  public double kI = 0.0;
  public double kD = 0.0;
  public double kF = 0.0;

  public PIDConstants() {}

  public PIDConstants(double p, double i, double d, double f) {
    this.kP = p;
    this.kI = i;
    this.kD = d;
    this.kF = f;
  }

  public PIDConstants(double p, double i, double d) {
    this.kP = p;
    this.kI = i;
    this.kD = d;
    this.kF = 0.0;
  }

  public PIDConstants(double p, double d) {
    this.kP = p;
    this.kI = 0.0;
    this.kD = d;
    this.kF = 0.0;
  }
}
