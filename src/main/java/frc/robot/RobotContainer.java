// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import java.util.List;

import com.pathplanner.lib.PathPlanner;
import com.swervedrivespecialties.swervelib.SdsModuleConfigurations;

import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.controller.ProfiledPIDController;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.math.kinematics.SwerveDriveKinematics;
import edu.wpi.first.math.trajectory.Trajectory;
import edu.wpi.first.math.trajectory.TrajectoryConfig;
import edu.wpi.first.math.trajectory.TrajectoryGenerator;
import edu.wpi.first.math.trajectory.TrapezoidProfile;
import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.GenericHID;
// import frc.robot.commands.PrototypeTestCommand;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.PneumaticsModuleType;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import edu.wpi.first.wpilibj2.command.WaitCommand;
import edu.wpi.first.wpilibj2.command.button.Button;
import edu.wpi.first.wpilibj2.command.button.JoystickButton;
import edu.wpi.first.wpilibj2.command.ParallelCommandGroup;
import frc.robot.Constants.Drivetrain;
import frc.robot.commands.DefaultDriveCommand;
import frc.robot.commands.RotateDegrees;
// import frc.robot.commands.DefaultDriveCommand;
import frc.robot.commands.ShooterCommand;
import frc.robot.commands.SwerveControllerStrafe;
import frc.robot.commands.Hang.HangDownCommand;
import frc.robot.commands.Hang.HangUpCommand;
import frc.robot.commands.IntakeFeeder.DeployIntakeCommand;
import frc.robot.commands.IntakeFeeder.RunFeederCommand;
import frc.robot.subsystems.ColorSensorSystem;
import frc.robot.subsystems.DrivetrainSubsystem;
import frc.robot.subsystems.FeederSubsystem;
import frc.robot.subsystems.HangSubsystem;
import frc.robot.subsystems.FeederSubsystem.FeederState;
import frc.robot.subsystems.IntakeSubsystem;
import frc.robot.subsystems.IntakeSubsystem.IntakeState;
import frc.robot.subsystems.ShooterSubsystem.ShooterState;
// import frc.robot.subsystems.ShooterSubsystem.ShooterState;
import frc.robot.subsystems.ShooterSubsystem;
import frc.robot.subsystems.VisionSubsystem;
/**
 * This class is where the bulk of the robot should be declared. Since Command-based is a
 * "declarative" paradigm, very little robot logic should actually be handled in the {@link Robot}
 * periodic methods (other than the scheduler calls). Instead, the structure of the robot (including
 * subsystems, commands, and button mappings) should be declared here.
 */
public class RobotContainer {
  // The robot's subsystems and commands are defined here...
  public final SendableChooser<String> m_autoSwitcher = new SendableChooser<String>();
  public static final String kAutoR1 = "1 Ball";
  public static final String kAutoR2 = "2 Ball";
  public static final String kAutoR3 = "Back Path";
  public String m_autoSelected;


  private final DrivetrainSubsystem m_drivetrainSubsystem;
  private final VisionSubsystem m_visionSubsystem;
  private final ColorSensorSystem m_ColorSensorSystem;
  private final ShooterSubsystem m_shooterSubsystem;
  private final FeederSubsystem m_feeder;
  private final IntakeSubsystem m_intake;
  private final HangSubsystem m_hangSubsystem;



  public static final double pi = Math.PI;
  private final XboxController m_controller = new XboxController(0);
  private final Compressor m_compressor;
  // private final Joystick driverVertical, driverHorizontal;
  

  public static final double MAX_VELOCITY_METERS_PER_SECOND = (6380.0 / 60.0 *
          SdsModuleConfigurations.MK4_L2.getDriveReduction() *
          SdsModuleConfigurations.MK4_L2.getWheelDiameter() * Math.PI);

  public static final double MAX_ANGULAR_VELOCITY_RADIANS_PER_SECOND = MAX_VELOCITY_METERS_PER_SECOND /
          Math.hypot(Drivetrain.DRIVETRAIN_TRACKWIDTH_METERS / 2.0, Drivetrain.DRIVETRAIN_WHEELBASE_METERS / 2.0);

  private final SwerveDriveKinematics m_kinematics = new SwerveDriveKinematics(
      // Front left
      new Translation2d(Drivetrain.DRIVETRAIN_TRACKWIDTH_METERS / 2.0, Drivetrain.DRIVETRAIN_WHEELBASE_METERS / 2.0),
      // Front right
      new Translation2d(Drivetrain.DRIVETRAIN_TRACKWIDTH_METERS / 2.0, -Drivetrain.DRIVETRAIN_WHEELBASE_METERS / 2.0),
      // Back left
      new Translation2d(-Drivetrain.DRIVETRAIN_TRACKWIDTH_METERS / 2.0, Drivetrain.DRIVETRAIN_WHEELBASE_METERS / 2.0),
      // Back right
      new Translation2d(-Drivetrain.DRIVETRAIN_TRACKWIDTH_METERS / 2.0, -Drivetrain.DRIVETRAIN_WHEELBASE_METERS / 2.0)
    );

