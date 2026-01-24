package org.wildstang.sample.subsystems;

import org.wildstang.framework.core.Core;
import org.wildstang.framework.io.inputs.Input;
import org.wildstang.framework.subsystems.Subsystem;
import org.wildstang.hardware.roborio.outputs.WsTalon;
import org.wildstang.sample.robot.WsOutputs;
import org.wildstang.sample.robot.WsSubsystems;
import org.wildstang.sample.subsystems.targeting.WsPose;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Turret implements Subsystem{

    WsPose pose;
    
    public static enum GameStates {FIRING, HOMINGLOWER, HOMINGUPPER};
    private GameStates turretState;
    private double desiredTurretAngle;

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
        
        desiredTurretAngle = (double)pose.angleOfTurretNZone()[0];
        turretMotor.setPosition(rotateTurret(desiredTurretAngle));
        SmartDashboard.putString("Turrent State", turretState.name());
    }

    double rotateTurret(double desiredAngle){

        double actualAngle = 0;

        if(turretState == GameStates.FIRING){
            // doing logic to find out which level to go to (example: if you are above 360 and want to go to the lower coterminal angle)
            // if there is a coterminal angle, you decide whether to rotate ccw or cw
            if(desiredAngle <= 60){
                if((Math.abs(turretMotor.getPosition() - desiredAngle)) 
                    > (Math.abs(turretMotor.getPosition() - (desiredAngle+360)))){
                        actualAngle = desiredAngle + 360;
                }
            }else if((desiredAngle >= 360) && (desiredAngle <= 420)){
                if ((Math.abs(turretMotor.getPosition() - desiredAngle)) 
                    < (Math.abs(turretMotor.getPosition() - (desiredAngle+360)))){
                        actualAngle = Math.abs(desiredAngle-360);
                }
            }else{
                actualAngle = desiredAngle;
            }
        }else if(turretState == GameStates.HOMINGLOWER || turretState == GameStates.HOMINGUPPER){
            //determine which hardstop we are further from and go there (it gives us more wiggle room)
             if(desiredAngle < 30 && desiredAngle >= 0){
                //always rotate to higher coterminal
                actualAngle = desiredAngle + 360;
                
             }else if(desiredAngle >= 420 && desiredAngle < 390){
                //always rotate to lower coterminal
                actualAngle = Math.abs(desiredAngle - 360);
             }else{
             actualAngle = desiredAngle;
             }
        }

        return actualAngle;
    }


    public boolean goodToFire(){
        double wiggle = Math.abs(desiredTurretAngle - turretMotor.getPosition());
        if(wiggle > 1){
            return false;
        }
        return true;
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
