package RaiderLib.Logging;

import java.util.HashMap;
import java.util.Map;

import RaiderLib.Logging.Logger.LogTable;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.kinematics.SwerveModulePosition;
import edu.wpi.first.math.kinematics.SwerveModuleState;
import edu.wpi.first.util.datalog.DataLog;
import edu.wpi.first.wpilibj.DataLogManager;

public class DiskLogger {
    private final DataLog log = DataLogManager.getLog();

    private final Map<String, Integer> entryIDs = new HashMap<>();

    public void sendUpdates(LogTable logTable) {
        for (Map.Entry<String, LoggedValue> field : logTable.updatesMap().entrySet()) {

            if (!entryIDs.containsKey(field.getKey())) {
                entryIDs.put(field.getKey(), log.start(field.getKey(), field.getValue().getWPILogType()));
            }

            int id = entryIDs.get(field.getKey());

            switch (field.getValue().type) {
                case Raw:
                    log.appendRaw(id, (byte[]) field.getValue().getLogValue(), logTable.timestamp());
                    break;
                case Boolean:
                    log.appendBoolean(id, (Boolean) field.getValue().getLogValue(), logTable.timestamp());
                    break;
                case BooleanArray:
                    log.appendBooleanArray(id, (boolean[]) field.getValue().getLogValue(), logTable.timestamp());
                    break;
                case Integer:
                    log.appendInteger(id, (Integer) field.getValue().getLogValue(), logTable.timestamp());
                    break;
                case IntegerArray:
                    log.appendIntegerArray(id, (long[]) field.getValue().getLogValue(), logTable.timestamp());
                    break;
                case Float:
                    log.appendFloat(id, (float) field.getValue().getLogValue(), logTable.timestamp());
                    break;
                case FloatArray:
                    log.appendFloatArray(id, (float[]) field.getValue().getLogValue(), logTable.timestamp());
                    break;
                case Double:
                    log.appendDouble(id, (double) field.getValue().getLogValue(), logTable.timestamp());
                    break;
                case DoubleArray:
                    log.appendDoubleArray(id, (double[]) field.getValue().getLogValue(), logTable.timestamp());
                    break;
                case String:
                    log.appendString(id, (String) field.getValue().getLogValue(), logTable.timestamp());
                    break;
                case StringArray:
                    log.appendStringArray(id, (String[]) field.getValue().getLogValue(), logTable.timestamp());
                    break;
                /*
                 * 0th Index = Identifier
                 * 0 - Pose2D
                 * 1 - SwerveModulePosition
                 * 2 - SwerveModuleState
                 */
                case Pose2d:
                    Pose2d pose = (Pose2d) field.getValue().getLogValue();
                    Rotation2d rotation = (Rotation2d) field.getValue().getLogValue();
                    double[] poseArray = { 0, pose.getX(), pose.getY(), rotation.getRadians() }; // [x, y, θ]
                    log.appendDoubleArray(id, poseArray, logTable.timestamp());
                    break;
                case SwerveModulePosition:
                    SwerveModulePosition position = (SwerveModulePosition) field.getValue().getLogValue();
                    double[] positionArray = { 1, position.distanceMeters, position.angle.getRadians() }; // [distance,
                                                                                                          // θ]
                    log.appendDoubleArray(id, positionArray, logTable.timestamp());
                    break;
                case SwerveModuleState:
                    SwerveModuleState state = (SwerveModuleState) field.getValue().getLogValue();
                    double[] stateArray = { 2, state.speedMetersPerSecond, state.angle.getRadians() }; // [speed, θ]
                    log.appendDoubleArray(id, stateArray, logTable.timestamp());
                    break;
            }
        }
    }
}
