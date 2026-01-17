package org.wildstang.sample.subsystems;

import org.wildstang.framework.core.Core;
import org.wildstang.framework.io.inputs.Input;
import org.wildstang.framework.subsystems.Subsystem;
import org.wildstang.hardware.roborio.inputs.WsDigitalInput;
import org.wildstang.hardware.roborio.outputs.WsTalon;
import org.wildstang.sample.robot.WsInputs;
import org.wildstang.sample.robot.WsOutputs;
import org.wildstang.sample.robot.WsSubsystems;
import org.wildstang.sample.subsystems.targeting.WsPose;

public class Launcher implements Subsystem{
    
    WsPose pose;

    private WsTalon flywheelMotor, hoodMotor;

    private WsDigitalInput driverLeftTrigger, driverLeftStickButton, operatorRightTrigger, operatorLeftTrigger, operatorLeftStickButton, operatorRightStickButton;

    private enum GameStates {SHOOT, FEED, STOW};
    private GameStates currentState;

    public boolean inFeedingZone;

    private double flywheelVelocity;
    private double desiredFlywheelVel;
    private PIDController flywheelPID;
    private double flywheelPGain;
    private double flywheelVelTolerance;


    private double hoodPosition;
    private double desiredHoodPos;
    private PIDController hoodPID;
    private double hoodPGain, hoodIGain, maxIntegralValue;
    private double hoodPositionTolerance;

    public boolean goodToFire;


   

    @Override
    public void init() {
        flywheelMotor = (WsTalon) WsOutputs.FLYWHEEL.get();
        hoodMotor = (WsTalon) WsOutputs.HOOD.get();

        driverLeftStickButton = (WsDigitalInput) Core.getInputManager().getInput(WsInputs.DRIVER_LEFT_JOYSTICK_BUTTON);
        driverLeftStickButton.addInputListener(this);

        driverLeftTrigger = (WsDigitalInput) Core.getInputManager().getInput(WsInputs.DRIVER_LEFT_TRIGGER);
        driverLeftTrigger.addInputListener(this);

        operatorRightTrigger = (WsDigitalInput) Core.getInputManager().getInput(WsInputs.OPERATOR_RIGHT_TRIGGER);
        operatorRightTrigger.addInputListener(this);

        operatorLeftTrigger = (WsDigitalInput) Core.getInputManager().getInput(WsInputs.OPERATOR_LEFT_TRIGGER);
        operatorLeftTrigger.addInputListener(this);

        operatorLeftStickButton = (WsDigitalInput) Core.getInputManager().getInput(WsInputs.OPERATOR_LEFT_JOYSTICK_BUTTON);
        operatorLeftStickButton.addInputListener(this);

        operatorRightStickButton = (WsDigitalInput) Core.getInputManager().getInput(WsInputs.OPERATOR_RIGHT_JOYSTICK_BUTTON);
        operatorRightStickButton.addInputListener(this);

        flywheelPID = new PIDController(flywheelPGain);
        hoodPID = new PIDController(hoodPGain, hoodIGain, maxIntegralValue);

        flywheelVelTolerance = 0;
        hoodPositionTolerance = 0;

    }

    @Override
    public void inputUpdate(Input source) {
        if(source.equals(driverLeftTrigger) || (source.equals(driverLeftStickButton) && !inFeedingZone) || source.equals(operatorRightTrigger)){
            currentState = GameStates.SHOOT;
        }
        else if(source.equals(driverLeftStickButton) && inFeedingZone) || source.equals(operatorLeftStickButton) || source.equals(operatorLeftTrigger) || source.equals(operatorRightStickButton)){
            currentState = GameStates.FEED;
        }
        else{
            currentState = GameStates.STOW;
        }

    }

    @Override
    public void selfTest() {
    }

    @Override
    public void update() {
        switch (currentState){
            case SHOOT:
                flywheelVelocity = flywheelPID.velocityPVal(desiredFlywheelVel, flywheelMotor.getVelocity());
                hoodPosition = hoodPID.positionPIController(desiredHoodPos, hoodMotor.getPosition()*2*Math.PI);

                if((((flywheelVelocity >= (flywheelVelocity - flywheelVelTolerance)) 
                    && (flywheelVelocity <= (flywheelVelocity + flywheelVelTolerance))) 
                        && ((hoodPosition >= (hoodPosition-hoodPositionTolerance)) 
                            && (hoodPosition <= (hoodPosition+hoodPositionTolerance))))){
                                goodToFire = true;
                            }

                break;

            case FEED:

                break;

            case STOW:
                goodToFire = false;


                break;
        }
    }

    @Override
    public void resetState() {
    }

    @Override
    public void initSubsystems() {
        pose = (WsPose) Core.getSubsystemManager().getSubsystem(WsSubsystems.WS_POSE);
    }

    @Override
    public String getName() {
        return "Launcher";
    }
    
}
