package org.wildstang.sample.subsystems;

import org.wildstang.framework.core.Core;
import org.wildstang.framework.io.inputs.Input;
import org.wildstang.framework.subsystems.Subsystem;
import org.wildstang.hardware.roborio.outputs.WsTalon;
import org.wildstang.sample.robot.WsOutputs;
import org.wildstang.sample.robot.WsSubsystems;
import org.wildstang.sample.subsystems.targeting.WsPose;

import edu.wpi.first.math.geometry.Pose2d;

public class Turret implements Subsystem{

    WsPose pose;
    
    public static enum GameStates {FIRING, HOMINGLOWER, HOMINGUPPER};
    private GameStates turretState;
    private double turretAngle;

    private WsTalon turretMotor;

    @Override
    public void inputUpdate(Input source) {

    }

    @Override
    public void init() {
        turretMotor = (WsTalon) WsOutputs.TURRET.get();
    }

    @Override
    public void selfTest() {
    }

    @Override
    public void update() {
        turretState = (GameStates)pose.angleOfTurretNZone()[1];
        

        switch(turretState){
            case FIRING:
                turretAngle = (double)pose.angleOfTurretNZone()[0];
            break;

            case HOMINGLOWER:
                turretAngle = (double)pose.angleOfTurretNZone()[0];
            break;

            case HOMINGUPPER:
                turretAngle = (double)pose.angleOfTurretNZone()[0];
            break;
        }
    }

    void rotateTurret(){
        if(turretState == GameStates.FIRING){
            if(turretAngle )
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
        return "Turret";
    }
    
}