  /**
   * The container for the robot. Contains subsystems, OI devices, and commands.
   */
  public RobotContainer() {

    // new SequentialCommandGroup(
    //     new WaitCommand(1),
    //     new InstantCommand(() -> m_drivetrainSubsystem.zeroGyroscope()),
    //     new InstantCommand(() -> m_drivetrainSubsystem.drive(new ChassisSpeeds(0.0, 0.0, 0.0))),
    //     new InstantCommand(() -> m_drivetrainSubsystem.resetOdometry(new Pose2d()))
    // ).schedule();
    m_drivetrainSubsystem = new DrivetrainSubsystem();
    m_visionSubsystem = new VisionSubsystem();
    m_ColorSensorSystem = new ColorSensorSystem();
    m_shooterSubsystem = new ShooterSubsystem();
    m_feeder = new FeederSubsystem();
    m_intake = new IntakeSubsystem();
    m_hangSubsystem = new HangSubsystem();

    m_drivetrainSubsystem.zeroGyroscope();
    m_drivetrainSubsystem.drive(new ChassisSpeeds(0.0, 0.0, 0.0));
    m_drivetrainSubsystem.resetOdometry(new Pose2d());

    m_autoSwitcher.addOption(kAutoR1, kAutoR1);
    m_autoSwitcher.addOption(kAutoR2, kAutoR2);
    m_autoSwitcher.addOption(kAutoR3, kAutoR3);


  SmartDashboard.putData(m_autoSwitcher);

  // driverVertical = new Joystick(Constants.OIConstants.JOYSTICK_DRIVE_VERTICAL); //send
  // driverHorizontal = new Joystick(Constants.OIConstants.JOYSTICK_DRIVE_HORIZONTAL);

  m_drivetrainSubsystem.setDefaultCommand(new DefaultDriveCommand(
          m_drivetrainSubsystem,
          () -> modifyAxis(m_controller.getLeftX())/2 * DrivetrainSubsystem.MAX_VELOCITY_METERS_PER_SECOND,
          () -> -modifyAxis(m_controller.getLeftY())/2 * DrivetrainSubsystem.MAX_VELOCITY_METERS_PER_SECOND,
          () -> modifyAxis(m_controller.getRightX()) * DrivetrainSubsystem.MAX_ANGULAR_VELOCITY_RADIANS_PER_SECOND
  ));
    // m_drivetrainSubsystem.setDefaultCommand(new DefaultDriveCommand(
    //         m_drivetrainSubsystem,
    //         () -> modifyAxis(driverVertical.getX())/2 * DrivetrainSubsystem.MAX_VELOCITY_METERS_PER_SECOND,
    //         () -> modifyAxis(driverVertical.getY())/2 * DrivetrainSubsystem.MAX_VELOCITY_METERS_PER_SECOND,
    //         () -> -modifyAxis(driverHorizontal.getX()) * DrivetrainSubsystem.MAX_ANGULAR_VELOCITY_RADIANS_PER_SECOND
    // ));




    
    m_compressor = new Compressor(2, PneumaticsModuleType.REVPH);
    m_compressor.disable();
    // //Configure the button bindings
    configureButtonBindings();
  }

