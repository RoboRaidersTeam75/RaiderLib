package RaiderLib.Logging;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.kinematics.SwerveModulePosition;
import edu.wpi.first.math.kinematics.SwerveModuleState;
import java.util.List;

public class LoggedValue {
  public final LoggableType type;
  private final Object value;

  public boolean sendToNT = true;

  enum LoggableType {
    Raw,
    Boolean,
    Integer,
    Float,
    Double,
    String,
    BooleanArray,
    IntegerArray,
    FloatArray,
    DoubleArray,
    StringArray,
    Pose2d,
    SwerveModuleState,
    SwerveModulePosition
  }

  private static final List<String> nt4Types =
      List.of(
          "raw",
          "boolean",
          "int",
          "float",
          "double",
          "string",
          "boolean[]",
          "int[]",
          "float[]",
          "double[]",
          "string[]");

  private static final List<String> wpilogTypes = List.of(
        "raw",
        "boolean",
        "int64",
        "float",
        "double",
        "string",
        "boolean[]",
        "int64[]",
        "float[]",
        "double[]",
        "string[]");

  public String getNT4Type() {
    return nt4Types.get(type.ordinal());
  }

  public String getWPILogType() {
    return wpilogTypes.get(type.ordinal());
  }

  public Object getLogValue() {
    return value;
  }

  LoggedValue(byte[] value) {
    type = LoggableType.Raw;
    this.value = value;
  }

  LoggedValue(boolean value) {
    type = LoggableType.Boolean;
    this.value = value;
  }

  LoggedValue(long value) {
    type = LoggableType.Integer;
    this.value = value;
  }

  LoggedValue(float value) {
    type = LoggableType.Float;
    this.value = value;
  }

  LoggedValue(double value) {
    type = LoggableType.Double;
    this.value = value;
  }

  LoggedValue(String value) {
    type = LoggableType.String;
    if (value != null) {
      this.value = value;
    } else {
      this.value = "";
    }
  }

  LoggedValue(boolean[] value) {
    type = LoggableType.BooleanArray;
    this.value = value;
  }

  LoggedValue(long[] value) {
    type = LoggableType.IntegerArray;
    this.value = value;
  }

  LoggedValue(float[] value) {
    type = LoggableType.FloatArray;
    this.value = value;
  }

  LoggedValue(double[] value) {
    type = LoggableType.DoubleArray;
    this.value = value;
  }

  LoggedValue(String[] value) {
    type = LoggableType.StringArray;
    this.value = value;
  }

  LoggedValue(Pose2d value) {
    type = LoggableType.Pose2d;
    Pose2d[] values = {value};
    this.value = values;
  }

  LoggedValue(Pose2d[] value) {
    type = LoggableType.Pose2d;
    this.value = value;
  }

  LoggedValue(SwerveModuleState value) {
    type = LoggableType.SwerveModuleState;
    SwerveModuleState[] values = {value};
    this.value = values;
  }

  LoggedValue(SwerveModuleState[] value) {
    type = LoggableType.SwerveModuleState;
    this.value = value;
  }

  LoggedValue(SwerveModulePosition value) {
    type = LoggableType.SwerveModulePosition;
    SwerveModulePosition[] values = {value};
    this.value = values;
  }

  LoggedValue(SwerveModulePosition[] value) {
    type = LoggableType.SwerveModulePosition;
    this.value = value;
  }
}
