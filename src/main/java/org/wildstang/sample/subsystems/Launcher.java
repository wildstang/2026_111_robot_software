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

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Launcher implements Subsystem{
    
    WsPose pose;

    private WsTalon flywheelMotor, hoodMotor;

    private WsJoystickAxis driverLeftTrigger, operatorRightTrigger, operatorLeftTrigger;


    private enum GameStates {SHOOT, FEED, STOW};
    private GameStates currentState = GameStates.STOW;

    public boolean inFeedingZone;
    private Timer shootTimer = new Timer();

    
    /* Flywheel Stuff */
    private double flywheelVel = 0.0;
    private double flywheelActual = 0.0;
    private final double flywheelTolerance = 1.0;


    /* Hood Stuff */
    private double hoodPos = 0.0;
    private double hoodActual = 0.0;
    private final double hoodTolerance = 0.2;
    private final double HOODMAX = 6.0;
    private final double HOODMIN = 0.0;
    double hoodStart = 0;

    @Override
    public void init() {
        flywheelMotor = (WsTalon) WsOutputs.FLYWHEEL.get();
        // flywheelMotor.enableFOC();
        flywheelMotor.initClosedLoop(0.05,0.0,0.0, 0.0, 0.01);
        flywheelMotor.setCurrentLimit(120, 70);

        hoodMotor = (WsTalon) WsOutputs.HOOD.get();
        hoodMotor.setCurrentLimit(50,50);

        driverLeftTrigger = (WsJoystickAxis) Core.getInputManager().getInput(WsInputs.DRIVER_LEFT_TRIGGER);
        driverLeftTrigger.addInputListener(this);

        operatorRightTrigger = (WsJoystickAxis) Core.getInputManager().getInput(WsInputs.OPERATOR_RIGHT_TRIGGER);
        operatorRightTrigger.addInputListener(this);

        operatorLeftTrigger = (WsJoystickAxis) Core.getInputManager().getInput(WsInputs.OPERATOR_LEFT_TRIGGER);
        operatorLeftTrigger.addInputListener(this);

        hoodStart = hoodMotor.getPosition();

        shootTimer.start();
    }

    @Override
    public void inputUpdate(Input source) {
       
        if(Math.abs(driverLeftTrigger.getValue()) > 0.5 || Math.abs(operatorRightTrigger.getValue()) > 0.5){
            currentState = GameStates.SHOOT;
        } else if (Math.abs(operatorLeftTrigger.getValue()) > 0.5){
            currentState = GameStates.FEED;
        } else{
            currentState = GameStates.STOW;
        }

    }

    @Override
    public void update() {
        
        switch (currentState){
            case SHOOT:
              
                flywheelVel = pose.inAllianceZone() ? pose.getFlywheelShootVelocity() : 0.0;
                hoodPos = pose.getHoodShootPosition();
                shootTimer.reset();
                break;

            case FEED:

                flywheelVel = pose.inAllianceZone() ? 0.0 :pose.getFlywheelFeedVelocity();
                hoodPos = HOODMAX;
                shootTimer.reset();
                break;

            case STOW:
                flywheelVel = !shootTimer.hasElapsed(1.0) ? flywheelVel: 0.0;
                if (pose.inAllianceZone()) hoodPos = pose.getHoodShootPosition();
                else hoodPos = HOODMAX;
                break;
        }
        if (flywheelVel != 0.0) flywheelMotor.setVelocity(flywheelVel);
        else flywheelMotor.setSpeed(0);
        setHood(hoodPos);
        flywheelActual = flywheelMotor.getVelocity();
        hoodActual = hoodMotor.getPosition() - hoodStart;
        SmartDashboard.putString("Launcher state", currentState.toString());
        SmartDashboard.putNumber("Launcher velocity", flywheelActual);
        SmartDashboard.putNumber("Launcher target velocity", flywheelVel);
        SmartDashboard.putNumber("Launcher hood pos", hoodActual);
        SmartDashboard.putNumber("Launcher hood target", hoodPos);
        SmartDashboard.putBoolean("Launcher ready to fire", goodToFire());
    }

    public boolean goodToFire(){

        if (flywheelVel == 0.0) return false;
        if (Math.abs(hoodPos - hoodActual) > hoodTolerance) return false;
        if (Math.abs(flywheelVel - flywheelActual) > flywheelTolerance) return false;
        return true;
        
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
    private void setHood(double hoodPos){
        double targetNew = 0;
        if (hoodPos > HOODMAX) targetNew = hoodStart+HOODMAX;
        else if (hoodPos < HOODMIN) targetNew = hoodStart+HOODMIN;
        else targetNew = hoodPos+hoodStart;
        double error = targetNew - hoodMotor.getPosition();
        if (error > 1) hoodMotor.setSpeed(0.3);
        if (error < -1) hoodMotor.setSpeed(-0.1);
        else hoodMotor.setSpeed(error*0.05 + 0.028*Math.signum(error));
    }
    public boolean isActive(){
        return !(currentState == GameStates.STOW);
    }
    public boolean isScoring(){
        return currentState == GameStates.SHOOT;
    }
    
}
