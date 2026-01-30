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

public class Intake implements Subsystem{
    
    Launcher launcher;
    Turret turret;
    WsPose pose;
    private WsJoystickAxis leftTrigger;
    private WsJoystickAxis rightTrigger;
    private WsJoystickButton leftShoulder;
    private WsJoystickButton rightShoulder;

    private WsJoystickAxis operatorLeftTrigger;
    private WsJoystickAxis operatorRightTrigger;
    private WsJoystickButton operatorLeftShoulder;
    private WsJoystickButton operatorRightShoulder;
    private WsJoystickButton operatorX;
    private WsJoystickButton operatorB;

    private WsTalon intakeMotor, deployMotor;

    private enum DeployState {IN, OUT, STOWED};
    private enum IntakeState {INTAKING, NEUTRAL, REVERSE};
    private DeployState deploy = DeployState.IN;
    private IntakeState direction = IntakeState.NEUTRAL;

    
   /*  private boolean forward;
    private boolean backwards;
    private boolean in;
    private boolean out;
    private boolean stowed; */


    @Override
    public void inputUpdate(Input source) {
        if(Math.abs(rightTrigger.getValue()) > 0 || rightShoulder.getValue()){
            //forward = true;
            //backwards = false;
            direction = IntakeState.INTAKING;
        }
        /* else{
            forward = false;
            backwards = false;
        } */
       //I think we'll eventually want this to be running slower than the above intake, something like 0.25
       //So this will be a fourth IntakeState
        else if((Math.abs(leftTrigger.getValue()) > 0 || Math.abs(operatorLeftTrigger.getValue()) > 0 || 
                operatorLeftShoulder.getValue() || operatorRightShoulder.getValue()) && (goodToFire())){
            // forward = true;
            // backwards = false;
            direction = IntakeState.INTAKING;
        }
                
          /*   else{forward=false; backwards = false;} */
        else if(operatorB.getValue()){
           // backwards = true;
           // forward = false;
           //I think you want REVERSE below
           direction = IntakeState.INTAKING;
        }  
        else{
            direction = IntakeState.NEUTRAL;
        }
       // else{forward = false;backwards = false;}
        
       if(operatorX.getValue()){
            //in = true;
            //out = false;
            //stowed = false;
            deploy = DeployState.IN;
        }  
     
        if(Math.abs(rightTrigger.getValue()) > 0 || rightShoulder.getValue()){
            //out = true;
            //in = false;
            //stowed = false;
            deploy = DeployState.OUT;
        }
        else if(Math.abs(leftTrigger.getValue()) > 0){
            //stowed = true;
            //in = false;
            //out = false;
            deploy = DeployState.STOWED;
        }

    }

    @Override
    public void init() {
        intakeMotor = (WsTalon) WsOutputs.INTAKE.get();
        deployMotor = (WsTalon) WsOutputs.INTAKE_DEPLOY.get();
        //we'll need to do some motor initializing here, intake_deploy PID and current limits for both of them

        rightTrigger = (WsJoystickAxis) Core.getInputManager().getInput(WsInputs.DRIVER_RIGHT_TRIGGER);
        rightTrigger.addInputListener(this);
        leftTrigger = (WsJoystickAxis) Core.getInputManager().getInput(WsInputs.DRIVER_LEFT_TRIGGER);
        leftTrigger.addInputListener(this);
        rightShoulder = (WsJoystickButton) Core.getInputManager().getInput(WsInputs.DRIVER_RIGHT_SHOULDER);
        rightShoulder.addInputListener(this);
        leftShoulder = (WsJoystickButton) Core.getInputManager().getInput(WsInputs.DRIVER_LEFT_SHOULDER);
        leftShoulder.addInputListener(this);
        
        operatorRightTrigger = (WsJoystickAxis) Core.getInputManager().getInput(WsInputs.OPERATOR_RIGHT_TRIGGER);
        operatorRightTrigger.addInputListener(this);
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
        launcher = (Launcher) Core.getSubsystemManager().getSubsystem(WsSubsystems.LAUNCHER);
        turret = (Turret) Core.getSubsystemManager().getSubsystem(WsSubsystems.TURRET);
        pose = (WsPose) Core.getSubsystemManager().getSubsystem(WsSubsystems.WS_POSE);
    }

    @Override
    public String getName() {
        return "Intake";
    }
    public boolean goodToFire(){
        //we'll eventually replace with goodToFire methods from launcher, turret, and pose
        return true;
    }
    
}
