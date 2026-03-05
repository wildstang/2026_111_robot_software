package org.wildstang.sample.subsystems.LED;

import org.wildstang.framework.core.Core;
import org.wildstang.framework.io.inputs.Input;
import org.wildstang.framework.subsystems.Subsystem;
import org.wildstang.hardware.roborio.inputs.WsJoystickAxis;
import org.wildstang.sample.robot.CANConstants;
import org.wildstang.sample.robot.WsInputs;
import org.wildstang.sample.robot.WsSubsystems;
import org.wildstang.sample.subsystems.swerve.SwerveDrive;
import org.wildstang.sample.subsystems.targeting.WsPose;

import com.ctre.phoenix6.configs.CANdleConfiguration;
import com.ctre.phoenix6.controls.RainbowAnimation;
import com.ctre.phoenix6.controls.SingleFadeAnimation;
import com.ctre.phoenix6.controls.SolidColor;
import com.ctre.phoenix6.hardware.CANdle;
import com.ctre.phoenix6.signals.RGBWColor;
import com.ctre.phoenix6.signals.StatusLedWhenActiveValue;
import com.ctre.phoenix6.signals.StripTypeValue;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;


public class LedController implements Subsystem {

    private WsPose pose;
    private CANdle led;
    private CANdleConfiguration config = new CANdleConfiguration();

    private WsJoystickAxis driverLY;

    private RainbowAnimation rainbow = new RainbowAnimation(0, 62);
    private SingleFadeAnimation fade = new SingleFadeAnimation(0, 62);
    private SolidColor color = new SolidColor(0, 62);

    private RGBWColor red = new RGBWColor(255,0,0);
    private RGBWColor blue = new RGBWColor(0,0,255);
    private RGBWColor green = new RGBWColor(0,255,0);
    private RGBWColor cyan = new RGBWColor(0, 255, 255);

    private boolean isAuto = true;
    private double FOM = 0.0;

    private String gameData = "";
    private boolean blueWonAuto = false;
    private double matchTime = 0;
    private double shiftTime = 0;
    private boolean ourHubActive = true;

    @Override
    public void update(){
        if (Core.isBlue() && isAuto){
            led.setControl(fade.withColor(blue));
        } else if (isAuto){
            led.setControl(fade.withColor(red));
        } else if (FOM > 1.0){
            led.setControl(color.withColor(red));
        } else if (ourHubActive){
            led.setControl(color.withColor(green));
        } else {
            led.setControl(rainbow);
        }
        
        displayMatchInfo();
    }

    @Override
    public void inputUpdate(Input source) {
        isAuto = false;
    }

    @Override
    public void initSubsystems() {    
        pose = (WsPose) Core.getSubsystemManager().getSubsystem(WsSubsystems.WS_POSE);
    }

    @Override
    public void init() {
        driverLY = (WsJoystickAxis) WsInputs.DRIVER_LEFT_JOYSTICK_VERTICAL.get();
        driverLY.addInputListener(this);
        
        //Outputs
        led = new CANdle(CANConstants.CANDLE, "drive_canivore");
        config.LED.StripType = StripTypeValue.GRB;
        config.LED.BrightnessScalar = 1.0;
        config.CANdleFeatures.StatusLedWhenActive = StatusLedWhenActiveValue.Disabled;
        led.getConfigurator().apply(config);
    }

    @Override
    public void selfTest() {
    }

    @Override
    public void resetState() {
    }

    @Override
    public String getName() {
        return "Led Controller";
    }

    public void startAuto(){
        isAuto = false;
    }
    public void displayMatchInfo(){
        matchTime = DriverStation.getMatchTime();
        gameData = DriverStation.getGameSpecificMessage();
        if (gameData.length() > 0){
            if (gameData.charAt(0) == 'R') blueWonAuto = false;
            if (gameData.charAt(0) == 'B') blueWonAuto = true;
        }
        if (matchTime > 130) {//both
            shiftTime = matchTime - 130;
            ourHubActive = true;
        } else if (matchTime > 105) {//auto loser
            shiftTime = matchTime - 105;
            ourHubActive = (Core.isBlue() ^ blueWonAuto);
        } else if (matchTime > 80) {//auto winner
            shiftTime = matchTime - 80;
            ourHubActive = !(Core.isBlue() ^ blueWonAuto);
        } else if (matchTime > 55) {//auto loser
            shiftTime = matchTime - 55;
            ourHubActive = (Core.isBlue() ^ blueWonAuto);
        } else if (matchTime > 30) {//auto winner
            shiftTime = matchTime - 30;
            ourHubActive = !(Core.isBlue() ^ blueWonAuto);
        } else {
            shiftTime = matchTime;//endgame
            ourHubActive = true;
        }

        SmartDashboard.putString("# who won auto", gameData.length() < 1 ? "No Data" : blueWonAuto ? "Blue" : "Red");
        SmartDashboard.putNumber("# Match Time", matchTime);
        SmartDashboard.putNumber("# Shift Time", shiftTime);
        SmartDashboard.putBoolean("# our hub active", ourHubActive);
    }
    public void setFOM(double newFOM){
        this.FOM = newFOM;
    }
}
