package org.wildstang.sample.auto.Programs;
import org.wildstang.framework.auto.AutoProgram;
import org.wildstang.framework.auto.AutoStep;
import org.wildstang.framework.auto.steps.AutoParallelStepGroup;
import org.wildstang.framework.core.Core;
import org.wildstang.sample.auto.Steps.AutoReadyBallpathStep;
import org.wildstang.sample.auto.Steps.AutoSetupStep;
import org.wildstang.sample.auto.Steps.AutoStartIntakeStep;
import org.wildstang.sample.auto.Steps.SwerveToPointStep;
import org.wildstang.sample.robot.WsSubsystems;
import org.wildstang.sample.subsystems.swerve.SwerveDrive;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.wpilibj.DriverStation.Alliance;
import org.wildstang.sample.auto.Steps.AutoStartLauncherStep;
import org.wildstang.sample.auto.Steps.AutoStowLauncherStep;

public class ShootScoopShootTop extends AutoProgram {
    
    @Override
    protected void defineSteps(){
        SwerveDrive swerve = (SwerveDrive) Core.getSubsystemManager().getSubsystem(WsSubsystems.SWERVE_DRIVE);
        addStep(new AutoSetupStep(3.3079943656921387,5.304169178009033, 0.7568339765983157 , Alliance.Blue));
        addStep(new AutoReadyBallpathStep());
        addStep(new AutoStartLauncherStep());

        addStep(new SwerveToPointStep(swerve, new Pose2d(4.675332069396973, 5.355766773223877 ,new Rotation2d(0))));
        addStep(new SwerveToPointStep(swerve, new Pose2d(5.930831432342529, 5.373841762542725,new Rotation2d(0))));
        
        AutoParallelStepGroup group1 = new AutoParallelStepGroup();
        group1.addStep(new AutoStartIntakeStep());
        group1.addStep(new SwerveToPointStep(swerve, new Pose2d(6.790835380554199, 6.12973165512085, new Rotation2d(-0.4636479786588318))));

        addStep(group1);

        addStep(new SwerveToPointStep(swerve, new Pose2d(7.642196178436279, 5.716950416564941, new Rotation2d(-1.002271880340043))));
        addStep(new SwerveToPointStep(swerve, new Pose2d(7.771190643310547, 4.813991546630859 , new Rotation2d(-2.383710451496954 ))));
        
        AutoParallelStepGroup group2 = new AutoParallelStepGroup();
        group2.addStep(new SwerveToPointStep(swerve, new Pose2d(6.9456281661987305, 4.736595153808594, new Rotation2d(2.77761408691693))));
        group2.addStep(new AutoStowLauncherStep());

        addStep(group2);

        addStep(new SwerveToPointStep(swerve, new Pose2d(5.783071994781494,5.312275409698486, new Rotation2d(-3.1145723565543277))));
        addStep(new SwerveToPointStep(swerve, new Pose2d(4.724131107330322 ,5.238395690917969, new Rotation2d(-3.076467566550133))));

        AutoParallelStepGroup group3 = new AutoParallelStepGroup();
        group3.addStep(new AutoReadyBallpathStep());
        group3.addStep(new AutoStartLauncherStep());
        group3.addStep(new SwerveToPointStep(swerve, new Pose2d(3.204798698425293, 5.101334095001, new Rotation2d(-0.6823159895151811))));

        addStep(group3);
        addStep(new SwerveToPointStep(swerve, new Pose2d(2.91166639328002 , 4.558308124542236, new Rotation2d(-0.6435014424752783))));


    }

    @Override
    public String toString() {
        // TODO Auto-generated method stub
        return "Shoot Scoop Shoot Top";
    }
    

}
