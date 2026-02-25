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

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Ballpath implements Subsystem{
    
    Launcher launcher;
    Turret turret;
    WsPose pose;

    private WsTalon ballpathMotor;
    private WsJoystickButton operatorA;
    private WsJoystickAxis driverLT, operatorLT, operatorRT;

    public enum GameState {FIRING, READYING, REVERSE, STOP}
    private GameState state = GameState.STOP;



    @Override
    public void inputUpdate(Input source) {
        if(operatorA.getValue()){
            state = GameState.REVERSE;
        }else if(Math.abs(driverLT.getValue()) > 0.5 || Math.abs(operatorRT.getValue()) > 0.5
        || Math.abs(operatorLT.getValue()) > 0.5){
            if (state != GameState.FIRING) state = GameState.READYING;
        }else{
            state = GameState.STOP;
        }
    }

    @Override
    public void init() {
        
        ballpathMotor = (WsTalon) WsOutputs.BALLPATH.get();
        ballpathMotor.enableFOC();
        ballpathMotor.setCurrentLimit(70, 70);

        operatorA = (WsJoystickButton) Core.getInputManager().getInput(WsInputs.OPERATOR_FACE_DOWN);
        operatorA.addInputListener(this);

        driverLT = (WsJoystickAxis) Core.getInputManager().getInput(WsInputs.DRIVER_LEFT_TRIGGER);
        driverLT.addInputListener(this);


        operatorLT = (WsJoystickAxis) Core.getInputManager().getInput(WsInputs.OPERATOR_LEFT_TRIGGER);
        operatorLT.addInputListener(this);

        operatorRT = (WsJoystickAxis) WsInputs.OPERATOR_RIGHT_TRIGGER.get();
        operatorRT.addInputListener(this);

        launcher = (Launcher) Core.getSubsystemManager().getSubsystem(WsSubsystems.LAUNCHER);
        turret = (Turret) Core.getSubsystemManager().getSubsystem(WsSubsystems.TURRET);
        pose = (WsPose) Core.getSubsystemManager().getSubsystem(WsSubsystems.WS_POSE);

    }

    @Override
    public void selfTest() {
    }

    @Override
    public void update() {
        if (state == GameState.READYING){
            if (turret.goodToFire() && launcher.goodToFire() && pose.goodToFire()) {
                ballpathMotor.setSpeed(1.0);
                state = GameState.FIRING;
            }
            else ballpathMotor.setSpeed(0);
        } else if (state == GameState.FIRING){
            if (turret.goodToFire() && pose.goodToFire()) ballpathMotor.setSpeed(1.0);
            else ballpathMotor.setSpeed(0.0);
        } else if (state == GameState.REVERSE){
            ballpathMotor.setSpeed(-0.2);
        } else if (state == GameState.STOP){
            ballpathMotor.setSpeed(0);
        }
        
        SmartDashboard.putString("Ballpath state", state.toString());
        SmartDashboard.putNumber("Ballpath current", ballpathMotor.getCurret());
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

    public void setBallpathState(GameState currentState){
        state = currentState;
    }
    
    public void setMotorSpeed(double speed){
        ballpathMotor.setSpeed(speed);
    }
}
