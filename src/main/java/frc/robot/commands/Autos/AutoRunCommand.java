package frc.robot.commands.Autos;

import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.Constants;
import frc.robot.subsystems.DrivetrainSubsystem;

public class AutoRunCommand extends CommandBase {
    private final DrivetrainSubsystem m_drivetrainSubsystem;
    private double m_velocityX;
    private double m_velocityY;
    private double m_DeltaTheta;
    private double m_initialAngle;
    private final PIDController pid;
    public double pidValue = 0;

    public AutoRunCommand(DrivetrainSubsystem drivetrainSubsystem, double velocityX, double velocityY, double deltaTheta) {
        this.m_drivetrainSubsystem = drivetrainSubsystem;
        this.m_velocityX = velocityX;
        this.m_velocityY = velocityX;
        this.m_DeltaTheta = deltaTheta;
        this.m_initialAngle = m_drivetrainSubsystem.getNavHeading();

        pid = new PIDController(Constants.Drivetrain.kPThetaController+.02, Constants.Drivetrain.kIThetaController, 0);
        
        addRequirements(drivetrainSubsystem);
    }

    @Override
    public void execute() {
        double currentAngle = m_drivetrainSubsystem.getNavHeading();
        double diff = m_initialAngle-currentAngle;
        pidValue = pid.calculate(diff, m_DeltaTheta);

        SmartDashboard.putNumber("PID Valuee", pidValue);
        m_drivetrainSubsystem.drive(new ChassisSpeeds(m_velocityX, m_velocityY, pidValue));
    }

    @Override
    public void end(boolean interrupted) {
        m_drivetrainSubsystem.drive(new ChassisSpeeds(0.0, 0.0, 0.0));
    }
}