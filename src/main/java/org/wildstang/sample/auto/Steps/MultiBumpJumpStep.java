package org.wildstang.sample.auto.Steps;

import java.util.Arrays;

import org.wildstang.framework.auto.AutoStep;
import org.wildstang.framework.core.Core;
import org.wildstang.sample.robot.WsSubsystems;
import org.wildstang.sample.subsystems.swerve.SwerveDrive;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.wpilibj.Timer;



public class MultiBumpJumpStep extends AutoStep {

    private SwerveDrive swerve;

    private int index = 0;
    private int jump = 1;
    private Pose2d[] poses;
    private Pose2d reset;
    private double duration;
    private double speed;

    private Timer timer = new Timer();

    public MultiBumpJumpStep(Pose2d[] poses, int jumpIndex, Pose2d resetPose, double time, double maxSpeed){
        swerve = (SwerveDrive) Core.getSubsystemManager().getSubsystem(WsSubsystems.SWERVE_DRIVE);
        this.poses = poses;
        this.jump = jumpIndex;
        this.reset = resetPose;
        this.duration = time;
        this.speed = maxSpeed;
    }

    @Override
    public void initialize() {
        swerve.setToAuto();
        timer.start();
    }

    @Override
    public void update() {

        if (index >= poses.length) setFinished();
        if (index != jump) swerve.setAutoValues(poses[index], speed);
        else swerve.setAutoValues(poses[jump], 1.0);
        
        // Drive to intermediate point
        if (index < poses.length - 1) {
            swerve.usePID(false);
            if (index != jump && swerve.isAtPosition(0.3)) {
                index++;
                if (index == jump) timer.reset();
            }
            if (index == jump && timer.hasElapsed(duration)){
                swerve.resetTranslation(reset.getX(), reset.getY());
                index++;
            }
        } else {
            swerve.usePID(true);
            if (swerve.isAtPosition()) {
                setFinished();
                return;
            }
            swerve.usePID(true);
        }
        
    }

    @Override
    public String toString() {
        return "Swerve Multi BumpJump Step";
    }
}
