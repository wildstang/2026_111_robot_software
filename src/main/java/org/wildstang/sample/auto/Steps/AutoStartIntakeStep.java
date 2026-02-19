package org.wildstang.sample.auto.Steps;

import org.wildstang.framework.auto.AutoStep;
import org.wildstang.framework.core.Core;
import org.wildstang.sample.robot.WsSubsystems;
import org.wildstang.sample.subsystems.Intake;
import org.wildstang.sample.subsystems.Intake.DeployState;
import org.wildstang.sample.subsystems.Intake.IntakeState;

public class AutoStartIntakeStep extends AutoStep{

    Intake intake;
    
    @Override
    public void initialize() {
       intake = (Intake) Core.getSubsystemManager().getSubsystem(WsSubsystems.INTAKE);
    }

    @Override
    public void update() {
        intake.setDeployState(DeployState.OUT);
        intake.setIntakeState(IntakeState.INTAKING);
    }

    @Override
    public String toString() {
       return "Auto Intake Step";
    }
    
    
}
