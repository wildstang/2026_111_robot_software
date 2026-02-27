package org.wildstang.sample.subsystems.targeting;

// ton of imports
import org.wildstang.framework.subsystems.Subsystem;
import org.wildstang.sample.robot.WsSubsystems;
import org.wildstang.sample.subsystems.Launcher;
import org.wildstang.sample.subsystems.Turret;
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

    private final double inToM = 1.0/39.37;

    private WsAprilTagLL left;
    private WsAprilTagLL right;
    // private WsAprilTagLL front;

    private WsAprilTagLL[] cameras; 

    // Object detection camera
    // public WsGamePieceLL object = new WsGamePieceLL("limelight-object");

    private final double poseBufferSizeSec = 2;
    public final double visionSpeedThreshold = 3.0;
    
    public int currentID = 0;
    public SwerveDrive swerve;
    private Turret turret;
    private Launcher launcher;

    // WPI blue relative (m and CCW rad)
    public Pose2d estimatedPose = new Pose2d();
    private Translation2d firingTarget = VisionConsts.CENTER_OF_HUB;

    StructPublisher<Pose2d> estimatedPosePublisher = NetworkTableInstance.getDefault().getStructTopic("estimatedPose", Pose2d.struct).publish();
    StructPublisher<Pose2d> firingPosePublisher = NetworkTableInstance.getDefault().getStructTopic("firing pose", Pose2d.struct).publish();

    private final TimeInterpolatableBuffer<Pose2d> poseBuffer = TimeInterpolatableBuffer.createBuffer(poseBufferSizeSec);

    @Override
    public void inputUpdate(Input source) {
    }

    @Override
    public void initSubsystems() {
        swerve = (SwerveDrive) Core.getSubsystemManager().getSubsystem(WsSubsystems.SWERVE_DRIVE);
        turret = (Turret) Core.getSubsystemManager().getSubsystem(WsSubsystems.TURRET);
        launcher = (Launcher) Core.getSubsystemManager().getSubsystem(WsSubsystems.LAUNCHER);
        left = new WsAprilTagLL("limelight-left", swerve::getMegaTag2Yaw);
        right = new WsAprilTagLL("limelight-right", swerve::getMegaTag2Yaw);
        cameras = new WsAprilTagLL[] {left, right};
    }

    @Override
    public void init() {
    }

    @Override
    public void selfTest() {
    }

    @Override
    public void update() {
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

        estimatedPosePublisher.set(estimatedPose);

        if (swerve.speedMagnitude() > 0.1){
            if (inAllianceZone()){
                firingTarget = shootOnTheMove(VisionConsts.CENTER_OF_HUB.getX() - estimatedPose.getX(), 
                    VisionConsts.CENTER_OF_HUB.getY() - estimatedPose.getY());
            } else {
                firingTarget = isFeedingLeft() ? 
                    shootOnTheMove(VisionConsts.highFeedPos.getX() - estimatedPose.getX(),
                        VisionConsts.highFeedPos.getY() - estimatedPose.getY()) : 
                    shootOnTheMove(VisionConsts.lowFeedPos.getX() - estimatedPose.getX(), 
                        VisionConsts.lowFeedPos.getY() - estimatedPose.getY());
            }
        } else {
            if (inAllianceZone()) {
                firingTarget = VisionConsts.CENTER_OF_HUB;
            } else {
                firingTarget = isFeedingLeft() ? VisionConsts.highFeedPos : VisionConsts.lowFeedPos;
            }
        }
        firingPosePublisher.set(new Pose2d(firingTarget, new Rotation2d()));
        SmartDashboard.putNumber("Pose firing X", firingTarget.getX());
        SmartDashboard.putNumber("Pose firing Y", firingTarget.getY());
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
        poseBuffer.clear();
    }

    public void addOdometryObservation(Pose2d newPose) {
        
        // Add pose to buffer at timestamp
        poseBuffer.addSample(Timer.getTimestamp(), newPose);
        estimatedPose = newPose;
    }

    private void addVisionObservation(PoseEstimate observation, double weight) {

        // SmartDashboard.putNumber("auto weight", weight);
        Optional<Pose2d> sample = poseBuffer.getSample(observation.timestampSeconds);
        if (sample.isEmpty()) {
            // exit if not there
            return;
        }

        // sample --> odometryPose transform and backwards of that
        //var sampleToOdometryTransform = new Transform2d(sample.get(), odometryPose);
        //var odometryToSampleTransform = new Transform2d(odometryPose, sample.get());

        // get old estimate by applying odometryToSample Transform
        //Pose2d estimateAtTime = estimatedPose.plus(odometryToSampleTransform);

        // difference between estimate and vision pose
        //Transform2d transform = new Transform2d(estimateAtTime, observation.pose);
        //transform = transform.times(Math.max(1, weight));

        // Recalculate current estimate by applying scaled transform to old estimate
        // then replaying odometry data
        // estimatedPose = estimateAtTime.plus(transform).plus(sampleToOdometryTransform);
        //estimatedPose = observation.pose; 
        swerve.resetTranslation(observation.pose.getX(), observation.pose.getY());
    }

    // YEAR SUBSYSTEM ACCESS METHODS

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
        return ShotData.getFeedPower(distanceToTarget(firingTarget));
    }
    public double getFlywheelShootVelocity(){
        return ShotData.getFlywheelPower(distanceToTarget(firingTarget));
    }
    public double getHoodShootPosition(){
        return ShotData.getHoodAngle(distanceToTarget(firingTarget));
    }
    
    // Returns double array with turret angle wanted and also the zone we are in: firing game state for alliance zone and homing for neutral
    //rather than an object[], we can probably return a GameState enum (from turret) in one method,
    //and then a second method that takes in a GameState and returns the double of the angle
    public double angleOfTurret(){
        return Math.toDegrees(Math.atan2(firingTarget.getY()-estimatedPose.getY(), firingTarget.getX() - estimatedPose.getX()));
    }

    public double fromFieldToRobotAngle(double fieldAngle){
        return (fieldAngle - swerve.getGyroAngle() + VisionConsts.turretOffset+1080)%360;
    }

    public Translation2d shootOnTheMove(double dix, double diy){
        //shoot on da move
        double dih = Math.hypot(dix, diy);
        double robotx = swerve.getSpeeds().vxMetersPerSecond;
        double roboty = swerve.getSpeeds().vyMetersPerSecond;

        double robotVelx = robotx*Math.cos(Math.toRadians(swerve.getGyroAngle()))
            + roboty*Math.cos(Math.toRadians(90 + swerve.getGyroAngle()));
        double robotVely = robotx*Math.sin(Math.toRadians(swerve.getGyroAngle()))
            + roboty*Math.sin(Math.toRadians(90 + swerve.getGyroAngle()));
        
        double dnewx = dix - robotVelx * ShotData.getTOF(dih);
        double dnewy = diy - robotVely * ShotData.getTOF(dih);
        double dnew = Math.hypot(dnewx, dnewy);
        double percentdiff = (dnew - dih)/dih;
        dih = dnew;
        double count = 0;
        double dnewTof = ShotData.getTOF(dnew);
        while((percentdiff >= 0.02) || (count <= 6  )){
            count++;
            dnewx = dix - robotVelx * dnewTof;
            dnewy = diy - robotVely * dnewTof;
            dnew = Math.hypot(dnewx, dnewy);
            percentdiff = (dnew - dih)/dih;
            dih = dnew;
            dnewTof = ShotData.getTOF(dnew);
        }
        return new Translation2d(dnewx+estimatedPose.getX(), dnewy+estimatedPose.getY());
    }

    public double distanceToTarget(Translation2d target) {
        double offsetX = target.getX() - estimatedPose.getX();
        double offsetY = target.getY() - estimatedPose.getY();
        return (Math.sqrt(offsetX*offsetX + offsetY*offsetY));
    }

    public boolean goodToFire(){
        if(inAllianceZone()){
            //in alliance zone
            // if(estimatedPose.getX() <= 158.6 && estimatedPose.getX() >= 118.6 && estimatedPose.getY() <= 170 && estimatedPose.getY() >= 130){
            //     //right up against the hub
            //     return false;
            // }
            // if(estimatedPose.getX() < 46*inToM && estimatedPose.getY() >= 120*inToM && estimatedPose.getY() <= 160*inToM){
            //     //behind climbing area
            //     return false;
            // }

        } else {
             if(estimatedPose.getY() <= 185*inToM && estimatedPose.getY() >= 130*inToM){
                //behind the hub
                return false;
            }                
        }
        return true;
    }
    public boolean inAllianceZone(){
        return estimatedPose.getX() < VisionConsts.ALLIANCE_ZONE;
    }
    public boolean canFeedLeft(){
        return !inAllianceZone() && estimatedPose.getY() > 170*inToM;
    }
    public boolean canFeedRight(){
        return !inAllianceZone() && estimatedPose.getY() < 130*inToM;
    }
    public boolean isFeedingLeft(){
        return estimatedPose.getY() > VisionConsts.halfFieldY;
    }
}