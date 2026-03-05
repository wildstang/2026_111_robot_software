package org.wildstang.sample.auto.Programs;

import org.wildstang.framework.auto.AutoProgram;
import org.wildstang.framework.auto.steps.AutoParallelStepGroup;
import org.wildstang.framework.auto.steps.control.AutoStepDelay;
import org.wildstang.framework.core.Core;
import org.wildstang.sample.auto.Steps.AutoBumpJumpStep;
import org.wildstang.sample.auto.Steps.AutoReadyBallpathStep;
import org.wildstang.sample.auto.Steps.AutoSetupStep;
import org.wildstang.sample.auto.Steps.AutoStartLauncherStep;
import org.wildstang.sample.auto.Steps.IntakingStep;
import org.wildstang.sample.auto.Steps.MultiBumpJumpStep;
import org.wildstang.sample.auto.Steps.MultiPointSnakeStep;
import org.wildstang.sample.auto.Steps.ShootingStep;
import org.wildstang.sample.auto.Steps.SwerveMultiPointStep;
import org.wildstang.sample.auto.Steps.SwerveToPointStep;
import org.wildstang.sample.auto.Steps.VisionOnStep;
import org.wildstang.sample.robot.WsSubsystems;
import org.wildstang.sample.subsystems.swerve.SwerveDrive;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.wpilibj.DriverStation.Alliance;

public class RedHighBumpDepot extends AutoProgram{

    @Override
    protected void defineSteps() {
        SwerveDrive swerve = (SwerveDrive) Core.getSubsystemManager().getSubsystem(WsSubsystems.SWERVE_DRIVE);
        addStep(new AutoSetupStep(AutoPos.highStart, Alliance.Red));
        addStep(new AutoStartLauncherStep());
        addStep(new SwerveToPointStep(swerve, AutoPos.highShootPos));
        addStep(new ShootingStep(1.5));

        // // //below for straight there and back
        addStep(new IntakingStep());
        // //straight line
        // // addStep(new MultiBumpJumpStep(new Pose2d[]{AutoPos.highJumpToNeutral, AutoPos.highIntake}, 
        // //     0, AutoPos.highPostJump, 1.4, 0.4));
        // //slight curve
        addStep(new MultiBumpJumpStep(new Pose2d[]{AutoPos.highJumpToNeutral, AutoPos.loopA, AutoPos.loopB}, 
            0, AutoPos.highPostJump, 1.4, 0.4));
            
        addStep(new VisionOnStep(true));
        addStep(new MultiBumpJumpStep(new Pose2d[]{AutoPos.highPostJump, AutoPos.highJumpToAlliance, AutoPos.highShootPos}, 
            1, AutoPos.highShootPos, 1.2, 1.0));
        // // AutoParallelStepGroup group2 = new AutoParallelStepGroup();
        addStep(new SwerveToPointStep(swerve, AutoPos.highShootPos));
        addStep(new ShootingStep(5.0));
        addStep(new VisionOnStep(false));
        // addStep(group2);

        // //below for the loop
        // addStep(new IntakingStep());
        // addStep(new AutoBumpJumpStep(swerve, AutoPos.highJumpToNeutral, 1.8, AutoPos.highPostJump));
        // //this for normal
        // //addStep(new SwerveMultiPointStep(new Pose2d[]{AutoPos.loopA, AutoPos.loopB, AutoPos.loopC, AutoPos.loopD, AutoPos.loopE}, new double[]{0.4, 0.4, 0.4, 0.4, 0.4}));
        // //this for snake
        // addStep(new MultiPointSnakeStep(new Pose2d[]{AutoPos.loopA, AutoPos.loopB, AutoPos.loopC, AutoPos.loopD, AutoPos.loopE}, new double[]{0.4, 0.4, 0.4, 0.4, 0.4}));
        // addStep(new AutoBumpJumpStep(swerve, AutoPos.highJumpToAlliance2, 1.8, AutoPos.highShootPos2));
        // AutoParallelStepGroup group2alt = new AutoParallelStepGroup();
        // group2alt.addStep(new SwerveToPointStep(swerve, AutoPos.highShootPos2));
        // group2alt.addStep(new ShootingStep(5.0));
        // addStep(group2alt);

        // //alt end
        // addStep(new AutoReadyBallpathStep());
        // addStep(new SwerveToPointStep(swerve, AutoPos.highPreDepot, 0.4));
        // addStep(new SwerveToPointStep(swerve, AutoPos.highPostDepot, 0.35));
        // addStep(new ShootingStep(20.0));

        addStep(new IntakingStep());
        addStep(new SwerveToPointStep(swerve, AutoPos.highPreDepot));
        addStep(new VisionOnStep(true));
        addStep(new SwerveToPointStep(swerve, AutoPos.highPreDepot));
        addStep(new AutoReadyBallpathStep());
        addStep(new SwerveToPointStep(swerve, AutoPos.highPostDepot, 0.15));
        addStep(new ShootingStep(20.0));
    }

    @Override
    public String toString() {
        return "Red High Bump Depot";
    }
    // addStep(new IntakingStep());
        // addStep(new AutoBumpJumpStep(swerve, AutoPos.highJumpToNeutral, 2.0, AutoPos.highPostJump));
        // addStep(new AutoStepDelay(100));
        // addStep(new SwerveMultiPointStep(new Pose2d[]{AutoPos.highPostJump, AutoPos.highPreIntake}, 
        //     new double[]{1.0, 1.0}));
        // addStep(new SwerveToPointStep(swerve, AutoPos.highFinishIntake, 0.4));
        // addStep(new SwerveToPointStep(swerve, AutoPos.highPostJump, 1.0));
        // addStep(new AutoStepDelay(100));
        // addStep(new AutoBumpJumpStep(swerve, AutoPos.highJumpToAlliance, 2.2, AutoPos.highShootPos));
        // // addStep(new MultiBumpJumpStep(new Pose2d[]{AutoPos.highPostJump, AutoPos.highJumpToAlliance, AutoPos.highShootPos}, 1, AutoPos.highShootPos, 1.8));
        // addStep(new AutoStepDelay(100));
        // addStep(new ShootingStep(5.0));
}
