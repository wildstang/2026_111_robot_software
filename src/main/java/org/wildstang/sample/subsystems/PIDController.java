package org.wildstang.sample.subsystems;

import org.wildstang.framework.logger.Log;

import edu.wpi.first.wpilibj.Timer;

public class PIDController {
    private double kpP, kpI; // Position Gains
    private double kvP; // Velecity Gains

    private double vP;
    private double pP, pI;
    private double tPrevious;
    private double maxIntegral;


    public PIDController(double kpP, double kpI, double kvP, double maxIntegral){
       this.kpP = kpP;
       this.kpI = kpI;
       this.kvP = kvP;
       this.tPrevious = Timer.getFPGATimestamp();
       this.maxIntegral = maxIntegral; 
    }

    public PIDController(double kvP){
        this.kvP = kvP;
    }

    public PIDController(double kpP, double kpI, double maxIntegral){
        this.kpP = kpP;
        this.kpI = kpI;

        this.tPrevious = Timer.getFPGATimestamp();
        this.maxIntegral = maxIntegral; 
    }

    private double getDeltaT(){
        double t = Timer.getFPGATimestamp();
        double dt = t - tPrevious;
        tPrevious = t;
        return dt;
    }

    public double positionPVal(double setPoint, double currentPos){
        double error = setPoint - currentPos;
        pP = error*kpP;
        return pP;
    }

    public double positionIVal(double setPoint, double currentPos){
        double dt = getDeltaT();
        double error = setPoint - currentPos;
        pI += kpI*error * (dt);

        if(pI > maxIntegral){
            pI = maxIntegral;
        }else if(pI < -maxIntegral){
            pI = -maxIntegral;
        }

        return pI;
    }

    public double positionPIController(double setPoint, double currentPos){
        double pTerm = positionPVal(setPoint, currentPos);
        double iTerm = positionIVal(setPoint, currentPos);
        return pTerm + iTerm;
    }

    public double velocityPController(double setPoint, double currentVel){
        double pTerm = velocityPVal(setPoint, currentVel);
        return pTerm;
    }


    public double velocityPVal(double setPoint, double currentVel){
        double error = setPoint - currentVel;
        vP = error*kvP;
        return vP;
    }

    public void resetIVal(){
        Log.warn("Reset integral");
        pI = 0;
    }
}
