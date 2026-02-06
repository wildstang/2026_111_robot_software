package org.wildstang.sample.subsystems.targeting;

// ton of imports
import org.wildstang.framework.subsystems.Subsystem;
import org.wildstang.sample.robot.WsSubsystems;
import org.wildstang.sample.subsystems.Turret;
import org.wildstang.sample.subsystems.Turret.GameStates;
import org.wildstang.sample.subsystems.swerve.DriveConstants;
import org.wildstang.sample.subsystems.swerve.SwerveDrive;
import org.wildstang.sample.subsystems.targeting.LimelightHelpers.PoseEstimate;

import java.util.Optional;
import java.util.Vector;

import org.wildstang.framework.core.Core;

import org.wildstang.framework.io.inputs.Input;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Transform2d;
import java.util.Arrays;

import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.geometry.Twist2d;
import edu.wpi.first.math.interpolation.TimeInterpolatableBuffer;
import edu.wpi.first.math.kinematics.SwerveModulePosition;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.networktables.StructPublisher;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class WsPose implements Subsystem {

    private WsAprilTagLL left;
    private WsAprilTagLL right;
    private WsAprilTagLL front;

    private WsAprilTagLL[] cameras; 

    // Object detection camera
    public WsGamePieceLL object = new WsGamePieceLL("limelight-object");

    private final double poseBufferSizeSec = 2;
    public final double visionSpeedThreshold = 3.0;
    
    public int currentID = 0;
    public SwerveDrive swerve;
    private Turret turret;

    // WPI blue relative (m and CCW rad)
    public Pose2d odometryPose = new Pose2d();
    public Pose2d estimatedPose = new Pose2d();

    StructPublisher<Pose2d> odometryPosePublisher = NetworkTableInstance.getDefault().getStructTopic("odometryPose", Pose2d.struct).publish();
    StructPublisher<Pose2d> estimatedPosePublisher = NetworkTableInstance.getDefault().getStructTopic("estimatedPose", Pose2d.struct).publish();
    StructPublisher<Pose2d> coralPosePublisher = NetworkTableInstance.getDefault().getStructTopic("coralPose", Pose2d.struct).publish();


    private final TimeInterpolatableBuffer<Pose2d> poseBuffer = TimeInterpolatableBuffer.createBuffer(poseBufferSizeSec);

    private SwerveModulePosition[] lastWheelPositions = {};
    private Rotation2d lastGyroAngle = new Rotation2d();

    public double angleToHub;

    @Override
    public void inputUpdate(Input source) {
    }

    @Override
    public void initSubsystems() {
        swerve = (SwerveDrive) Core.getSubsystemManager().getSubsystem(WsSubsystems.SWERVE_DRIVE);
        left = new WsAprilTagLL("limelight-left", swerve::getMegaTag2Yaw);
        right = new WsAprilTagLL("limelight-right", swerve::getMegaTag2Yaw);
        front = new WsAprilTagLL("limelight-object", swerve::getMegaTag2Yaw);
        cameras = new WsAprilTagLL[] {left, right, front};
    }

    @Override
    public void init() {
    }

    @Override
    public void selfTest() {
    }

    @Override
    public void update() {
        object.update();
        int bestIndex = -1;
        double bestStdDev = Double.MAX_VALUE;
        PoseEstimate bestEstimate = null;
        for (int i = 0; i < cameras.length; i++) {
            Optional<PoseEstimate> estimate = cameras[i].update();
            if (estimate.isPresent() && getStdDev(estimate) < bestStdDev) {
                bestIndex = i;
                bestEstimate = estimate.get();
                bestStdDev = getStdDev(estimate);
            }   
        }

        // If we found a valid estimate
        if (bestEstimate != null) {
            currentID = cameras[bestIndex].tid;
            addVisionObservation(bestEstimate, 1/bestStdDev);
        }

        if (getCoralPose().isPresent()) {
            coralPosePublisher.set(new Pose2d(getCoralPose().get(), new Rotation2d()));
        }

        odometryPosePublisher.set(odometryPose);
        estimatedPosePublisher.set(estimatedPose);


        //shoot on da move
        double dix = VisionConsts.CENTER_OF_HUB[0] - estimatedPose.getX();
        double diy = VisionConsts.CENTER_OF_HUB[1] - estimatedPose.getY();
        double dih = Math.hypot(dix, diy);
        double robotVelx = swerve.getSpeeds().vxMetersPerSecond;
        double robotVely = swerve.getSpeeds().vyMetersPerSecond;
        
        double dnewx = dix + robotVelx * ShotData.getTOF(dih);
        double dnewy = diy + robotVely * ShotData.getTOF(dih);
        double dnew = Math.hypot(dnewx, dnewy);
        double percentdiff = (dnew - dih)/dih;
        dih = dnew;
        double count = 0;
        double dnewTof = ShotData.getTOF(dnew);
        while((percentdiff >= 0.02) || (count <= 6  )){
            count++;
            dnewx = dix + robotVelx * dnewTof;
            dnewy = diy + robotVely * dnewTof;
            dnew = Math.hypot(dnewx, dnewy);
            percentdiff = (dnew - dih)/dih;
            dih = dnew;
            dnewTof = ShotData.getTOF(dnew);
        }
        angleToHub = Math.atan(dnewy/dnewx);
        
    }

    public double getStdDev(Optional<PoseEstimate> estimate) {
        return estimate.isPresent() && estimate.get().rawFiducials.length > 0 ? 
            Math.pow(Arrays.stream(estimate.get().rawFiducials).mapToDouble(fiducial -> fiducial.distToCamera).min().getAsDouble(),2) / estimate.get().tagCount 
            : Double.MAX_VALUE;
    }

    @Override
    public void resetState() {
        left.update();
        right.update();
    }

    /**
     * Reset estimated pose and odometry pose to pose
     * Clear pose buffer
    */
    public void resetPose(Pose2d initialPose) {
        estimatedPose = initialPose;
        odometryPose = initialPose;
        poseBuffer.clear();
    }

    public void addOdometryObservation(Pose2d newPose) {
        
        // Add pose to buffer at timestamp
        poseBuffer.addSample(Timer.getTimestamp(), newPose);

        estimatedPose = newPose;
    }

    private void addVisionObservation(PoseEstimate observation, double weight) {

        SmartDashboard.putNumber("auto weight", weight);
        Optional<Pose2d> sample = poseBuffer.getSample(observation.timestampSeconds);
        if (sample.isEmpty()) {
            // exit if not there
            return;
        }

        // sample --> odometryPose transform and backwards of that
        var sampleToOdometryTransform = new Transform2d(sample.get(), odometryPose);
        var odometryToSampleTransform = new Transform2d(odometryPose, sample.get());

        // get old estimate by applying odometryToSample Transform
        Pose2d estimateAtTime = estimatedPose.plus(odometryToSampleTransform);

        // difference between estimate and vision pose
        Transform2d transform = new Transform2d(estimateAtTime, observation.pose);
        //transform = transform.times(Math.max(1, weight));

        // Recalculate current estimate by applying scaled transform to old estimate
        // then replaying odometry data
        estimatedPose = estimateAtTime.plus(transform).plus(sampleToOdometryTransform);
    }

    // YEAR SUBSYSTEM ACCESS METHODS

    /**
     * Can the object detection camera see a coral
     * @return true if a coral is present
     */
    public boolean coralInView(){
        return object.targetInView();
    }
    /**
     * Coral pose
     * 
     * @return field relative pose of the coral if the coral is present
     */
    public Optional<Translation2d> getCoralPose() {
        if (!object.targetInView()) return Optional.empty();

        Optional<Pose2d> sample = poseBuffer.getSample(object.timestamp);
        if (sample.isEmpty()) {
            // exit if not there
            return Optional.empty();
        }

        // current odometryPose --> sample transformation
        var odometryToSampleTransform = new Transform2d(odometryPose, sample.get());

        // get old estimate at timestamp by applying odometryToSample Transform
        Pose2d estimateAtTime = estimatedPose.plus(odometryToSampleTransform);

        // Assumes the angle of depression is gonna be negative
        double camToCoralDist = VisionConsts.camTransform.getZ() / -Math.tan(VisionConsts.camTransform.getRotation().getY() + Math.toRadians(object.ty));

        // Transform from camera to coral
        Transform2d camToCoralTransform = new Transform2d(Math.cos(Math.toRadians(-object.tx)) * camToCoralDist, Math.sin(Math.toRadians(-object.tx)) * camToCoralDist, Rotation2d.fromDegrees(-object.tx));

        Transform2d camTransform2d = new Transform2d(VisionConsts.camTransform.getX(), VisionConsts.camTransform.getY(), new Rotation2d(VisionConsts.camTransform.getRotation().getZ()));
        // Combines transformations to get coral pose in field coordinates
        return Optional.of(estimateAtTime.plus(camTransform2d).plus(camToCoralTransform).getTranslation());
    }

    // Set the front limlelight (same camera referenced by both front and object) to object or april tag pipeline
    public void setPipelineObject(boolean isObject) {
        if (isObject) {
            object.setPipeline(0);
        } else {
            front.setPipeline(1);
        }
    }

    public double turnToTarget(Translation2d target) {
        double offsetX = target.getX() - estimatedPose.getX();
        double offsetY = target.getY() - estimatedPose.getY();
        return (Math.toDegrees(Math.atan2(offsetY, offsetX)));
    }

    @Override
    public String getName() {
        return "Ws Pose";
    }

    public double getFlywheelFeedVelocity(){
        return 0;
    }
    public double getFlywheelShootVelocity(){
        return 0;
    }
    public double getHoodFeedPosition(){
        return 0;
    }
    public double getHoodShootPosition(){
        return 0;
    }
    
    // Returns double array with turret angle wanted and also the zone we are in: firing game state for alliance zone and homing for neutral
    //rather than an object[], we can probably return a GameState enum (from turret) in one method,
    //and then a second method that takes in a GameState and returns the double of the angle
    public double angleOfTurret(){
       
        double desiredTurretAngle = 0;
        Pose2d robotPose = estimatedPose;
        double hubXDistance = Math.abs(VisionConsts.CENTER_OF_HUB[0] - estimatedPose.getX());
        double hubYDistance = Math.abs(VisionConsts.CENTER_OF_HUB[1] - estimatedPose.getX());

        if(robotPose.getX() < VisionConsts.ALLIANCE_ZONE){
            // in our alliance zone
            desiredTurretAngle = Math.tanh((hubYDistance)/hubXDistance);
            turret.turretState = Turret.GameStates.FIRING;
            
        }else if(robotPose.getX() > VisionConsts.ALLIANCE_ZONE){
            // in the neutral zone
            if(robotPose.getY() > VisionConsts.halfFieldY){
                double feedZoneXDistance = Math.abs(VisionConsts.highFeedPos[0] - estimatedPose.getX());
                double feedZoneYDistance = Math.abs(VisionConsts.highFeedPos[1] - estimatedPose.getY());

                desiredTurretAngle = Math.tanh(feedZoneYDistance/feedZoneXDistance);
                turret.turretState = Turret.GameStates.HOMING; 
            }else if(robotPose.getY() < VisionConsts.halfFieldY){
                double feedZoneXDistance = Math.abs(VisionConsts.lowFeedPos[0] - estimatedPose.getX());
                double feedZoneYDistance = Math.abs(VisionConsts.lowFeedPos[1] - estimatedPose.getY());

                desiredTurretAngle = Math.tanh(feedZoneYDistance/feedZoneXDistance);
                turret.turretState = Turret.GameStates.HOMING; 

            }
                
                //we'll also need another method for below (maybe in the turret subsystem)
                //to get from field-centric angle to robot-centric angle.
                //we'll need to pull in the gyro value from swerveDrive to do that
                
            
        }
        desiredTurretAngle = (desiredTurretAngle + 360) % 360;
        return desiredTurretAngle;
        
            
    }

    public double fromFieldToRobotAngle(){
        return angleOfTurret() - swerve.getGyroAngle() - VisionConsts.turretOffset;
    }

      public double distanceToTarget(Translation2d target) {
        double offsetX = target.getX() - estimatedPose.getX();
        double offsetY = target.getY() - estimatedPose.getY();
        return (Math.sqrt(offsetX*offsetX + offsetY*offsetY));
    }

    public boolean goodToFire(){
        GameStates state = turret.turretState;
        if(state.equals(GameStates.FIRING)){
            //in alliance zone
            if(estimatedPose.getX() <= 158.6 && estimatedPose.getX() >= 118.6 && estimatedPose.getY() <= 170 && estimatedPose.getY() >= 130){
                //right up against the hub
                return false;
            }
            if(estimatedPose.getX() < 46 && estimatedPose.getY() >= 120 && estimatedPose.getY() <= 160){
                //behind climbing area
                return false;
            }

            }
        if(state.equals(GameStates.HOMING)){
             if(estimatedPose.getY() <= 170 && estimatedPose.getY() >= 130){
                //behind the hub
                return false;
            }
                
        }
        return true;
    }
}