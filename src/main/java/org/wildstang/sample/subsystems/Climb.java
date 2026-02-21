package org.wildstang.sample.subsystems;

import org.wildstang.framework.io.inputs.Input;
import org.wildstang.framework.subsystems.Subsystem;
import org.wildstang.hardware.roborio.outputs.WsTalon;
import org.wildstang.sample.robot.WsOutputs;

public class Climb implements Subsystem{

    private WsTalon climb1Motor, climb2Motor;
    
    @Override
    public void inputUpdate(Input source) {
    }

    @Override
    public void init() {
        climb1Motor = (WsTalon) WsOutputs.CLIMB1.get();
        // climb2Motor = (WsTalon) WsOutputs.CLIMB2.get();
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
    }

    @Override
    public String getName() {
        return "Climb";
    }
    
}
