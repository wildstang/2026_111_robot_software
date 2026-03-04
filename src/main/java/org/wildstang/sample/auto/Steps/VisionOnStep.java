package org.wildstang.sample.auto.Steps;

import org.wildstang.framework.auto.AutoStep;
import org.wildstang.framework.core.Core;
import org.wildstang.sample.robot.WsSubsystems;
import org.wildstang.sample.subsystems.swerve.SwerveDrive;
import org.wildstang.sample.subsystems.targeting.WsPose;

public class VisionOnStep extends AutoStep{

    private SwerveDrive swerve;
    private boolean isOn = false;

    public VisionOnStep(boolean turnOn){
        this.isOn = turnOn;
    }

    @Override
    public void initialize() {
        swerve = (SwerveDrive) Core.getSubsystemManager().getSubsystem(WsSubsystems.SWERVE_DRIVE);
    }

    @Override
    public void update() {
        swerve.turnOnVision(isOn);
        setFinished();

    }

    @Override
    public String toString() {
        return "Vision On Step";
    }
    
}