  /**
   * Use this method to define your button->command mappings. Buttons can be created by
   * instantiating a {@link GenericHID} or one of its subclasses ({@link
   * edu.wpi.first.wpilibj.Joystick} or {@link XboxController}), and then passing it to a {@link
   * edu.wpi.first.wpilibj2.command.button.JoystickButton}.
   */
  private void configureButtonBindings() {


    new Button(m_controller::getBButton).whileActiveOnce(
      // new DeployIntakeCommand(m_intake, IntakeState.DOWN)
      new RunFeederCommand(m_feeder, FeederState.INTAKE, 1.0, 0.4)
    );
    // new JoystickButton(driverHorizontal, 2).whileActiveOnce(
    //   // new DeployIntakeCommand(m_intake, IntakeState.DOWN)
    //   new RunFeederCommand(m_feeder, FeederState.INTAKE, 1.0, 0.4)
    // );

    // new JoystickButton(driverVertical, 3).whileActiveOnce(
    //   // new DeployIntakeCommand(m_intake, IntakeState.DOWN)
    //   new FeederCommand(m_feeder, FeederState.OUTTAKE, 0.5, 0.3)
    // );
    // new JoystickButton(driverVertical, 3).whileActiveOnce(
    //   // new DeployIntakeCommand(m_intake, IntakeState.DOWN)
    //   new FeederCommand(m_feeder, FeederState.OUTTAKE, 0.5, 0.3)
    // );

    // new JoystickButton(driverVertical, 4).whileHeld(new ShooterCommand(m_shooterSubsystem, m_visionSubsystem, ShooterState.ZONE_2));
    // new JoystickButton(driverHorizontal, 1).whileHeld(
    //   // new DeployIntakeCommand(m_intake, IntakeState.DOWN)
    //   new FeederCommand(m_feeder, FeederState.INTAKE, 0.5, 0.5) 
    // );

    // new JoystickButton(driverVertical, 1).whenInactive(
    //   // new DeployIntakeCommand(m_intake, IntakeState.UP)
    //   new FeederCommand(m_feeder, FeederState.IDLE, 0.0, 0.0)
    // );
    // new JoystickButton(driverHorizontal, 1).whenInactive(
    //   // new DeployIntakeCommand(m_intake, IntakeState.UP)
    //   new FeederCommand(m_feeder, FeederState.IDLE, 0.0, 0.0)
    // );

    // new Button(m_controller::getAButtonPressed).whenPressed(command)

    new Button(m_controller::getAButtonPressed).whenPressed(
      new SequentialCommandGroup(
        new RotateDegrees(m_drivetrainSubsystem, m_visionSubsystem)
      )
    );

    // Back button zeros the gyroscope
    new Button(m_controller::getYButtonPressed).whenPressed(
      m_drivetrainSubsystem::zeroGyroscope
    );
  }

  public void updateRobotState() {
    RobotState.intakeState = m_intake.updateIntakeState();
    RobotState.feederState = m_feeder.updateFeederState();
    // RobotState.shooterState = m_shooter.updateShooterState();
    // RobotState.visionState = m_vision.updateVisionState();
    SmartDashboard.putNumber("VISION: Distance", m_visionSubsystem.getDistance());
    SmartDashboard.putNumber("VISION: Angle", m_visionSubsystem.getRawAngle());
    SmartDashboard.putString("HANG: limit switch right ", m_hangSubsystem.hangRightLimitSwitch.get() + "");
    SmartDashboard.putString("HANG: limit switch left", m_hangSubsystem.hangLeftLimitSwitch.get() + "");
    SmartDashboard.putString("HANG: encoder left ", m_hangSubsystem.encoderLeft.getSelectedSensorPosition() + "");
    SmartDashboard.putString("HANG: encoder right ", m_hangSubsystem.encoderRight.getSelectedSensorPosition() + "");
  }

  private static double deadband(double value, double deadband) {
    if (Math.abs(value) > deadband) {
      if (value > 0.0) {
        return (value - deadband) / (1.0 - deadband);
      } else {
        return (value + deadband) / (1.0 - deadband);
      }
    } else {
      return 0.0;
    }
  }

  private static double modifyAxis(double value) {
    // Deadband
    value = deadband(value, 0.05);

    // Square the axis
    value = Math.copySign(value * value, value);

    return value;
  }



  public Command getTestCommand(){
    // return new RunFeederCommand(m_feeder, FeederState.INTAKE, 1.0, 0.4);

    // return new ShooterCommand(m_feeder, m_shooterSubsystem);

    return new RotateDegrees(m_drivetrainSubsystem, m_visionSubsystem);

    //code hang on first rung
    // return new SequentialCommandGroup(
    //   new HangUpCommand(m_hangSubsystem, 0.1),
    //   new HangDownCommand(m_hangSubsystem, 0.1)
    // );
  }


