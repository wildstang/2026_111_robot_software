package org.wildstang.sample.subsystems.swerve;

import static edu.wpi.first.units.Units.MetersPerSecond;
import static edu.wpi.first.units.Units.RadiansPerSecond;
import static edu.wpi.first.units.Units.RotationsPerSecond;

import com.ctre.phoenix6.swerve.SwerveModule.DriveRequestType;
import com.ctre.phoenix6.swerve.SwerveModule.SteerRequestType;
import com.ctre.phoenix6.swerve.SwerveRequest;
import com.ctre.phoenix6.swerve.utility.PhoenixPIDController;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Transform2d;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class SwerveSignal {
    private double verticalSpeed = 0;
    private double horizontalSpeed = 0;
    private double rotationSpeed = 0;
    private double rotationTarget = 0;
    private Pose2d drivingPose = new Pose2d();
    private Pose2d currentPose = new Pose2d();
    private double autoMaxSpeed = 1.0;
    private boolean autoSnake = false;
    private double teleopMax = 1.0;
    public enum controlState {MANUAL, ROTLOCKED, SNAKE, DRIVETOPOINT, X_DRIVETOPOINT, Y_DRIVETOPOINT}
    private controlState state = controlState.MANUAL;
    private SwerveRequest.FieldCentric driveCommand = new SwerveRequest.FieldCentric()
        .withDriveRequestType(DriveRequestType.OpenLoopVoltage)
        .withSteerRequestType(SteerRequestType.Position);
    private SwerveRequest.FieldCentricFacingAngle driveLockedCommand = new SwerveRequest.FieldCentricFacingAngle()
        .withDriveRequestType(DriveRequestType.OpenLoopVoltage)
        .withSteerRequestType(SteerRequestType.Position);
    // private TranslationRequest translationRequest = new TranslationRequest();
    private SwerveRequest.FieldCentricFacingAngle translationRequest = new SwerveRequest.FieldCentricFacingAngle()
        .withDriveRequestType(DriveRequestType.OpenLoopVoltage)
        .withSteerRequestType(SteerRequestType.Position);
    private XTranslationRequest xTranslationRequest = new XTranslationRequest();
    private YTranslationRequest yTranslationRequest = new YTranslationRequest();    

    public SwerveSignal(){
        this(0,0,0);
    }
    public SwerveRequest drive(){
        if (state == controlState.ROTLOCKED){
            return driveLockedCommand.withVelocityX(getVertical() * DriveConstants.maxSpeed.in(MetersPerSecond))
                .withVelocityY(getHorizontal() * DriveConstants.maxSpeed.in(MetersPerSecond))
                .withTargetDirection(new Rotation2d(Math.toRadians(getRotTarget())))
                .withRotationalDeadband(0.05);
            //.withTargetRateFeedforward(double) to potentially include rotation rate


        } else if (state == controlState.SNAKE){
            if (getVertical() != 0 || getHorizontal() != 0) {
                setRotTarget(Math.toDegrees(Math.atan2(getHorizontal(), getVertical())));
                return driveLockedCommand.withVelocityX(getVertical() * DriveConstants.maxSpeed.in(MetersPerSecond))
                .withVelocityY(getHorizontal() * DriveConstants.maxSpeed.in(MetersPerSecond))
                .withTargetDirection(new Rotation2d(Math.atan2(getHorizontal(), getVertical())));
            }
            return driveLockedCommand.withVelocityX(getVertical() * DriveConstants.maxSpeed.in(MetersPerSecond))
                .withVelocityY(getHorizontal() * DriveConstants.maxSpeed.in(MetersPerSecond))
                .withTargetDirection(new Rotation2d(Math.toRadians(getRotTarget())));


        } else if (state == controlState.DRIVETOPOINT){
            // translationRequest.setTarget(getDriveToPoint(), autoMaxSpeed);
            double velX = DriveConstants.ALIGN_P * DriveConstants.maxSpeed.in(MetersPerSecond) * 
                (getDriveToPoint().getX() - currentPose.getX());
            if (Math.abs(velX) > autoMaxSpeed * DriveConstants.maxSpeed.in(MetersPerSecond)){
                velX = Math.signum(velX) * autoMaxSpeed * DriveConstants.maxSpeed.in(MetersPerSecond);
            }
            if (Math.abs(velX) < 0.06) velX = 0;
            double velY = DriveConstants.ALIGN_P * DriveConstants.maxSpeed.in(MetersPerSecond) *
                (getDriveToPoint().getY() - currentPose.getY());
            if (Math.abs(velY) > autoMaxSpeed * DriveConstants.maxSpeed.in(MetersPerSecond)){
                velY = Math.signum(velY) * autoMaxSpeed * DriveConstants.maxSpeed.in(MetersPerSecond);
            }
            if (Math.abs(velY) < 0.06) velY = 0;
            if (autoSnake){
                return translationRequest.withVelocityX(velX).withVelocityY(velY)
                    .withTargetDirection(new Rotation2d(Math.atan2(velY, velX)));
            }
            return translationRequest.withVelocityX(velX)
            .withVelocityY(velY)
            .withTargetDirection(getDriveToPoint().getRotation());


        } else if (state == controlState.X_DRIVETOPOINT){
            xTranslationRequest.setTarget(getDriveToPoint(), getHorizontal());
            return xTranslationRequest;


        } else if (state == controlState.Y_DRIVETOPOINT){
            yTranslationRequest.setTarget(getDriveToPoint(), getVertical());
            return yTranslationRequest;


        } else {//} else if (state == controlState.MANUAL){
            return driveCommand.withVelocityX(getVertical() * DriveConstants.maxSpeed.in(MetersPerSecond))
                .withVelocityY(getHorizontal() * DriveConstants.maxSpeed.in(MetersPerSecond))
                .withRotationalRate(getRotation() * Math.abs(getRotation()) * RotationsPerSecond.of(0.35).in(RadiansPerSecond));
        }
    }
    public SwerveSignal(double hori, double vert, double rot){
        this.verticalSpeed = vert;
        this.horizontalSpeed = hori;
        this.rotationSpeed = rot;
        driveLockedCommand.HeadingController = new PhoenixPIDController(DriveConstants.heading_P, 0.0, DriveConstants.heading_D);
        driveLockedCommand.HeadingController.enableContinuousInput(-Math.PI, Math.PI);
        translationRequest.HeadingController = new PhoenixPIDController(DriveConstants.heading_P, 0.0, DriveConstants.heading_D);
        translationRequest.HeadingController.enableContinuousInput(-Math.PI, Math.PI);
    }
    public void setTranslation(double hori, double vert){
        this.verticalSpeed = vert;
        this.horizontalSpeed = hori;
        teleopMax = 1.0;
        if (state != controlState.ROTLOCKED) state = controlState.MANUAL;
    }
    public void setTranslation(double hori, double vert, double max){
        this.verticalSpeed = vert;
        this.horizontalSpeed = hori;
        teleopMax = max;
        if (state != controlState.ROTLOCKED) state = controlState.MANUAL;
    }
    public void setFreeRotation(double rotation){
        state = controlState.MANUAL;
        rotationSpeed = rotation;
    }
    public void setRotLocked(double rotation){
        state = controlState.ROTLOCKED;
        rotationTarget = rotation;
    }
    private void setRotTarget(double rotation){
        rotationTarget = rotation;
    }
    public void setDriveToPoint(Pose2d newTarget){
        drivingPose = newTarget;
        rotationTarget = newTarget.getRotation().getDegrees();
        state = controlState.DRIVETOPOINT;
        autoMaxSpeed = 1.0;
    }
    public void setDriveToPoint(Pose2d newTarget, double maxSpeed, Pose2d currentPose){
        drivingPose = newTarget;
        rotationTarget = newTarget.getRotation().getDegrees();
        state = controlState.DRIVETOPOINT;
        autoMaxSpeed = maxSpeed;
        this.currentPose = currentPose;
    }
    public void setXDriveToPoint(double hori, Pose2d newTarget){
        this.horizontalSpeed = hori;
        this.drivingPose = newTarget;
        rotationTarget = newTarget.getRotation().getDegrees();
        state = controlState.X_DRIVETOPOINT;
    }
    public void setYDriveToPoint(double vert, Pose2d newTarget){
        this.verticalSpeed = vert;
        this.drivingPose = newTarget;
        rotationTarget = newTarget.getRotation().getDegrees();
        state = controlState.Y_DRIVETOPOINT;
    }
    public void setSnake(double vert, double hori){
        this.verticalSpeed = vert;
        this.horizontalSpeed = hori;
        state = controlState.SNAKE;
    }
    public void setAutoSnake(boolean isUsing){
        this.autoSnake = isUsing;
    }
    public double getVertical(){ 
        if (Math.abs(verticalSpeed) > teleopMax) return teleopMax * Math.signum(verticalSpeed);
        return verticalSpeed;
    }
    public double getHorizontal(){ 
        if (Math.abs(horizontalSpeed) > teleopMax) return teleopMax * Math.signum(horizontalSpeed);
        return horizontalSpeed;
    }
    public double getRotation(){
        if (Math.abs(rotationSpeed) > teleopMax) return teleopMax * Math.signum(rotationSpeed);
        return rotationSpeed;
    }
    public double getRotTarget(){
        return rotationTarget;
    }
    public Pose2d getDriveToPoint(){
        return drivingPose;
    }
    public controlState currentState(){
        return state;
    }
    public boolean isRotLocked(){
        return state == controlState.ROTLOCKED;
    }
    public boolean isManual(){
        return state == controlState.MANUAL;
    }
    public boolean isTranslate(){
        return state == controlState.DRIVETOPOINT;
    }
    public boolean isXTranslate(){
        return state == controlState.X_DRIVETOPOINT;
    }
    public boolean isYTranslate(){
        return state == controlState.Y_DRIVETOPOINT;
    }
}
