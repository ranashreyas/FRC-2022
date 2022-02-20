// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonFX;
import com.kauailabs.navx.frc.AHRS;
import edu.wpi.first.wpilibj.SPI;
import edu.wpi.first.wpilibj.Solenoid;
//import edu.wpi.first.wpilibj.motorcontrol.Spark;
import edu.wpi.first.wpilibj.motorcontrol.Talon;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import com.ctre.phoenix.motorcontrol.can.TalonSRX;

import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.PneumaticsModuleType;
import static edu.wpi.first.wpilibj.DoubleSolenoid.Value.*;

import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;

public class Intake extends SubsystemBase {
    // private TalonSRX motor1Falcon;
    // private TalonSRX motor2Falcon;
    private Solenoid rightValve;
    private Solenoid leftValve;

    public static enum IntakeState {
        TEST,
        RUN
    }

    public Intake() {
        // motor1 = new Talon(34);

        // motor1Falcon = new TalonSRX(Constants.IntakeConstants.RIGHT_MOTOR); // Right
        // motor
        // motor2Falcon = new TalonSRX(Constants.IntakeConstants.LEFT_MOTOR); // Left
        // motor
        // motor2Falcon.setInverted(true); // The left motor is inverted

        rightValve = new Solenoid(2, PneumaticsModuleType.REVPH, 7); // PAREMETERS: ???, foward channel, reverse channel
        leftValve = new Solenoid(2, PneumaticsModuleType.REVPH, 6); // PAREMETERS: ???, foward channel, reverse channel
    }

    @Override
    public void periodic() {}


    public void deploy(boolean status) {
        if (status) { // moves the piston out if the status is true (intake down)
            rightValve.set(true);
            leftValve.set(true);
        } else { // moves the piston in if the status is false (intake up)
            rightValve.set(false);
            leftValve.set(false);
        }
    }

    public void stopAll() {
        // motor1Falcon.set(ControlMode.PercentOutput, 0);
        // motor2Falcon.set(ControlMode.PercentOutput, 0);
        rightValve.set(false);
        leftValve.set(false);
    }

    public void diagnostics() {
        String leftPistonStatus = "Left Piston Status";
        String rightPistonStatus = "Right Piston Status";
        String compressorStatus = "Compressor Status";

        try {
            deploy(true);
            if (rightValve.get()) {
                SmartDashboard.putString(rightPistonStatus, "Success");
            } else
                SmartDashboard.putString(rightPistonStatus, "Failed");
        } catch (Exception e) {
            SmartDashboard.putString(rightPistonStatus, "Failed");
            SmartDashboard.putString(compressorStatus, "Failed");
        }

        try {
        deploy(true);
        if (leftValve.get()) {
        SmartDashboard.putString(leftPistonStatus, "Success");
        } else
        SmartDashboard.putString(leftPistonStatus, "Failed");
        } catch (Exception e) {
        SmartDashboard.putString(leftPistonStatus, "Failed");
        }
    }
}