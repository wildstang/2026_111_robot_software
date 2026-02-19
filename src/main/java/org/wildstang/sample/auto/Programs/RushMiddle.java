package org.wildstang.sample.auto.Programs;
import org.wildstang.framework.auto.AutoProgram;
import org.wildstang.framework.auto.AutoStep;
import org.wildstang.framework.auto.steps.AutoParallelStepGroup;
import org.wildstang.framework.core.Core;
import org.wildstang.sample.auto.Steps.AutoSetupStep;
import org.wildstang.sample.auto.Steps.SwerveToPointStep;
import org.wildstang.sample.robot.WsSubsystems;
import org.wildstang.sample.subsystems.swerve.SwerveDrive;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.wpilibj.DriverStation.Alliance;

public class RushMiddle extends AutoProgram {
    
    @Override
    protected void defineSteps(){
        SwerveDrive swerve = (SwerveDrive) Core.getSubsystemManager().getSubsystem(WsSubsystems.SWERVE_DRIVE);
        addStep(new AutoSetupStep(7.7559, 6.515, 0, Alliance.Blue));
        addStep(new SwerveToPointStep(swerve, new Pose2d(5.802069,2.24773,new Rotation2d(0.4378731))));
        addStep(new SwerveToPointStep(swerve, new Pose2d(7.549369,1.968659,new Rotation2d(0.626978681967))));
        addStep(new SwerveToPointStep(swerve, new Pose2d(7.6663, 2.8651998, new Rotation2d(1.570796))));
        addStep(new SwerveToPointStep(swerve, new Pose2d(7.719572067, 3.835101604, new Rotation2d(1.5707963267))));
        addStep(new SwerveToPointStep(swerve, new Pose2d(7.719572067, 4.631082534790, new Rotation2d(1.551567818292692))));
        addStep(new SwerveToPointStep(swerve, new Pose2d(7.7559051513, 6.514901161, new Rotation2d(1.570796326794))));

    }

    @Override
    public String toString() {
        // TODO Auto-generated method stub
        return "Rush Middle";
    }
    

}
