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

    
    /* Flywheel Stuff */
    private double flywheelVelocity;
    private double desiredFlywheelShootVel;
    private double desiredFlywheelFeedVel;
    private PIDController flywheelPID;
    private double flywheelPGain;
    private double flywheelShootVelTolerance;
    private double flywheelFeedVelTolerance;


    /* Hood Stuff */
    private double hoodPosition;
    private double desiredHoodShootPos;
    private double desiredHoodFeedPos;
    private PIDController hoodPID;
    private double hoodPGain, hoodIGain, maxIntegralValue;
    private double hoodShootPositionTolerance;
    private double hoodFeedPositionTolerance;

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

        flywheelShootVelTolerance = 0;
        hoodShootPositionTolerance = 0;
        flywheelFeedVelTolerance = 0;
        hoodFeedPositionTolerance = 0;

    }

    @Override
    public void inputUpdate(Input source) {
        if(source.equals(driverLeftTrigger) || (source.equals(driverLeftStickButton) && !inFeedingZone) || source.equals(operatorRightTrigger)){
            currentState = GameStates.SHOOT;
        }
        else if(source.equals(driverLeftStickButton) && inFeedingZone || source.equals(operatorLeftStickButton) || source.equals(operatorLeftTrigger) || source.equals(operatorRightStickButton)){
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
                
                flywheelVelocity = flywheelPID.velocityPVal(desiredFlywheelShootVel, flywheelMotor.getVelocity());
                hoodPosition = hoodPID.positionPIController(desiredHoodShootPos, hoodMotor.getPosition()*2*Math.PI);

                goodToFire = isGoodToFire(flywheelVelocity, hoodPosition);
                            

                break;

            case FEED:

                flywheelVelocity = flywheelPID.velocityPVal(desiredFlywheelFeedVel, flywheelMotor.getVelocity());
                hoodPosition = hoodPID.positionPIController(desiredHoodFeedPos, hoodMotor.getPosition()*2*Math.PI);

                goodToFire = isGoodToFire(flywheelVelocity, hoodPosition);

                break;

            case STOW:
                
                goodToFire = false;

                break;
        }
    }

    private boolean isGoodToFire(double flywheelVel, double hoodPos){

        boolean flywheelGood = false;
        boolean hoodPosGood = false;

        if(currentState.equals(GameStates.SHOOT)){
            double[] actualFlywheelVel = {flywheelVel - flywheelShootVelTolerance, flywheelVel + flywheelShootVelTolerance};
            double[] actualHoodPosition = {hoodPos - hoodShootPositionTolerance, hoodPos + hoodShootPositionTolerance};

            
            if(flywheelVel >= actualFlywheelVel[0] && flywheelVel <= actualFlywheelVel[1]){
                flywheelGood = true;
            }else{
                flywheelGood = false;
            }
            if(hoodPos >= actualHoodPosition[0] && hoodPos <= actualHoodPosition[1]){
                hoodPosGood = true;
            }else{
                hoodPosGood = false;
            }

        
        }else if(currentState.equals(GameStates.FEED)){
            double[] actualFlywheelVel = {flywheelVel - flywheelFeedVelTolerance, flywheelVel + flywheelFeedVelTolerance};
            double[] actualHoodPosition = {hoodPos - hoodFeedPositionTolerance, hoodPos + hoodFeedPositionTolerance};

            
            if(flywheelVel >= actualFlywheelVel[0] && flywheelVel <= actualFlywheelVel[1]){
                flywheelGood = true;
            }else{
                flywheelGood = false;
            }
            if(hoodPos >= actualHoodPosition[0] && hoodPos <= actualHoodPosition[1]){
                hoodPosGood = true;
            }else{
                hoodPosGood = false;
            }
        }

        return flywheelGood && hoodPosGood;
        
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
