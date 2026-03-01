package org.wildstang.sample.subsystems;

import org.wildstang.framework.io.inputs.Input;
import org.wildstang.framework.subsystems.Subsystem;
import org.wildstang.hardware.roborio.inputs.WsDPadButton;
import org.wildstang.hardware.roborio.outputs.WsTalon;
import org.wildstang.sample.robot.WsInputs;
import org.wildstang.sample.robot.WsOutputs;

public class Climb implements Subsystem{

   private WsTalon climb1Motor;
    private WsDPadButton dpadUp, dpadDown;
    private enum ClimbState {UP, DOWN};
    private ClimbState climbState = ClimbState.DOWN;
    private double climbUpSpeed = 1;
    private double climbDownSpeed = -1;//probably should be -1 for now
    
    
    @Override
    public void inputUpdate(Input source) {
        if(dpadUp.getValue()){
            climbState = ClimbState.UP;
        }
         if(dpadDown.getValue()){
            climbState = ClimbState.DOWN;
        }
    }

    @Override
    public void init() {
        climb1Motor = (WsTalon) WsOutputs.CLIMB1.get();
        climb1Motor.setBrake();
        climb1Motor.initClosedLoop(12, 0, 0);
        climb1Motor.setCurrentLimit(20,70);

        dpadUp = (WsDPadButton) WsInputs.DRIVER_DPAD_UP.get();
        dpadUp.addInputListener(null);
        dpadDown = (WsDPadButton) WsInputs.DRIVER_DPAD_DOWN.get();
        dpadDown.addInputListener(null);

        
    }

    @Override
    public void selfTest() {
    }

    @Override
    public void update() {
        if(climbState == ClimbState.UP){
            //this works for testing
            //we'll eventually change this to use .setPosition(target),
            //with an upper and lower target instead of manual
            climb1Motor.setSpeed(climbUpSpeed);
        }
        else{
            climb1Motor.setSpeed(climbDownSpeed);
        }
    }

    @Override
    public void resetState() {
    }

    @Override
    public void initSubsystems() {
    }

    public void setClimbState(ClimbState state){
        climbState = state;
    }

    @Override
    public String getName() {
        return "Climb";
    }
    
    
}
