package org.wildstang.sample.auto.Steps;

import org.wildstang.framework.auto.AutoStep;
import org.wildstang.sample.subsystems.swerve.SwerveDrive;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.wpilibj.Timer;



public class SwerveToPointStep extends AutoStep {

    private SwerveDrive swerve;
    private double maxspeed;

    private Pose2d fieldAutoPose;

    private Timer timer;

    public SwerveToPointStep(SwerveDrive drive, Pose2d pose, double speed) {
        this.maxspeed = speed;
        swerve = drive;
        timer = new Timer();
        fieldAutoPose = pose;
    }

    public SwerveToPointStep(SwerveDrive drive, Pose2d pose) {
        maxspeed = 1.0;
        swerve = drive;
        timer = new Timer();
        fieldAutoPose = pose;
    }

    @Override
    public void initialize() {
        swerve.setToAuto();
        timer.start();
    }

    @Override
    public void update() {
        swerve.setAutoValues(fieldAutoPose, maxspeed);
        if (swerve.isAtPosition()) {
            setFinished();
        } 
    }

    @Override
    public String toString() {
        return "Swerve To Point Step";
    }
}
