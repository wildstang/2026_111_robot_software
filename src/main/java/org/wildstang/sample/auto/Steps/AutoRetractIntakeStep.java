package org.wildstang.sample.auto.Steps;

import org.wildstang.framework.auto.AutoStep;
import org.wildstang.sample.robot.WsSubsystems;
import org.wildstang.sample.subsystems.Intake;
import org.wildstang.sample.subsystems.Ballpath.GameState;
import org.wildstang.sample.subsystems.Intake.DeployState;
import org.wildstang.sample.subsystems.Intake.IntakeState;
import org.wildstang.framework.core.Core;

public class AutoRetractIntakeStep extends AutoStep{

    Intake intake;

    @Override
    public void initialize() {
       intake = (Intake) Core.getSubsystemManager().getSubsystem(WsSubsystems.INTAKE);
    }

    @Override
    public void update() {
        intake.setIntakeState(IntakeState.NEUTRAL);
        intake.setDeployState(DeployState.IN);
    }

    @Override
    public String toString() {
       return "Auto Retract Intake Step";
    }
    
    
}
