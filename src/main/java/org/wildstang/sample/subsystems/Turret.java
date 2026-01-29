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
    
    //I think we only need one homing state
    public static enum GameStates {FIRING, HOMING};
    public GameStates turretState;
    public double desiredTurretAngle;

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
        desiredTurretAngle = pose.fromFieldToRobotAngle();
        turretMotor.setPosition(rotateTurret());
        SmartDashboard.putString("Turrent State", turretState.name());
    }

    public double rotateTurret(){
        //note that desiredAngle will probably only be 0-360
        //this seems to assume it'll be up to 420
        double actualAngle = 0;

        if(turretState == GameStates.FIRING){
            // doing logic to find out which level to go to (example: if you are above 360 and want to go to the lower coterminal angle)
            // if there is a coterminal angle, you decide whether to rotate ccw or cw
            //this will apply to both cases - see if actual angle being in the 360-420
            //range is better, and then do the +360
            if(desiredTurretAngle <= 60){
                if((Math.abs(turretMotor.getPosition() - desiredTurretAngle)) 
                    > (Math.abs(turretMotor.getPosition() - (desiredTurretAngle+360)))){
                        actualAngle = desiredTurretAngle + 360;
                }else{
                        actualAngle = desiredTurretAngle;
                }    
            
            }else{
                actualAngle = desiredTurretAngle;
            }
        }else if(turretState == GameStates.HOMING){
            //determine which hardstop we are further from and go there (it gives us more wiggle room)
             if(desiredTurretAngle < 30 && desiredTurretAngle >= 0){
                //always rotate to higher coterminal
                actualAngle = desiredTurretAngle + 360;
             }
                //this needs something to handle a "between values"
             else{
                actualAngle = desiredTurretAngle;
             }
                //won't need this
             
                //always rotate to lower coterminal
                
                //before this, we should check for if we're in 30-60 or 360-390 that we go
                //the close direction
                actualAngle = desiredTurretAngle;
             
        }

        return actualAngle;
    }


    public boolean goodToFire(){
        //good
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
