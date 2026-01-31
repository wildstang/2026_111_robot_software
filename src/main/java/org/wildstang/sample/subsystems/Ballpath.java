package org.wildstang.sample.subsystems;

import org.wildstang.framework.core.Core;
import org.wildstang.framework.io.inputs.Input;
import org.wildstang.framework.subsystems.Subsystem;
import org.wildstang.hardware.roborio.inputs.WsJoystickAxis;
import org.wildstang.hardware.roborio.inputs.WsJoystickButton;
import org.wildstang.hardware.roborio.outputs.WsTalon;
import org.wildstang.sample.robot.WsInputs;
import org.wildstang.sample.robot.WsOutputs;
import org.wildstang.sample.robot.WsSubsystems;
import org.wildstang.sample.subsystems.targeting.WsPose;

public class Ballpath implements Subsystem{
    
    Launcher launcher;
    Turret turret;
    WsPose pose;

    private WsTalon ballpathMotor;
    private WsJoystickButton operatorA, driverLS, operatorLS, operatorRS;
    private WsJoystickAxis driverLT, operatorLT;



    @Override
    public void inputUpdate(Input source) {
        if(operatorA.getValue()){
            ballpathMotor.setSpeed(-0.2);
        }else if((driverLS.getValue() || (driverLT.getValue() > 0.5) || operatorLS.getValue()
        || operatorLT.getValue() > 0.5 || operatorRS.getValue()) && 
        turret.goodToFire() && launcher.goodToFire){
            ballpathMotor.setSpeed(1);
        }else{
            ballpathMotor.setSpeed(0);
        }
    }

    @Override
    public void init() {
        
        ballpathMotor = (WsTalon) WsOutputs.BALLPATH.get();

        operatorA = (WsJoystickButton) Core.getInputManager().getInput(WsInputs.OPERATOR_FACE_LEFT);
        operatorA.addInputListener(this);

        driverLS = (WsJoystickButton) Core.getInputManager().getInput(WsInputs.DRIVER_LEFT_SHOULDER);
        driverLS.addInputListener(this);

        driverLT = (WsJoystickAxis) Core.getInputManager().getInput(WsInputs.DRIVER_LEFT_TRIGGER);
        driverLT.addInputListener(this);

        operatorLS = (WsJoystickButton) Core.getInputManager().getInput(WsInputs.OPERATOR_LEFT_JOYSTICK_BUTTON);
        operatorLS.addInputListener(this);

        operatorLT = (WsJoystickAxis) Core.getInputManager().getInput(WsInputs.OPERATOR_LEFT_TRIGGER);
        operatorLT.addInputListener(this);

        operatorRS = (WsJoystickButton) Core.getInputManager().getInput(WsInputs.OPERATOR_RIGHT_JOYSTICK_BUTTON);
        operatorRS.addInputListener(this);

        launcher = (Launcher) Core.getSubsystemManager().getSubsystem(WsSubsystems.LAUNCHER);
        turret = (Turret) Core.getSubsystemManager().getSubsystem(WsSubsystems.TURRET);
        pose = (WsPose) Core.getSubsystemManager().getSubsystem(WsSubsystems.WS_POSE);

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
        return "Ballpath";
    }
    
    public void setMotorSpeed(double speed){
        ballpathMotor.setSpeed(speed);
    }
}
