package org.wildstang.sample.auto.Programs;

import org.wildstang.framework.auto.AutoProgram;
import org.wildstang.framework.core.Core;
import org.wildstang.sample.auto.Steps.AutoSetupStep;
import org.wildstang.sample.auto.Steps.IntakingStep;
import org.wildstang.sample.auto.Steps.ShootingStep;
import org.wildstang.sample.auto.Steps.SwerveMultiPointStep;
import org.wildstang.sample.auto.Steps.SwerveToPointStep;
import org.wildstang.sample.robot.WsSubsystems;
import org.wildstang.sample.subsystems.swerve.SwerveDrive;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.wpilibj.DriverStation.Alliance;

public class BlueLowDoubleBump extends AutoProgram{

    @Override
    protected void defineSteps() {
        SwerveDrive swerve = (SwerveDrive) Core.getSubsystemManager().getSubsystem(WsSubsystems.SWERVE_DRIVE);
        addStep(new AutoSetupStep(AutoPos.lowStart, Alliance.Blue));
        addStep(new ShootingStep(1.0));

        addStep(new IntakingStep());
        addStep(new SwerveMultiPointStep(new Pose2d[]{AutoPos.lowPostJump, AutoPos.lowPreIntake, AutoPos.lowFinishIntake}, 
            new double[]{0.6, 1.0, 0.6}));
        addStep(new SwerveMultiPointStep(new Pose2d[]{AutoPos.lowPostJump, AutoPos.lowShootPos}, new double[]{1.0, 0.6}));
        addStep(new ShootingStep(5.0));

        addStep(new IntakingStep());
        addStep(new SwerveMultiPointStep(new Pose2d[]
            {AutoPos.lowPostJump, AutoPos.lowPreIntake, AutoPos.lowLoopA, AutoPos.lowLoopB, AutoPos.lowPreJump2}, 
            new double[]{0.6, 1.0, 0.6, 0.6, 0.6}));
        addStep(new SwerveToPointStep(swerve, AutoPos.lowShootPos2, 0.6));
        addStep(new ShootingStep(5.0));

    }

    @Override
    public String toString() {
        return "Blue Low Double Bump";
    }
    
}
