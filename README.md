# RaiderLib

RaiderLib is a collection of useful subsystems, classes, and utilities to streamline robot development in FRC. Main features include out-of-the-box swerve, logging, Smart Dashboard utilities, unified motor support across both REV and Phoenix, and various other subsystems.

# Documentation

Dashboard
----
AutoTab - Uses inputs from SmartDashboard to generate an autonomous path.
TuningTab - Uses inputs from SmartDashboard to tune PIDs.

Logging
----
(Incomplete)

# Drivers

Digital Inputs
----
Due to be deleted

IMUs
----
Wrapper around either pigeon or NavX gyros
Uses an IMUFactory and IMUType
There are two IMUTypes
```java
IMUType.PIGEON2
IMUType.NAVX
```
Then, in order to instantiate the the IMU class use the factory:
```java
IMUFactory.createIMU(IMUType.PIGEON2, 2); // note that Pigeon REQUIRES CANID
IMUFactory.createIMU(IMUType.NAVX); // there is no CANID for NavX
```
Take a look at the [source code](https://github.com/RoboRaidersTeam75/RaiderLib/blob/main/src/main/java/RaiderLib/Drivers/IMUs/IMU.java) to see what methods the IMUs have

Lights
----

(Incomplete, unimplemented)

Motors
----
Supports various motor types across different providers.
Uses a **MotorFactory** in order to create each type of motor
```java
MotorFactory.createMotor(MotorType.FALCON500, motorConfig);
```
There are 4 supported types of motors. All of them contain encoders within them so you do not need to instantiate a separate encoder class
```java
MotorType.FALCON500
MotorType.KRAKENX60
MotorType.CANSPARKMAX
MotorType.CANSPARKFLEX
```
Each motor needs to be configured via the **MotorConfiguration** class 
There are many options, but you can see all of them in the source [code](https://github.com/RoboRaidersTeam75/RaiderLib/blob/main/src/main/java/RaiderLib/Config/MotorConfiguration.java)

Usage:
```java
MotorConfiguration myConfig;
myConfig.CANID = 2;
myConfig.PIDConfigs.slot0configs.kP = .5;
```
This **MotorConfiguration** can then be passed into the createMotor factory.

Each **Motor** has various useful methods which can be seen in the [source code](https://github.com/RoboRaidersTeam75/RaiderLib/blob/main/src/main/java/RaiderLib/Drivers/Motors/Motor.java)


Vision
----
(Incomplete)

# Subsystems
Drivetrains
----
Contains drivetrain frameworks for both Tank and Swerve Drive.

### Swerve Drive

Swerve Drive requires four inputs:
- SwerveConstants
- angle MotorConstants
- drive MotorConstants
- an IMU

```java
public SwerveDrive(
  SwerveConstants constants,
  MotorType type,
  MotorConfiguration driveConfig,
  MotorConfiguration angleConfig,
  IMU imu) {
```

This assumes that all motors (angle and drive) both use the same motor type
For SwerveConstants, you just need to create an instance of [this class](https://github.com/RoboRaidersTeam75/RaiderLib/blob/main/src/main/java/RaiderLib/Util/SwerveConstants.java) and set the values to whatever fits your robot.

The class acts as a drop-in subsystem for your code.
There are only two methods that need to be used.
```java
m_Swerve.zeroGyro(); // resets gyro
m_Swerve.drive(new Translation2d(10,10), new Rotation2d(18), false); // the last parameter is if to use open loop or not
```

ToDo: implement open loop / closed loop

### Tank Drive
Out-of-the-box solution for Tank Drive subsystem.

Tank Drive requires two inputs:
- Left/Right Master and Slave Motors
- An IMU

Parameters:
```java
  MotorType MotorType,
  MotorConfiguration rightMotorConfig,
  MotorConfiguration leftMotorConfig,
  IMU imu,
  DifferentialDriveKinematics kinematics,
  double autoMaxSpeed,
  double teleOpMaxSpeed,
  double wheelDiameter
```

Usage:
```java
m_Tank.zeroGyro() // resets gyro
m_Tank.drive(0, new Rotation2d(), false, false) // second-to-last parameter is whether or not to use square inputs, last parameter is if the competition is in autonomous period
```

Lights
----
(Incomplete)

Shooters
----
(Incomplete, unimplemented)

# Miscellaneous

Utilities
----
Conversions - Contains methods to convert between units.
ModuleState - Contains optimizations for Swerve Module rotation.
SwerveConstants - Contains all constants for Swerve Drive.

Config
----
Contains the necessary configuration information for Motors and PID controllers.
