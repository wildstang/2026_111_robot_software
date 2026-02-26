package main.java.org.wildstang.sample.auto.Programs;
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
import main.java.org.wildstang.sample.auto.Steps.AutoStartLauncherStep;

public class OneLoop extends AutoProgram {
    
    @Override
    protected void defineSteps(){
        SwerveDrive swerve = (SwerveDrive) Core.getSubsystemManager().getSubsystem(WsSubsystems.SWERVE_DRIVE);
        addStep(new AutoSetupStep(3.0617289543151855, 2.8816704750061035, 0, Alliance.Blue));
        addStep(new SwerveToPointStep(swerve, new Pose2d(5.038179397583008,2.6757900714874268 ,new Rotation2d(0))));
        addStep(new SwerveToPointStep(swerve, new Pose2d(7.549369,1.968659,new Rotation2d(0))));
        
        AutoParallelStepGroup group1 = new AutoParallelStepGroup();
        group1.addStep(new AutoStartIntakeStep());
        group1.addStep(new addStep(new SwerveToPointStep(swerve, new Pose2d(6.541105270385742, 2.3875577449798584, new Rotation2d(0)))));

        addStep(group1);

        addStep(new SwerveToPointStep(swerve, new Pose2d(7.838151454925537, 1.131688117980957, new Rotation2d(1.5232134658689336 ))));
        addStep(new SwerveToPointStep(swerve, new Pose2d(7.7763872146606445, 3.5816633701324463 , new Rotation2d(1.6078162523052952 ))));
        addStep(new SwerveToPointStep(swerve, new Pose2d(7.73521089553833, 5.434586048126221, new Rotation2d(1.6775314737236904 ))));

        addStep(new SwerveToPointStep(swerve, new Pose2d(6.376401424407959, 5.990462779998779, new Rotation2d(0))));
        addStep(new SwerveToPointStep(swerve, new Pose2d(5.161707878112793 , 5.702230453491211 , new Rotation2d(0 ))));

        AutoParallelStepGroup group2 = new AutoParallelStepGroup();
        group2.addStep(new AutoReadyBallpathStep());
        group2.addStep(new AutoStartLauncherStep());
        group2.addStep(new SwerveToPointStep(swerve, new Pose2d(2.7117323875427246 , 5.496349811553955 , new Rotation2d(0 ))));

        addStep(group2);
        addStep(new SwerveToPointStep(swerve, new Pose2d(2.7117323875427246 , 4.425772666931152 , new Rotation2d(0 ))));


    }

    @Override
    public String toString() {
        // TODO Auto-generated method stub
        return "Rush Middle";
    }
    

}
