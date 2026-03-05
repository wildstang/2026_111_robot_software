package org.wildstang.sample.auto.Programs;

import org.wildstang.framework.auto.AutoProgram;
import org.wildstang.framework.auto.steps.AutoParallelStepGroup;
import org.wildstang.framework.auto.steps.control.AutoStepDelay;
import org.wildstang.framework.core.Core;
import org.wildstang.sample.auto.Steps.AutoBumpJumpStep;
import org.wildstang.sample.auto.Steps.AutoReadyBallpathStep;
import org.wildstang.sample.auto.Steps.AutoSetupStep;
import org.wildstang.sample.auto.Steps.AutoStartLauncherStep;
import org.wildstang.sample.auto.Steps.AutoStopBallpathStep;
import org.wildstang.sample.auto.Steps.IntakingStep;
import org.wildstang.sample.auto.Steps.MultiBumpJumpStep;
import org.wildstang.sample.auto.Steps.MultiPointSnakeStep;
import org.wildstang.sample.auto.Steps.ShootingStep;
import org.wildstang.sample.auto.Steps.SwerveMultiPointStep;
import org.wildstang.sample.auto.Steps.SwerveToPointStep;
import org.wildstang.sample.robot.WsSubsystems;
import org.wildstang.sample.subsystems.swerve.SwerveDrive;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.wpilibj.DriverStation.Alliance;

public class RedDepotOutpost extends AutoProgram{

    @Override
    protected void defineSteps() {
        SwerveDrive swerve = (SwerveDrive) Core.getSubsystemManager().getSubsystem(WsSubsystems.SWERVE_DRIVE);
        addStep(new AutoSetupStep(AutoPos.highStart, Alliance.Red));
        addStep(new AutoStartLauncherStep());
        addStep(new SwerveToPointStep(swerve, AutoPos.highShootPos));
        addStep(new ShootingStep(1.5));        

        addStep(new IntakingStep());
        addStep(new SwerveToPointStep(swerve, AutoPos.highPreDepot));
        addStep(new SwerveToPointStep(swerve, AutoPos.highPreDepot));
        addStep(new AutoReadyBallpathStep());
        addStep(new SwerveToPointStep(swerve, AutoPos.highPostDepot, 0.15));
        addStep(new AutoStopBallpathStep());

        addStep(new IntakingStep());
        addStep(new SwerveMultiPointStep(new Pose2d[]{AutoPos.outpostA, AutoPos.outpostB, AutoPos.outpostC}));
        addStep(new AutoReadyBallpathStep());
        addStep(new AutoStepDelay(1500));
        addStep(new ShootingStep(20.0));

    }

    @Override
    public String toString() {
        return "Red Depot Outpost";
    }
    
}
