package org.wildstang.sample.auto.Programs;

import org.wildstang.framework.auto.AutoProgram;
import org.wildstang.framework.core.Core;
import org.wildstang.sample.auto.Steps.AutoSetupStep;
import org.wildstang.sample.auto.Steps.SwerveMultiPointStep;
import org.wildstang.sample.auto.Steps.SwerveToPointStep;
import org.wildstang.sample.robot.WsSubsystems;
import org.wildstang.sample.subsystems.swerve.SwerveDrive;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.wpilibj.DriverStation.Alliance;

public class TestSquare extends AutoProgram{

    SwerveDrive swerve;

    @Override
    protected void defineSteps() {
        swerve = (SwerveDrive) Core.getSubsystemManager().getSubsystem(WsSubsystems.SWERVE_DRIVE);
        addStep(new AutoSetupStep(new Pose2d(0, 0, new Rotation2d()), Alliance.Blue));
        addStep(new SwerveMultiPointStep(new Pose2d[]{
            new Pose2d(50.0/39.37, 0, new Rotation2d(Math.toRadians(45.0))),
            new Pose2d(50.0/39.37, 50.0/39.37, new Rotation2d(Math.toRadians(90.0))),
            new Pose2d(0, 50.0/39.37, new Rotation2d(Math.toRadians(45.0))),
            new Pose2d(0, 0, new Rotation2d())
        }));
    }

    @Override
    public String toString() {
        return "Test Square";
    }
    
}
