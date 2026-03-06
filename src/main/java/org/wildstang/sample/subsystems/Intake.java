package org.wildstang.sample.subsystems;

import org.wildstang.framework.core.Core;
import org.wildstang.framework.io.inputs.Input;
import org.wildstang.framework.subsystems.Subsystem;
import org.wildstang.hardware.roborio.inputs.WsJoystickAxis;
import org.wildstang.hardware.roborio.inputs.WsJoystickButton;
import org.wildstang.hardware.roborio.outputs.WsTalon;
import org.wildstang.sample.robot.WsInputs;
import org.wildstang.sample.robot.WsOutputs;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Intake implements Subsystem{
    
    private WsJoystickAxis leftTrigger;
    private WsJoystickAxis rightTrigger;
    private WsJoystickButton rightShoulder;

    private WsJoystickAxis operatorLeftTrigger;
    private WsJoystickAxis operatorRightTrigger;
    private WsJoystickButton operatorX;
    private WsJoystickButton operatorB;
    private WsJoystickButton operatorY;
    private WsJoystickButton operatorStart;

    private WsTalon intakeMotor, deployMotor;

    public enum DeployState {IN, OUT, INTAKING, STOWED, RESETTING};
    public enum IntakeState {INTAKING, NEUTRAL, REVERSE, SLOW};
    private DeployState deploy = DeployState.OUT;
    private IntakeState direction = IntakeState.NEUTRAL;
    private double deployStart;
    private boolean resetting = false;

    private Timer stowTimer = new Timer();


    @Override
    public void inputUpdate(Input source) {
        if (operatorStart.getValue()) {
            resetting = true;
            deploy = DeployState.RESETTING;
        }
        if (!operatorStart.getValue() && resetting){
            resetting = false;
            deployStart = deployMotor.getPosition();
            deploy = DeployState.OUT;
        }
        if(Math.abs(rightTrigger.getValue()) > 0.5 || rightShoulder.getValue()){
            direction = IntakeState.INTAKING;
        } else if((Math.abs(leftTrigger.getValue()) > 0.5 || Math.abs(operatorLeftTrigger.getValue()) > 0.5 ||
                Math.abs(operatorRightTrigger.getValue()) > 0.5)){
            direction = IntakeState.SLOW;
        } else if(operatorB.getValue()){
            direction = IntakeState.REVERSE;
        } else{
            direction = IntakeState.NEUTRAL;
        }
        
       if(operatorX.getValue()){
            deploy = DeployState.IN;
       } else if (Math.abs(rightTrigger.getValue()) > 0.5 || rightShoulder.getValue()){
            deploy = DeployState.INTAKING;
       } else if (Math.abs(leftTrigger.getValue()) > 0.5 || Math.abs(operatorLeftTrigger.getValue()) > 0.5 || 
                operatorY.getValue() || Math.abs(operatorRightTrigger.getValue()) > 0.5){
            deploy = DeployState.STOWED;
       } else if (deploy != DeployState.RESETTING) {
            deploy = DeployState.OUT;
       }
    }

    @Override
    public void init() {
        intakeMotor = (WsTalon) WsOutputs.INTAKE.get();
        deployMotor = (WsTalon) WsOutputs.INTAKE_DEPLOY.get();
        //we'll need to do some motor initializing here, intake_deploy PID and current limits for both of them
        deployMotor.initClosedLoop(2.0,0,0);
        // deployMotor.addClosedLoop(2.0, 0, 0);
        intakeMotor.enableFOC();
        intakeMotor.setCurrentLimit(70,70);
        deployMotor.setCurrentLimit(50,50);
        deployStart = deployMotor.getPosition();

        rightTrigger = (WsJoystickAxis) Core.getInputManager().getInput(WsInputs.DRIVER_RIGHT_TRIGGER);
        rightTrigger.addInputListener(this);
        leftTrigger = (WsJoystickAxis) Core.getInputManager().getInput(WsInputs.DRIVER_LEFT_TRIGGER);
        leftTrigger.addInputListener(this);
        rightShoulder = (WsJoystickButton) Core.getInputManager().getInput(WsInputs.DRIVER_RIGHT_SHOULDER);
        rightShoulder.addInputListener(this);
        operatorLeftTrigger = (WsJoystickAxis) Core.getInputManager().getInput(WsInputs.OPERATOR_LEFT_TRIGGER);
        operatorLeftTrigger.addInputListener(this);
        operatorRightTrigger = (WsJoystickAxis) WsInputs.OPERATOR_RIGHT_TRIGGER.get();
        operatorRightTrigger.addInputListener(this);
        operatorX = (WsJoystickButton) Core.getInputManager().getInput(WsInputs.OPERATOR_FACE_LEFT);
        operatorX.addInputListener(this);
        operatorB = (WsJoystickButton) Core.getInputManager().getInput(WsInputs.OPERATOR_FACE_RIGHT);
        operatorB.addInputListener(this);
        operatorY = (WsJoystickButton) WsInputs.OPERATOR_FACE_UP.get();
        operatorY.addInputListener(this);
        operatorStart = (WsJoystickButton) WsInputs.OPERATOR_START.get();
        operatorStart.addInputListener(this);

        stowTimer.start();
    }

    @Override
    public void selfTest() {
    }

    @Override
    public void update() {
        if(direction == IntakeState.INTAKING){
            intakeMotor.setSpeed(-1.0);
        }
        else if (direction == IntakeState.REVERSE){
            intakeMotor.setSpeed(1.0);
        }
        else if (direction == IntakeState.SLOW){
            intakeMotor.setSpeed(-0.35);
        }
        else{
           intakeMotor.setSpeed(0.0);
        }
        
        if(deploy == DeployState.IN){
            deployMotor.setPosition(deployStart);
            stowTimer.reset();
        }
        if(deploy == DeployState.OUT){
            // if (Math.abs(deployMotor.getPosition()-(deployStart+2.45))>0.05) deployMotor.setSpeed(0.1);
            // else deployMotor.setSpeed(0);
            deployMotor.setPosition(deployStart+2.45, 0);
            stowTimer.reset();
        }
        if(deploy == DeployState.STOWED){
            // if (Math.abs(deployMotor.getPosition()-(deployStart+2.45))>0.05) deployMotor.setSpeed(0.1);
            // else deployMotor.setSpeed(0);
            deployMotor.setPosition(Math.max(deployStart, deployStart+0.85-0.45*stowTimer.get()), 0);
        }
        if (deploy == DeployState.INTAKING){
            if (deployMotor.getPosition() < deployStart+2.4) deployMotor.setSpeed(1.0);
            else deployMotor.setSpeed(1.0);
            stowTimer.reset();
        }
        if (deploy == DeployState.RESETTING){
            deployMotor.setSpeed(-0.5);
        }
        SmartDashboard.putNumber("Intake Deploy position", deployMotor.getPosition());
        SmartDashboard.putNumber("Intake target", deploy != DeployState.IN ? deployStart+2.45 : deployStart);
        SmartDashboard.putString("Intake state", deploy.toString());
    }

    @Override
    public void resetState() {
        direction = IntakeState.NEUTRAL;
        deploy = DeployState.OUT;
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
