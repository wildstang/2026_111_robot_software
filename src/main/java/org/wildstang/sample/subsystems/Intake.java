package org.wildstang.sample.subsystems;

import org.wildstang.framework.core.Core;
import org.wildstang.framework.io.inputs.Input;
import org.wildstang.framework.subsystems.Subsystem;
import org.wildstang.hardware.roborio.inputs.WsJoystickAxis;
import org.wildstang.hardware.roborio.inputs.WsJoystickButton;
import org.wildstang.hardware.roborio.outputs.WsTalon;
import org.wildstang.sample.robot.WsInputs;
import org.wildstang.sample.robot.WsOutputs;

public class Intake implements Subsystem{
    
    private WsJoystickAxis leftTrigger;
    private WsJoystickAxis rightTrigger;
    private WsJoystickButton rightShoulder;

    private WsJoystickAxis operatorLeftTrigger;
    private WsJoystickButton operatorLeftShoulder;
    private WsJoystickButton operatorRightShoulder;
    private WsJoystickButton operatorX;
    private WsJoystickButton operatorB;

    private WsTalon intakeMotor, deployMotor;

    public enum DeployState {IN, OUT, STOWED};
    public enum IntakeState {INTAKING, NEUTRAL, REVERSE, SLOW};
    private DeployState deploy = DeployState.IN;
    private IntakeState direction = IntakeState.NEUTRAL;


    @Override
    public void inputUpdate(Input source) {
        if(Math.abs(rightTrigger.getValue()) > 0 || rightShoulder.getValue()){
            direction = IntakeState.INTAKING;
        } else if((Math.abs(leftTrigger.getValue()) > 0 || Math.abs(operatorLeftTrigger.getValue()) > 0 || 
                operatorLeftShoulder.getValue() || operatorRightShoulder.getValue())){
            direction = IntakeState.SLOW;
        } else if(operatorB.getValue()){
            direction = IntakeState.REVERSE;
        } else{
            direction = IntakeState.NEUTRAL;
        }
        
       if(operatorX.getValue()){
            deploy = DeployState.IN;
        }  else if(Math.abs(rightTrigger.getValue()) > 0 || rightShoulder.getValue()){
            deploy = DeployState.OUT;
        } else {
            deploy = DeployState.STOWED;
        }

    }

    @Override
    public void init() {
        intakeMotor = (WsTalon) WsOutputs.INTAKE.get();
        deployMotor = (WsTalon) WsOutputs.INTAKE_DEPLOY.get();
        //we'll need to do some motor initializing here, intake_deploy PID and current limits for both of them
        deployMotor.initClosedLoop(0.1,0,0);
        intakeMotor.enableFOC();
        intakeMotor.setCurrentLimit(70,70);
        deployMotor.setCurrentLimit(40,40);

        rightTrigger = (WsJoystickAxis) Core.getInputManager().getInput(WsInputs.DRIVER_RIGHT_TRIGGER);
        rightTrigger.addInputListener(this);
        leftTrigger = (WsJoystickAxis) Core.getInputManager().getInput(WsInputs.DRIVER_LEFT_TRIGGER);
        leftTrigger.addInputListener(this);
        rightShoulder = (WsJoystickButton) Core.getInputManager().getInput(WsInputs.DRIVER_RIGHT_SHOULDER);
        rightShoulder.addInputListener(this);
        operatorLeftTrigger = (WsJoystickAxis) Core.getInputManager().getInput(WsInputs.OPERATOR_LEFT_TRIGGER);
        operatorLeftTrigger.addInputListener(this);
        operatorRightShoulder = (WsJoystickButton) Core.getInputManager().getInput(WsInputs.OPERATOR_RIGHT_SHOULDER);
        operatorRightShoulder.addInputListener(this);
        operatorLeftShoulder = (WsJoystickButton) Core.getInputManager().getInput(WsInputs.OPERATOR_LEFT_SHOULDER);
        operatorLeftShoulder.addInputListener(this);
        operatorX = (WsJoystickButton) Core.getInputManager().getInput(WsInputs.OPERATOR_FACE_LEFT);
        operatorX.addInputListener(this);
        operatorB = (WsJoystickButton) Core.getInputManager().getInput(WsInputs.OPERATOR_FACE_RIGHT);
        operatorB.addInputListener(this);
    }

    @Override
    public void selfTest() {
    }

    @Override
    public void update() {
        if(direction == IntakeState.INTAKING){
            intakeMotor.setSpeed(1.0);
        }
        else if (direction == IntakeState.REVERSE){
            intakeMotor.setSpeed(-1.0);
        }
        else if (direction == IntakeState.SLOW){
            intakeMotor.setSpeed(0.15);
        }
        else{
           intakeMotor.setSpeed(0.0);
        }
        
        if(deploy == DeployState.IN){
            deployMotor.setPosition(-1.0);
        }
        if(deploy == DeployState.OUT){
            deployMotor.setPosition(0.0);
        }
        if(deploy == DeployState.STOWED){
            deployMotor.setPosition(-0.5);
        }
    }

    @Override
    public void resetState() {
    }

    @Override
    public void initSubsystems() {
    }

    @Override
    public String getName() {
        return "Intake";
    }

   public void setDeployState(DeployState currState){
        deploy = currState;
   }

   public void setIntakeState(IntakeState currentState){
        direction = currentState;
   }    
}
