package org.wildstang.sample.subsystems.swerve;

import static edu.wpi.first.units.Units.MetersPerSecond;

import com.ctre.phoenix6.StatusCode;
import com.ctre.phoenix6.swerve.SwerveDrivetrain.SwerveControlParameters;
import com.ctre.phoenix6.swerve.SwerveModule.DriveRequestType;
import com.ctre.phoenix6.swerve.SwerveModule.SteerRequestType;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;

import com.ctre.phoenix6.swerve.SwerveModule;
import com.ctre.phoenix6.swerve.SwerveRequest;
import com.ctre.phoenix6.swerve.utility.PhoenixPIDController;

public class TranslationRequest implements SwerveRequest{

    private final FieldCentricFacingAngle request = new FieldCentricFacingAngle()
        .withDriveRequestType(DriveRequestType.OpenLoopVoltage)
        .withSteerRequestType(SteerRequestType.Position);
    public Pose2d targetPose = new Pose2d();
    private double maxSpeed = 1.0;
    private double velX;
    private double velY;
    public PhoenixPIDController HeadingController = new PhoenixPIDController(0, 0, 0);

    @Override
    public StatusCode apply(SwerveControlParameters arg0, SwerveModule<?, ?, ?>... arg1) {
        velX = DriveConstants.ALIGN_P * DriveConstants.maxSpeed.in(MetersPerSecond) * 
                (targetPose.getX() - arg0.currentPose.getX());
        if (Math.abs(velX) > maxSpeed * DriveConstants.maxSpeed.in(MetersPerSecond)){
            velX = Math.signum(velX) * maxSpeed * DriveConstants.maxSpeed.in(MetersPerSecond);
        }
        velY = DriveConstants.ALIGN_P * DriveConstants.maxSpeed.in(MetersPerSecond) *
                (targetPose.getY() - arg0.currentPose.getY());
        if (Math.abs(velY) > maxSpeed * DriveConstants.maxSpeed.in(MetersPerSecond)){
            velX = Math.signum(velY) * maxSpeed * DriveConstants.maxSpeed.in(MetersPerSecond);
        }
        
        return request
            .withVelocityX(velX)
            .withVelocityY(velY)
            .withTargetDirection(targetPose.getRotation())
            .apply(arg0, arg1);
    }
    public void setTarget(Pose2d target){
        this.targetPose = target;
        this.maxSpeed = 1.0;
    }
    public void setTarget(Pose2d target, double newSpeed){
        this.targetPose = target;
        this.maxSpeed = newSpeed;
    }
    
}
