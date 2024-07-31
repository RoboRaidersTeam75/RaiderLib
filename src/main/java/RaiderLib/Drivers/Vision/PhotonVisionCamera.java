// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package RaiderLib.Drivers.Vision;

import edu.wpi.first.apriltag.AprilTagFieldLayout;
import edu.wpi.first.apriltag.AprilTagFields;
import edu.wpi.first.math.geometry.Rotation3d;
import edu.wpi.first.math.geometry.Transform3d;
import edu.wpi.first.math.geometry.Translation3d;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.smartdashboard.Field2d;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.OptionalDouble;
import org.photonvision.EstimatedRobotPose;
import org.photonvision.PhotonCamera;
import org.photonvision.PhotonPoseEstimator;
import org.photonvision.PhotonPoseEstimator.PoseStrategy;
import org.photonvision.targeting.PhotonPipelineResult;
import org.photonvision.targeting.PhotonTrackedTarget;

public class PhotonVisionCamera extends SubsystemBase {
  private PhotonCamera m_camera;
  private PhotonPipelineResult m_result;
  private AprilTagFieldLayout m_tagLayout = AprilTagFields.k2024Crescendo.loadAprilTagLayoutField();
  private PhotonPoseEstimator m_poseEstimator;
  private final Field2d m_field = new Field2d();

  public PhotonVisionCamera(String name) {
    // m_camera = new PhotonCamera(name);
    m_camera = new PhotonCamera(NetworkTableInstance.getDefault(), name);
    SmartDashboard.putData("Field", m_field);

    m_poseEstimator =
        new PhotonPoseEstimator(
            m_tagLayout,
            PoseStrategy.MULTI_TAG_PNP_ON_COPROCESSOR,
            m_camera,
            new Transform3d(
                new Translation3d(0.254, -0.254, .241), new Rotation3d(-40 * Math.PI / 180, 0, 0)));
  }

  public boolean hasTarget() {
    List<PhotonTrackedTarget> targets = m_result.getTargets();
    return (targets.size() > 0);
  }

  public int getTagID() {
    List<PhotonTrackedTarget> targets = m_result.getTargets();
    return targets.get(0).getFiducialId();
  }

  public List<Integer> getAllTagIds() {
    List<PhotonTrackedTarget> targets = m_result.getTargets();
    ArrayList<Integer> ids = new ArrayList<Integer>();

    for (PhotonTrackedTarget target : targets) {
      ids.add(target.getFiducialId());
    }
    return ids;
  }

  public double getAprilTagHeight(int id) {
    return m_tagLayout.getTagPose(id).get().getY();
  }

  public PhotonTrackedTarget getTarget(int id) {
    List<PhotonTrackedTarget> targets = m_result.getTargets();
    for (PhotonTrackedTarget target : targets) {
      if (target.getFiducialId() == id) {
        return target;
      }
    }
    return null;
  }

  // public OptionalDouble getRange(int id) {
  //   PhotonTrackedTarget target = getTarget(id);
  //   if (target == null) {
  //     return OptionalDouble.empty();
  //   }
  //   double targetHeight = getAprilTagHeight(id);
  //   return OptionalDouble.of(
  //       PhotonUtils.calculateDistanceToTargetMeters(
  //           kCameraHeight,
  //           targetHeight,
  //           kCameraPitch,
  //           Units.degreesToRadians(target.getPitch())));
  // }

  public OptionalDouble getX(int id) {
    PhotonTrackedTarget target = getTarget(id);
    if (target == null) {
      return OptionalDouble.empty();
    }
    return OptionalDouble.of(target.getYaw());
  }

  public OptionalDouble getY(int id) {
    PhotonTrackedTarget target = getTarget(id);
    if (target == null) {
      return OptionalDouble.empty();
    }
    return OptionalDouble.of(target.getPitch());
  }

  public EstimatedRobotPose getEstimatedPose() {
    Optional<EstimatedRobotPose> pose = m_poseEstimator.update();
    if (!pose.isEmpty()) {
      return pose.get();
    }
    return null;
  }

  public double getTimestamp() {
    return m_result.getTimestampSeconds();
  }

  @Override
  public void periodic() {
    m_result = m_camera.getLatestResult();
    System.out.println(m_result);
    if (hasTarget()) {
      EstimatedRobotPose pose = getEstimatedPose();
      if (!(pose == null)) {
        m_field.setRobotPose(getEstimatedPose().estimatedPose.toPose2d());
      }
    }
  }
}
