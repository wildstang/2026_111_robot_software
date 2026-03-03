package org.wildstang.sample.auto.Steps;

import org.wildstang.framework.auto.AutoStep;
import org.wildstang.sample.subsystems.swerve.SwerveDrive;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.wpilibj.Timer;



public class AutoBumpJumpStep extends AutoStep {

    private SwerveDrive swerve;

    private Pose2d toJump;
    private Pose2d reset;
    private double duration;

    private Timer timer;

    public AutoBumpJumpStep(SwerveDrive drive, Pose2d jumpPose, double time, Pose2d resetPose) {
        toJump = jumpPose;
        swerve = drive;
        timer = new Timer();
        reset = resetPose;
        duration = time;
    }

    @Override
    public void initialize() {
        swerve.setToAuto();
        timer.start();
    }

    @Override
    public void update() {
        swerve.setAutoValues(toJump, 1.0);
        if (timer.hasElapsed(duration)) {
            swerve.resetTranslation(reset.getX(), reset.getY());
            swerve.setAutoValues(reset);
            setFinished();
        } 
    }

    @Override
    public String toString() {
        return "Auto Bump Jump Step";
    }
}
