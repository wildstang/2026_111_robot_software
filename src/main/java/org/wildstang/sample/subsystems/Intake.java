package org.wildstang.sample.subsystems;

import org.wildstang.framework.core.Core;
import org.wildstang.framework.io.inputs.Input;
import org.wildstang.framework.subsystems.Subsystem;
import org.wildstang.hardware.roborio.outputs.WsTalon;
import org.wildstang.sample.robot.WsOutputs;
import org.wildstang.sample.robot.WsSubsystems;
import org.wildstang.sample.subsystems.targeting.WsPose;

public class Intake implements Subsystem{
    
    Launcher launcher;
    Turret turret;
    WsPose pose;

    private WsTalon intakeMotor, deployMotor;

    @Override
    public void inputUpdate(Input source) {
    }

    @Override
    public void init() {
        intakeMotor = (WsTalon) WsOutputs.INTAKE.get();
        deployMotor = (WsTalon) WsOutputs.INTAKE_DEPLOY.get();
    }

    @Override
    public void selfTest() {
    }

    @Override
    public void update() {
    }

    @Override
    public void resetState() {
    }

    @Override
    public void initSubsystems() {
        launcher = (Launcher) Core.getSubsystemManager().getSubsystem(WsSubsystems.LAUNCHER);
        turret = (Turret) Core.getSubsystemManager().getSubsystem(WsSubsystems.TURRET);
        pose = (WsPose) Core.getSubsystemManager().getSubsystem(WsSubsystems.WS_POSE);
    }

    @Override
    public String getName() {
        return "Intake";
    }
    
}
