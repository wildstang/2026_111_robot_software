package org.wildstang.sample.auto.Steps;

import org.wildstang.framework.auto.AutoStep;
import org.wildstang.framework.core.Core;
import org.wildstang.sample.robot.WsSubsystems;
import org.wildstang.sample.subsystems.Ballpath;
import org.wildstang.sample.subsystems.Intake;
import org.wildstang.sample.subsystems.Ballpath.GameState;
import org.wildstang.sample.subsystems.Intake.DeployState;
import org.wildstang.sample.subsystems.Intake.IntakeState;

public class IntakingStep extends AutoStep{

    private Intake intake;

    @Override
    public void initialize() {
        intake = (Intake) Core.getSubsystemManager().getSubsystem(WsSubsystems.INTAKE);
    }

    @Override
    public void update() {
        intake.setDeployState(DeployState.INTAKING);
        intake.setIntakeState(IntakeState.INTAKING);
        setFinished();
    }

    @Override
    public String toString() {
        return "Intaking Step";
    }
    
}