  /**
   * Use this to pass the autonomous command to the main {@link Robot} class.
   *
   * @return the command to run in autonomous
   */
  public Command getAutonomousCommand() {
    // An ExampleCommand will run in autonomous
    m_autoSelected = m_autoSwitcher.getSelected();
    String autoSelect = "";
    switch (m_autoSelected){ //1 ball path
      case kAutoR1:
          autoSelect = kAutoR1;
        // System.out.println("Unamed_0 works");
        break;
      case kAutoR2:
        autoSelect = kAutoR2;
        // System.out.println("Unamed works");
        break;
      case kAutoR3:
       autoSelect  = kAutoR3;
        // System.out.println("Unamed_1 works");
        break;

      default:
        break;
    }

    // TrajectoryConfig trajectoryConfig = new TrajectoryConfig(MAX_VELOCITY_METERS_PER_SECOND, 0.5).setKinematics(m_kinematics);
    TrajectoryConfig trajectoryConfig = new TrajectoryConfig(2, 0.7).setKinematics(m_kinematics);
    Trajectory trajectory;
    if(autoSelect.equals("1 Ball")){

      // trajectory = TrajectoryGenerator.generateTrajectory(
      //   new Pose2d(0,0, new Rotation2d(0)),
      //   List.of(
      //     new Translation2d(-1,0)
      //   ),
      //   new Pose2d(-1,0, new Rotation2d(0)),
      //   trajectoryConfig
      // );
      trajectory = PathPlanner.loadPath("New Path", 2, 0.7); //change paths
    }
    else if(autoSelect.equals("2 Ball")){
      //  trajectory = TrajectoryGenerator.generateTrajectory(
      //   new Pose2d(0,0, new Rotation2d(0)),
      //   List.of(
      //     new Translation2d(0,-0.5),
      //     new Translation2d(0.5,-0.5),
      //     new Translation2d(0,-0.5),
      //     new Translation2d(-0.5,-0.5)

      //   ),
      //   new Pose2d(-0.75, -0.75, new Rotation2d(0)),
      //   trajectoryConfig
      // );
      trajectory = PathPlanner.loadPath("New New Path", 2, 0.7); //change paths
    }
    else if(autoSelect.equals("Back Path")){
      //  trajectory = TrajectoryGenerator.generateTrajectory(
      //   new Pose2d(0,0, Rotation2d.fromDegrees(0)),
      //   List.of(
      //     // new Translation2d(0,-0.1),
      //      new Translation2d(0,-0.75)
      //     // new Translation2d(0,-1.4)
      //   ),
      //   new Pose2d(0, -1.5, Rotation2d.fromDegrees(0)),
      //   trajectoryConfig
      // );
      Trajectory examplePath = TrajectoryGenerator.generateTrajectory(
          new Pose2d(0,0, Rotation2d.fromDegrees(0)),
          List.of(
            
           
            
          ),
          new Pose2d(0, -1, Rotation2d.fromDegrees(0)),
          trajectoryConfig
        );
       Trajectory exampleTrajectory = PathPlanner.loadPath("New Path", 2, 0.7); //change paths
      trajectory = examplePath.concatenate(exampleTrajectory);
    }
    else
    {
      //  trajectory = TrajectoryGenerator.generateTrajectory(
      //   new Pose2d(0, 0, new Rotation2d(0)),
      //   List.of(
      //     new Translation2d(-1, 0)
      //   ),
      //   new Pose2d(-1.5, 0, new Rotation2d(0)),
      //   trajectoryConfig
      //);
       trajectory = PathPlanner.loadPath("New Path", 3, 1.5); //change paths
    }
   

    PIDController xController = new PIDController(Constants.Drivetrain.kPXController, Constants.Drivetrain.kIXController, 0); //FIXME
    PIDController yController = new PIDController(Constants.Drivetrain.kPYController, Constants.Drivetrain.kIYController, 0);//FIXME
    ProfiledPIDController thetaController = new ProfiledPIDController(
          Constants.Drivetrain.kPThetaControllerTrajectory, 0, 0, new TrapezoidProfile.Constraints(  //FIXME
              MAX_ANGULAR_VELOCITY_RADIANS_PER_SECOND,
              MAX_ANGULAR_VELOCITY_RADIANS_PER_SECOND/3));
    
    thetaController.enableContinuousInput(-Math.PI, Math.PI);
 

   

    //SwerveControllerStrafe
    SwerveControllerStrafe swerveTrajectoryFollower = new SwerveControllerStrafe(
      trajectory, //change to just trajectory
      m_drivetrainSubsystem::getPose,
      m_kinematics,
      xController,
      yController,
      thetaController,
      m_drivetrainSubsystem::setModuleStates,
      m_drivetrainSubsystem
    );
    
    RotateDegrees rotateDegrees = new RotateDegrees(m_drivetrainSubsystem, m_visionSubsystem);





    return new SequentialCommandGroup(
      new WaitCommand(1.5),
      new InstantCommand(() -> m_drivetrainSubsystem.zeroGyroscope()),
      // new InstantCommand(() -> m_drivetrainSubsystem.drive(new ChassisSpeeds(-0.5, 0.0, 0.0))),
       new InstantCommand(() -> m_drivetrainSubsystem.resetOdometry(trajectory.getInitialPose())),
      // new RotateDegrees(m_drivetrainSubsystem, m_visionSubsystem),z
      swerveTrajectoryFollower.withTimeout(5)
      // new RotateDegrees(m_drivetrainSubsystem, m_visionSubsystem)
    );
  }
}
