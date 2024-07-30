package RaiderLib.Subsystems.Drivetrains;

import RaiderLib.Config.MotorConfiguration;
import RaiderLib.Drivers.IMUs.IMU;
import RaiderLib.Drivers.Motors.Motor.MotorType;
import RaiderLib.Util.SwerveConstants;
import edu.wpi.first.math.estimator.SwerveDrivePoseEstimator;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.math.kinematics.SwerveDriveKinematics;
import edu.wpi.first.math.kinematics.SwerveModulePosition;
import edu.wpi.first.math.kinematics.SwerveModuleState;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class SwerveDrive extends SubsystemBase {
    private SwerveDrivePoseEstimator swerveOdometry;
    private SwerveModule[] m_modules;
    private SwerveConstants m_constants;

    private IMU m_imu;

    public SwerveDrive(SwerveConstants constants,
                       MotorType type,
                       MotorConfiguration driveConfig, 
                       MotorConfiguration angleConfig,
                       IMU imu) {

        SwerveModule[] modules = new SwerveModule[4];
        for (int i = 0; i < 4; i++) {
            modules[i] = new SwerveModule(i, constants, type, driveConfig, angleConfig);
        }

        m_imu = imu;
        m_modules = modules;
        m_constants = constants;
        
        swerveOdometry = new SwerveDrivePoseEstimator(
            constants.kinematics, 
            m_imu.getRotation2d(), 
            getModulePositions(), 
            new Pose2d(0, 0, new Rotation2d(0)));
    }

    public SwerveModulePosition[] getModulePositions() {
        SwerveModulePosition[] positions = new SwerveModulePosition[4];
        for (int i = 0; i < 4; i++) {
            positions[i] = m_modules[i].getPosition();
        }
        return positions;
    }

    public void drive(Translation2d translation,
                      double rotation,
                      boolean isOpenLoop) {
        SwerveModuleState[] swerveModuleStates =
            m_constants.kinematics.toSwerveModuleStates(
            ChassisSpeeds.fromFieldRelativeSpeeds(
                -translation.getX(), -translation.getY(), rotation, m_imu.getRotation2d()));

        SwerveDriveKinematics.desaturateWheelSpeeds(
            swerveModuleStates, m_constants.maxSpeed);

        for (SwerveModule mod : m_modules) {
            mod.setDesiredState(swerveModuleStates[mod.m_moduleNumber]);
        }
    }

    public void zeroGyro() {
        m_imu.setAngle(new Rotation2d(0));
    }

    public void periodic() {
        swerveOdometry.update(m_imu.getRotation2d(), getModulePositions());
    }
}
