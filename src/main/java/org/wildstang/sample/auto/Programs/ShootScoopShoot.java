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
import org.wildstang.sample.auto.Steps.AutoStartLauncherStep;

public class ShootScoopShoot extends AutoProgram {
    
    @Override
    protected void defineSteps(){
        SwerveDrive swerve = (SwerveDrive) Core.getSubsystemManager().getSubsystem(WsSubsystems.SWERVE_DRIVE);
        addStep(new AutoSetupStep(3.3911373615264893, 2.5110859870910645, 0.7568339765983157 , Alliance.Blue));
        addStep(new AutoReadyBallpathStep());
        addStep(new AutoStartLauncherStep());

        addStep(new SwerveToPointStep(swerve, new Pose2d(4.667594909667969, 2.3875575065612793 ,new Rotation2d(0))));
        addStep(new SwerveToPointStep(swerve, new Pose2d(5.944052696228027, 2.0787370204925537,new Rotation2d(0))));
        
        AutoParallelStepGroup group1 = new AutoParallelStepGroup();
        group1.addStep(new AutoStartIntakeStep());
        group1.addStep(new SwerveToPointStep(swerve, new Pose2d(7.17933464050293, 1.7081525325775146, new Rotation2d(0.7086266808492994 ))));

        addStep(group1);

        addStep(new SwerveToPointStep(swerve, new Pose2d(7.591094970703125, 2.346381425857544, new Rotation2d(0.982793723247329))));
        addStep(new SwerveToPointStep(swerve, new Pose2d(7.817563056945801, 2.7787299156188965 , new Rotation2d(2.111214975562058))));
        
        AutoParallelStepGroup group2 = new AutoParallelStepGroup();
        group2.addStep(new SwerveToPointStep(swerve, new Pose2d(6.705810070037842, 2.964022397994995, new Rotation2d(2.77761408691693))));
        group2.addStep(new AutoStartIntakeStep());

        addStep(group2);

        addStep(new SwerveToPointStep(swerve, new Pose2d(5.408764362335205,2.9022581577301025, new Rotation2d(-3.1145723565543277))));
        addStep(new SwerveToPointStep(swerve, new Pose2d(4.026819705963135 ,2.92758846282959, new Rotation2d(-3.076467566550133))));

        AutoParallelStepGroup group3 = new AutoParallelStepGroup();
        group3.addStep(new AutoReadyBallpathStep());
        group3.addStep(new AutoStartLauncherStep());
        group3.addStep(new SwerveToPointStep(swerve, new Pose2d(2.855848789215088 , 3.0051984786987305, new Rotation2d(0.6022869468090264))));

        addStep(group2);
        addStep(new SwerveToPointStep(swerve, new Pose2d(2.7117323875427246 , 4.425772666931152 , new Rotation2d(0 ))));


    }

    @Override
    public String toString() {
        // TODO Auto-generated method stub
        return "Shoot Scoop Shoot";
    }
    

}
