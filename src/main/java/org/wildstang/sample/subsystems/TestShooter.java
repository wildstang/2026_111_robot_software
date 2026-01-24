package org.wildstang.sample.subsystems;

import org.wildstang.framework.core.Core;
import org.wildstang.framework.io.inputs.Input;
import org.wildstang.framework.subsystems.Subsystem;
import org.wildstang.hardware.roborio.inputs.WsJoystickAxis;
import org.wildstang.hardware.roborio.inputs.WsJoystickButton;
import org.wildstang.hardware.roborio.outputs.WsTalon;
import org.wildstang.sample.robot.WsInputs;
import org.wildstang.sample.robot.WsOutputs;

public class TestShooter implements Subsystem{


    WsTalon flywheelMotor;
    WsTalon ballpathMotor;
    WsTalon turretMotor;
    WsTalon hoodMotor;

    WsJoystickAxis operatorLT, operatorRT;
    WsJoystickButton operatorY, operatorA, operatorB, operatorX, DpadUp, DpadDown, DpadRight, DpadLeft, operatorLB, operatorRB;

    double shooterSpeed;

    @Override
    public void init() {
        operatorA = (WsJoystickButton) Core.getInputManager().getInput(WsInputs.OPERATOR_FACE_DOWN);
        operatorA.addInputListener(this);

        operatorY = (WsJoystickButton) Core.getInputManager().getInput(WsInputs.OPERATOR_FACE_UP);
        operatorY.addInputListener(this);

        operatorB = (WsJoystickButton) Core.getInputManager().getInput(WsInputs.OPERATOR_FACE_RIGHT);
        operatorB.addInputListener(this);

        operatorX = (WsJoystickButton) Core.getInputManager().getInput(WsInputs.OPERATOR_FACE_LEFT);
        operatorX.addInputListener(this);

        operatorLT = (WsJoystickAxis) Core.getInputManager().getInput(WsInputs.OPERATOR_LEFT_TRIGGER);
        operatorLT.addInputListener(this);

        operatorRT = (WsJoystickAxis) Core.getInputManager().getInput(WsInputs.OPERATOR_RIGHT_TRIGGER);
        operatorRT.addInputListener(this);

        DpadUp = (WsJoystickButton) Core.getInputManager().getInput(WsInputs.OPERATOR_DPAD_UP);
        DpadUp.addInputListener(this);

        DpadLeft = (WsJoystickButton) Core.getInputManager().getInput(WsInputs.OPERATOR_DPAD_LEFT);
        DpadLeft.addInputListener(this);

        DpadRight = (WsJoystickButton) Core.getInputManager().getInput(WsInputs.OPERATOR_DPAD_RIGHT);
        DpadRight.addInputListener(this);

        DpadDown = (WsJoystickButton) Core.getInputManager().getInput(WsInputs.OPERATOR_DPAD_DOWN);
        DpadDown.addInputListener(this);

        operatorLB = (WsJoystickButton) Core.getInputManager().getInput(WsInputs.OPERATOR_LEFT_SHOULDER);
        operatorLB.addInputListener(this);

        operatorRB = (WsJoystickButton) Core.getInputManager().getInput(WsInputs.OPERATOR_RIGHT_SHOULDER);
        operatorRB.addInputListener(this);

        shooterSpeed = 0;

        flywheelMotor = (WsTalon) WsOutputs.FLYWHEEL.get();
        flywheelMotor.setCurrentLimit(120, 70);

        ballpathMotor = (WsTalon) WsOutputs.BALLPATH.get();
        turretMotor = (WsTalon) WsOutputs.TURRET.get();

        hoodMotor = (WsTalon) WsOutputs.HOOD.get();
        hoodMotor.setCurrentLimit(50,50);
        

    }

    @Override
    public void inputUpdate(Input source) {
        if(operatorLT.getValue()> 0.5){
            flywheelMotor.setSpeed(shooterSpeed);
        }else if(operatorRT.getValue() > 0.5){
            ballpathMotor.setSpeed(1);
        }else if(operatorY.getValue()){
            hoodMotor.setSpeed(0.1);
        }else if(operatorA.getValue()){
            hoodMotor.setSpeed(-0.1);
        }else if(operatorB.getValue()){
            hoodMotor.setSpeed(0.5);
        }else if(operatorX.getValue()){
            hoodMotor.setSpeed(-0.5);
        }else if(DpadUp.getValue()){
            shooterSpeed += 0.0025;
        }else if(DpadDown.getValue()){
            shooterSpeed -= 0.0025;
        }else if(DpadRight.getValue()){
            shooterSpeed += 0.025;
        }else if(DpadLeft.getValue()){
            shooterSpeed -= 0.025;
        }else if(operatorLB.getValue()){
            turretMotor.setSpeed(0.1);
        }else if(operatorRB.getValue()){
            turretMotor.setSpeed(-0.1);
        }else{
            flywheelMotor.setSpeed(0);
            hoodMotor.setSpeed(0);
            turretMotor.setSpeed(0);
            ballpathMotor.setSpeed(0);
        }
    }

    @Override
    public void selfTest() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'selfTest'");
    }

    @Override
    public void update() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'update'");
    }

    @Override
    public void resetState() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'resetState'");
    }

    @Override
    public void initSubsystems() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'initSubsystems'");
    }

    @Override
    public String getName() {
        return "Test Shooter";
    }


}
