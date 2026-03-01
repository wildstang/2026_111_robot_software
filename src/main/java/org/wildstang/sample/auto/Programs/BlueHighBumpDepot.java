package org.wildstang.sample.auto.Programs;

import org.wildstang.framework.auto.AutoProgram;
import org.wildstang.framework.core.Core;
import org.wildstang.sample.auto.Steps.AutoReadyBallpathStep;
import org.wildstang.sample.auto.Steps.AutoSetupStep;
import org.wildstang.sample.auto.Steps.AutoStartLauncherStep;
import org.wildstang.sample.auto.Steps.IntakingStep;
import org.wildstang.sample.auto.Steps.ShootingStep;
import org.wildstang.sample.auto.Steps.SwerveMultiPointStep;
import org.wildstang.sample.auto.Steps.SwerveToPointStep;
import org.wildstang.sample.robot.WsSubsystems;
import org.wildstang.sample.subsystems.swerve.SwerveDrive;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.wpilibj.DriverStation.Alliance;

public class BlueHighBumpDepot extends AutoProgram{

    @Override
    protected void defineSteps() {
        SwerveDrive swerve = (SwerveDrive) Core.getSubsystemManager().getSubsystem(WsSubsystems.SWERVE_DRIVE);
        addStep(new AutoSetupStep(AutoPos.highStart, Alliance.Blue));
        addStep(new AutoStartLauncherStep());
        addStep(new SwerveToPointStep(swerve, AutoPos.highShootPos));
        addStep(new ShootingStep(1.5));

        addStep(new IntakingStep());
        addStep(new SwerveMultiPointStep(new Pose2d[]{AutoPos.highPostJump, AutoPos.highPreIntake}, 
            new double[]{1.0, 1.0}));
        addStep(new SwerveToPointStep(swerve, AutoPos.highFinishIntake, 0.4));
        addStep(new SwerveMultiPointStep(new Pose2d[]{AutoPos.highPostJump, AutoPos.highShootPos}, new double[]{1.0, 0.6}));
        addStep(new ShootingStep(6.0));

        addStep(new IntakingStep());
        addStep(new SwerveToPointStep(swerve, AutoPos.highPreDepot));
        addStep(new AutoReadyBallpathStep());
        addStep(new SwerveToPointStep(swerve, AutoPos.highPostDepot, 0.35));
        addStep(new ShootingStep(3.0));
    }

    @Override
    public String toString() {
        return "Blue High Bump Depot";
    }
    
}
