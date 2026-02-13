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

public class Launcher implements Subsystem{
    
    WsPose pose;

    private WsTalon flywheelMotor, hoodMotor;

    private WsJoystickAxis driverLeftTrigger, operatorRightTrigger, operatorLeftTrigger;
    private WsJoystickButton operatorLeftBumper, operatorRightBumper;


    private enum GameStates {SHOOT, FEED, STOW};
    private GameStates currentState = GameStates.STOW;

    public boolean inFeedingZone;

    
    /* Flywheel Stuff */
    private double flywheelVelocity;
    private double desiredFlywheelShootVel;
    private double desiredFlywheelFeedVel;
   
    private double flywheelPGain, flywheelIGain, flywheelDGain;

    private double flywheelShootVelTolerance;
    private double flywheelFeedVelTolerance;


    /* Hood Stuff */
    private double hoodPosition;
    private double desiredHoodShootPos;
    private double desiredHoodFeedPos;

    private double hoodPGain, hoodIGain, hoodDGain;

    private double hoodShootPositionTolerance;
    private double hoodFeedPositionTolerance;


    public boolean goodToFire;


   

    @Override
    public void init() {
        flywheelMotor = (WsTalon) WsOutputs.FLYWHEEL.get();
        flywheelMotor.initClosedLoop(flywheelPGain, flywheelIGain, flywheelDGain);
        flywheelMotor.setCurrentLimit(70, 70);

        hoodMotor = (WsTalon) WsOutputs.HOOD.get();
        hoodMotor.initClosedLoop(hoodPGain, hoodIGain, hoodDGain);
        hoodMotor.setCurrentLimit(50,50);

        driverLeftTrigger = (WsJoystickAxis) Core.getInputManager().getInput(WsInputs.DRIVER_LEFT_TRIGGER);
        driverLeftTrigger.addInputListener(this);

        operatorRightTrigger = (WsJoystickAxis) Core.getInputManager().getInput(WsInputs.OPERATOR_RIGHT_TRIGGER);
        operatorRightTrigger.addInputListener(this);

        operatorLeftTrigger = (WsJoystickAxis) Core.getInputManager().getInput(WsInputs.OPERATOR_LEFT_TRIGGER);
        operatorLeftTrigger.addInputListener(this);

        operatorLeftBumper = (WsJoystickButton) Core.getInputManager().getInput(WsInputs.OPERATOR_LEFT_SHOULDER);
        operatorLeftBumper.addInputListener(this);

        operatorRightBumper = (WsJoystickButton) Core.getInputManager().getInput(WsInputs.OPERATOR_RIGHT_SHOULDER);
        operatorRightBumper.addInputListener(this);

        flywheelShootVelTolerance = 0;
        hoodShootPositionTolerance = 0;
        flywheelFeedVelTolerance = 0;
        hoodFeedPositionTolerance = 0;

    }

    @Override
    public void inputUpdate(Input source) {
       
        if(((Math.abs(driverLeftTrigger.getValue()) > 0.5))  
                || (Math.abs(operatorRightTrigger.getValue()) > 0.5)){
            currentState = GameStates.SHOOT;
        }
        else if(operatorLeftBumper.getValue() 
                || (Math.abs(operatorLeftTrigger.getValue()) > 0.5) 
                    || operatorRightBumper.getValue()){
            currentState = GameStates.FEED;
        }
        else{
            currentState = GameStates.STOW;
        }

    }

    @Override
    public void update() {
        
        switch (currentState){
            case SHOOT:
              
                desiredFlywheelShootVel = pose.getFlywheelShootVelocity();
                desiredHoodShootPos = pose.getHoodShootPosition();

                flywheelMotor.setVelocity(desiredFlywheelShootVel);
                hoodMotor.setPosition(desiredHoodShootPos);

                goodToFire = isGoodToFire(flywheelVelocity, hoodPosition);
                            

                break;

            case FEED:

                desiredFlywheelFeedVel = pose.getFlywheelFeedVelocity();
                desiredHoodFeedPos = pose.getHoodFeedPosition();

                flywheelMotor.setVelocity(desiredFlywheelFeedVel);
                hoodMotor.setPosition(desiredHoodFeedPos);

                goodToFire = isGoodToFire(flywheelVelocity, hoodPosition);

                break;

            case STOW:
                
                goodToFire = false;
                flywheelMotor.setSpeed(0.0);
                hoodMotor.setPosition(0.0);

                break;
        }
        SmartDashboard.putString("Launcher state", currentState.toString());
        SmartDashboard.putNumber("Launcher velocity", flywheelMotor.getVelocity());
        SmartDashboard.putNumber("Launcher target velocity", currentState == GameStates.SHOOT ? desiredFlywheelShootVel
            : currentState == GameStates.FEED ? desiredFlywheelFeedVel : 0.0);
        SmartDashboard.putNumber("Launcher hood pos", hoodMotor.getPosition());
        SmartDashboard.putNumber("Launcher hood target", currentState == GameStates.SHOOT ? desiredHoodShootPos
            : currentState == GameStates.FEED ? desiredHoodFeedPos : 0.0);
        SmartDashboard.putBoolean("Launcher ready to fire", goodToFire);
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
    public void selfTest() {
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
