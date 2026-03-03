package org.wildstang.sample.subsystems.targeting;

// ton of imports
import org.wildstang.framework.subsystems.Subsystem;
import org.wildstang.hardware.roborio.inputs.WsJoystickButton;
import org.wildstang.sample.robot.WsInputs;
import org.wildstang.sample.robot.WsSubsystems;
import org.wildstang.sample.subsystems.Launcher;
import org.wildstang.sample.subsystems.Turret;
import org.wildstang.sample.subsystems.LED.LedController;
import org.wildstang.sample.subsystems.swerve.DriveConstants;
import org.wildstang.sample.subsystems.swerve.SwerveDrive;
import org.wildstang.sample.subsystems.targeting.LimelightHelpers.PoseEstimate;
import org.wildstang.sample.subsystems.targeting.LimelightHelpers.RawFiducial;

import java.util.Optional;
import java.util.Vector;

import org.wildstang.framework.core.Core;
import org.wildstang.framework.io.inputs.DigitalInput;
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

    private WsJoystickButton leftBumper, operatorLeftBumper, operatorRightBumper;//drive over bump

    private final double inToM = 1.0/39.37;

    private WsAprilTagLL left;
    private WsAprilTagLL right;
    // private WsAprilTagLL front;

    private double distanceDriven = 0;
    private double rotationSpeed = 0;
    private boolean feedingSides = true;

    private WsAprilTagLL[] cameras;


    // Object detection camera
    // public WsGamePieceLL object = new WsGamePieceLL("limelight-object");

    private final double poseBufferSizeSec = 2;
    public final double visionSpeedThreshold = 3.0;
    
    public int currentID = 0;
    public SwerveDrive swerve;
    private Turret turret;
    private Launcher launcher;
    private LedController led;

    // WPI blue relative (m and CCW rad)
    public Pose2d estimatedPose = new Pose2d();

    private Translation2d firingTarget = VisionConsts.CENTER_OF_HUB;

    StructPublisher<Pose2d> estimatedPosePublisher = NetworkTableInstance.getDefault().getStructTopic("estimatedPose", Pose2d.struct).publish();
    StructPublisher<Pose2d> bestEstimatePosePublisher = NetworkTableInstance.getDefault().getStructTopic("bestEstimatePose", Pose2d.struct).publish();
    StructPublisher<Pose2d> firingPosePublisher = NetworkTableInstance.getDefault().getStructTopic("firing pose", Pose2d.struct).publish();

    private final TimeInterpolatableBuffer<Pose2d> poseBuffer = TimeInterpolatableBuffer.createBuffer(poseBufferSizeSec);
    private double oldOdometryUpdateTime = 0.0;

    @Override
    public void inputUpdate(Input source) {
        if(leftBumper.getValue()){
            driverOverBump();
        }
        if (operatorLeftBumper.getValue()) feedingSides = true;
        if (operatorRightBumper.getValue()) feedingSides = false;
    }

    @Override
    public void initSubsystems() {
        swerve = (SwerveDrive) Core.getSubsystemManager().getSubsystem(WsSubsystems.SWERVE_DRIVE);
        turret = (Turret) Core.getSubsystemManager().getSubsystem(WsSubsystems.TURRET);
        launcher = (Launcher) Core.getSubsystemManager().getSubsystem(WsSubsystems.LAUNCHER);
        led = (LedController) Core.getSubsystemManager().getSubsystem(WsSubsystems.LED);
        left = new WsAprilTagLL("limelight-left", swerve::getMegaTag2Yaw);
        right = new WsAprilTagLL("limelight-right", swerve::getMegaTag2Yaw);
        cameras = new WsAprilTagLL[] {left, right};
    }

    @Override
    public void init() {
        leftBumper = (WsJoystickButton) WsInputs.DRIVER_LEFT_SHOULDER.get();
        leftBumper.addInputListener(this);
        operatorLeftBumper = (WsJoystickButton) WsInputs.OPERATOR_LEFT_SHOULDER.get();
        operatorLeftBumper.addInputListener(this);
        operatorRightBumper = (WsJoystickButton) WsInputs.OPERATOR_RIGHT_SHOULDER.get();
        operatorRightBumper.addInputListener(this);
    }

    @Override
    public void selfTest() {
    }

    @Override
    public void update() {
        double odFOM = odometryFOM();
        SmartDashboard.putNumber("Odometry FOM", odFOM);
        double bestStdDev = Double.MAX_VALUE;
        PoseEstimate bestEstimate = null;

        WsAprilTagLL bestCamera = getBestCamera();

        rotationSpeed = Math.abs(swerve.speeds().omegaRadiansPerSecond);
        SmartDashboard.putNumber("Rotation Speed", rotationSpeed);
        
        if(bestCamera == null){
            SmartDashboard.putString("Vision/BestCamera", "none");
            estimatedPosePublisher.set(estimatedPose);
        } else {
            //SmartDashboard.putString("Vision/BestCamera", bestCamera.CameraID);

            bestEstimate = bestCamera.update().orElse(null);
        }
            
            

        if(bestEstimate == null){
            //SmartDashboard.putString("Vision/BestCamera", bestCamera.CameraID);
            SmartDashboard.putString("Has Reset", "False");
        }else{
            bestStdDev = getStdDev(Optional.of(bestEstimate));
            double camFOM = cameraFOM(bestCamera);
           

            SmartDashboard.putNumber("Camera FOM", camFOM);
            SmartDashboard.putNumber("Best StdDev", bestStdDev);

            if (camFOM <= odFOM) {
                addVisionObservation(bestEstimate, 1/bestStdDev);

                distanceDriven = camFOM;
                SmartDashboard.putString("Has Reset", "True");
            } else if (camFOM <= odFOM+0.2){
                addVisionObservation(bestEstimate, 1/bestStdDev);
                SmartDashboard.putString("HasReset", "True");
            } else if(camFOM > odFOM){
          
                SmartDashboard.putString("Has Reset", "False");
                
            }
        }

        estimatedPosePublisher.set(estimatedPose);
         if(bestEstimate != null){
            bestEstimatePosePublisher.set(bestEstimate.pose);
            SmartDashboard.putNumber("Tid", bestCamera.tid);
        }
        
        if (swerve.speedMagnitude() > 0.1){
            if (inAllianceZone()){
                firingTarget = shootOnTheMove(VisionConsts.CENTER_OF_HUB.getX() - estimatedPose.getX(), 
                    VisionConsts.CENTER_OF_HUB.getY() - estimatedPose.getY());
            } else {
                if (feedingSides){
                    firingTarget = isFeedingLeft() ? 
                        shootOnTheMove(VisionConsts.highFeedPos.getX() - estimatedPose.getX(),
                            VisionConsts.highFeedPos.getY() - estimatedPose.getY()) : 
                        shootOnTheMove(VisionConsts.lowFeedPos.getX() - estimatedPose.getX(), 
                            VisionConsts.lowFeedPos.getY() - estimatedPose.getY());
                } else {
                    firingTarget = isFeedingLeft() ?
                        shootOnTheMove(VisionConsts.highCenterFeed.getX() - estimatedPose.getX(),
                            VisionConsts.highCenterFeed.getY() - estimatedPose.getY()) : 
                        shootOnTheMove(VisionConsts.lowCenterFeed.getX() - estimatedPose.getX(), 
                            VisionConsts.lowCenterFeed.getY() - estimatedPose.getY());
                }
            }
        } else {
            if (inAllianceZone()) {
                firingTarget = VisionConsts.CENTER_OF_HUB;
            } else {
                if (feedingSides) firingTarget = isFeedingLeft() ? VisionConsts.highFeedPos : VisionConsts.lowFeedPos;
                else firingTarget = isFeedingLeft() ? VisionConsts.highCenterFeed : VisionConsts.lowCenterFeed;
            }
        }
        firingPosePublisher.set(new Pose2d(firingTarget, new Rotation2d()));
        SmartDashboard.putNumber("Pose firing X", firingTarget.getX());
        SmartDashboard.putNumber("Pose firing Y", firingTarget.getY());
        SmartDashboard.putBoolean("Pose feeding sides", feedingSides);
         SmartDashboard.putNumber("Best Standard Deviation ", bestStdDev);
       
    }

    private double cameraFOM(WsAprilTagLL bestCamera){
        double robotSpeed = swerve.speedMagnitude();
        //int numberOfTagsSeen = bestCamera.getNumberOfTags();
        SmartDashboard.putNumber("Robot Speed", robotSpeed);
        SmartDashboard.putNumber("Rotation Speed", rotationSpeed);
        return (robotSpeed * FOMConstants.CAM_CNSTANT*2) + (3*rotationSpeed);
    }

     
    private double odometryFOM(){

        double robotSpeed = swerve.speedMagnitude();
        
        double newTime = Timer.getFPGATimestamp();
        double deltaT = newTime - oldOdometryUpdateTime;
        oldOdometryUpdateTime = newTime;

        distanceDriven += FOMConstants.ODOMETRY_DISPLACEMENT*(Math.abs(robotSpeed)*deltaT);
        SmartDashboard.putNumber("Distance Driven", distanceDriven);


        return distanceDriven;
    }

    public void driverOverBump(){
        distanceDriven = 3;
    }

    private WsAprilTagLL getBestCamera(){

        if(LimelightHelpers.getTargetCount(left.CameraID) == 0 && LimelightHelpers.getTargetCount(right.CameraID) == 0){
            SmartDashboard.putNumber("Priority tag",0);
            return null;
        }
        Optional<PoseEstimate> leftEstimate = left.update(); 
        Optional<PoseEstimate> rightEstimate = right.update();
        
       SmartDashboard.putBoolean("sees right estimate?", rightEstimate.isPresent());
        //checking if any of the camera poses are null
       if(!leftEstimate.isPresent() && !rightEstimate.isPresent()){
           return null;
       }

        if(!leftEstimate.isPresent() && rightEstimate.isPresent()){
            SmartDashboard.putNumber("Priority tag",1);
            return right;
        }
        else if(!rightEstimate.isPresent() && leftEstimate.isPresent()){
            SmartDashboard.putNumber("Priority tag",2);
            return left;
        }

        int lID = left.tid;
        int rID = right.tid;

            SmartDashboard.putNumber("Priority tag",3);
            // Get distance from priority tag to each camera
            /*
             * Calcualte the distance by getting camera pose coordinates and the coordinates of priority tag and get distance
             * 
             */
                double leftDistance = 0;
                double rightDistance = 0;
               
            
                RawFiducial[] arrayOfTagsForLeftCamera = LimelightHelpers.getRawFiducials(left.CameraID);
                RawFiducial[] arrayOfTagsForRightCamera = LimelightHelpers.getRawFiducials(right.CameraID);

                for(int i = 0; i < arrayOfTagsForLeftCamera.length; i++ ){
                    if(arrayOfTagsForLeftCamera[i].id == lID){
                        leftDistance = arrayOfTagsForLeftCamera[i].distToCamera;
                    }
                }
                for(int i = 0; i < arrayOfTagsForRightCamera.length; i++ ){
                    if(arrayOfTagsForRightCamera[i].id == rID){
                        rightDistance = arrayOfTagsForRightCamera[i].distToCamera;
                    }
                }
                if (leftDistance < rightDistance){
                    SmartDashboard.putNumber("Priority tag",4);
                    return left;
                }else if(leftDistance >= rightDistance){
    
                    SmartDashboard.putNumber("Priority tag",5);
                    return right;
                } 
    
        return left;


    }


     public double getStdDev(Optional<PoseEstimate> bestEstimate) {
        return bestEstimate.isPresent() && bestEstimate.get().rawFiducials.length > 0 ? 
            Math.pow(Arrays.stream(bestEstimate.get().rawFiducials).mapToDouble(fiducial -> fiducial.distToCamera).min().getAsDouble(),2) / bestEstimate.get().tagCount : 
            Double.MAX_VALUE;
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
        swerve.resetTranslation(initialPose.getX(), initialPose.getY());
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
        swerve.resetTranslation(observation.pose.getX(), observation.pose.getY());
        estimatedPose = observation.pose;
    
    //         // sample --> odometryPose transform and backwards of that
    //         var sampleToOdometryTransform = new Transform2d(sample.get(), odometryPose); // current bservatin 
    //         var odometryToSampleTransform = new Transform2d(odometryPose, sample.get());
    
    //         // get old estimate by applying odometryToSample Transform
    //         Pose2d estimateAtTime = estimatedPose.plus(odometryToSampleTransform);
    //   // difference between estimate and vision pose
          
    //         Transform2d transform = new Transform2d(estimateAtTime, observation.pose);
    //     transform = transform.times(Math.max(1, weight));

    //     // Recalculate current estimate by applying scaled transform to old estimate
    //     // then replaying odometry data
    //     estimatedPose = estimateAtTime.plus(transform).plus(sampleToOdometryTransform);
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