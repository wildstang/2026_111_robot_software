package org.wildstang.sample.auto.Programs;

import org.wildstang.framework.auto.AutoProgram;
import org.wildstang.framework.auto.steps.control.AutoStepDelay;
import org.wildstang.framework.core.Core;
import org.wildstang.sample.auto.Steps.AutoBumpJumpStep;
import org.wildstang.sample.auto.Steps.AutoReadyBallpathStep;
import org.wildstang.sample.auto.Steps.AutoSetupStep;
import org.wildstang.sample.auto.Steps.AutoStartLauncherStep;
import org.wildstang.sample.auto.Steps.IntakingStep;
import org.wildstang.sample.auto.Steps.MultiBumpJumpStep;
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
        addStep(new AutoBumpJumpStep(swerve, AutoPos.highJumpToNeutral, 1.6, AutoPos.highPostJump));
        addStep(new SwerveMultiPointStep(new Pose2d[]{AutoPos.highPostJump, AutoPos.highPreIntake}, 
            new double[]{1.0, 1.0}));
        addStep(new SwerveToPointStep(swerve, AutoPos.highFinishIntake, 0.4));
        addStep(new MultiBumpJumpStep(new Pose2d[]{AutoPos.highPostJump, AutoPos.highJumpToAlliance, AutoPos.highShootPos}, 1, AutoPos.highShootPos, 1.6));
        addStep(new AutoStepDelay(100));
        addStep(new ShootingStep(5.0));

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
