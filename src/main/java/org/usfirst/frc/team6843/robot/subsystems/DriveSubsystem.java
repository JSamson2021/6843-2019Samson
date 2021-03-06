/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package org.usfirst.frc.team6843.robot.subsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import com.kauailabs.navx.frc.AHRS;

import org.usfirst.frc.team6843.robot.RobotMap;
import org.usfirst.frc.team6843.robot.commands.JoystickTankDrive;

import edu.wpi.first.wpilibj.PIDController;
import edu.wpi.first.wpilibj.PIDOutput;
import edu.wpi.first.wpilibj.PIDSource;
import edu.wpi.first.wpilibj.PIDSourceType;
import edu.wpi.first.wpilibj.SPI;
import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * The drive subsystem uses the Talon on board PID to control the speed of all
 * driving. It uses on RIO PID controllers to track headings and distances.
 */
public class DriveSubsystem extends Subsystem {
	/** Suggested rotate in place velocity base for PID output math. */
	public static final double ROTATE_VELOCITY_BASE = 1000.0;

	/** Holds the last PID calculated turn rate. */
	private double gyroTurnRate = 0.0;
	/** Our NavX MXP gyro used as PID input for turns. */
	private final AHRS gyro = new AHRS(SPI.Port.kMXP);
	/** The output target for the turn PID Controller. */
	private final PIDOutput turnPidOutput = new PIDOutput() {
		@Override
		public void pidWrite(double output) {
			gyroTurnRate = output;
		}
	};
	/** The turn PID controller using the above gyro and PID output. */
	private final PIDController turnController = new PIDController(0.2, 0.0, 0.0, 0.0, gyro, turnPidOutput);

	/** The target distance in encoder clicks. */
	private double distTarget = 0;
	/** Holds the last PID calculated distance drive velocity. */
	private double distDriveRate = 0.0;
	/** A custom PID source for distance control (see {@link DistDrivePIDSource}) */
	private PIDSource distDriveSource = new DistDrivePIDSource();
	/** The output target for the distance PID Controller. */
	private PIDOutput distDriveOutput = new PIDOutput() {
		@Override
		public void pidWrite(double output) {
			distDriveRate = output;
		}
	};
	/** The distance PID controller using the above source and output. */
	private final PIDController distController = new PIDController(0.5, 0.0, 0.0, 0.0, distDriveSource,
			distDriveOutput);

	private final WPI_TalonSRX leftMotor1 = new WPI_TalonSRX(RobotMap.LEFT_MOTOR_1);
	// private final WPI_TalonSRX leftMotor2 = new
	// WPI_TalonSRX(RobotMap.LEFT_MOTOR_2);
	private final WPI_TalonSRX rightMotor1 = new WPI_TalonSRX(RobotMap.RIGHT_MOTOR_1);
	// private final WPI_TalonSRX rightMotor2 = new
	// WPI_TalonSRX(RobotMap.RIGHT_MOTOR_2);

	/**
	 * Creates the components of the subsystem and initialized them all.
	 */
	public DriveSubsystem() {
		rightMotor1.configSelectedFeedbackSensor(FeedbackDevice.QuadEncoder, 0, 100);
		rightMotor1.setSensorPhase(true);
		// set the peak, nominal outputs, and deadband
		rightMotor1.configNominalOutputForward(0, 100);
		rightMotor1.configNominalOutputReverse(0, 100);
		rightMotor1.configPeakOutputForward(1, 100);
		rightMotor1.configPeakOutputReverse(-1, 100);
		// set closed loop gains in slot0
		rightMotor1.config_kF(0, .25, 100); // current was .265
		rightMotor1.config_kP(0, 0.1, 100); // P Value: 0.1
		rightMotor1.config_kI(0, 0, 100);
		rightMotor1.config_kD(0, 0, 100);

		leftMotor1.configSelectedFeedbackSensor(FeedbackDevice.QuadEncoder, 0, 100);
		leftMotor1.setSensorPhase(true);
		// set the peak, nominal outputs, and deadband
		leftMotor1.configNominalOutputForward(0, 100);
		leftMotor1.configNominalOutputReverse(0, 100);
		leftMotor1.configPeakOutputForward(1, 100);
		leftMotor1.configPeakOutputReverse(-1, 100);
		// set closed loop gains in slot0
		leftMotor1.config_kF(0, 0.2578, 100);
		leftMotor1.config_kP(0, 0.1, 100); // P value1: 0.117 value2: .105
		leftMotor1.config_kI(0, 0, 100);
		leftMotor1.config_kD(0, 0, 100);

		leftMotor1.setNeutralMode(NeutralMode.Brake);
		rightMotor1.setNeutralMode(NeutralMode.Brake);
		// leftMotor1.set(ControlMode.PercentOutput, 0.0);
		// leftMotor2.set(ControlMode.Follower, RobotMap.LEFT_MOTOR_1);
		// rightMotor1.set(ControlMode.PercentOutput, 0.0);
		// rightMotor2.set(ControlMode.Follower, RobotMap.RIGHT_MOTOR_1);
		turnController.setInputRange(-180.0, 180.0);
		turnController.setOutputRange(-1.0, 1.0);
		turnController.setAbsoluteTolerance(2.0);
		turnController.setContinuous(true);
		turnController.disable();

		distController.setInputRange(-100000.0, 100000.0);
		distController.setOutputRange(-5000.0, 5000.0);
		distController.setAbsoluteTolerance(100.0);
		distController.setContinuous(false);
		distController.disable();
	}

	/**
	 * @return the last calculated gyro controlled turn rate. [-1.0, 1.0]
	 */
	public double getGyroTurnRate() {
		return gyroTurnRate;
	}

	/**
	 * @return the current gyro angle (yaw). [-180.0, 180.0]
	 */
	public double getGyroAngle() {
		return gyro.getYaw();
	}

	/**
	 * @return true if the turn PID controller reports we are within our turn
	 *         tolerance.
	 */
	public boolean isTurnOnTarget() {
		return turnController.onTarget();
	}

	/**
	 * Used to initiate a gyro/PID controlled turn.
	 * 
	 * @param targetAngle the absolute field relative target angle.
	 */
	public void startTurn(double targetAngle) {
		turnController.enable();
		turnController.setSetpoint(targetAngle);
	}

	/**
	 * Used to stop (normally or otherwise) a gyro/PID controlled turn.
	 */
	public void endTurn() {
		turnController.reset();
		this.gyroTurnRate = 0.0;
	}

	/**
	 * Used to initiate a PID controlled straight drive a distance in inches.
	 */
	public void startDistance(double distanceInInches) {
		this.distTarget = distanceInInches / 18.85 * 1440.0;
		clearEncoders();
		distController.enable();
		distController.setSetpoint(this.distTarget);
	}

	/**
	 * @return the last calculated drive straight distance rate [-velocity, +velocity]
	 */
	public double getDistDriveRate() {
		return this.distDriveRate;
	}

	/**
	 * @return true if the distance PID controller reports we are within our
	 *         tolerance.
	 */
	public boolean isDistOnTarget() {
		if (distController.isEnabled()) {
			return distController.onTarget();
		}
		return true;
	}

	/**
	 * Used to stop (normally or otherwise) a PID controlled straight drive distance.
	 */
	public void endDistance() {
		distController.reset();
		this.distTarget = 0.0;
		this.distDriveRate = 0.0;
	}

	/**
	 * Updates the dashboard with drive subsystem critical data.
	 */
	public void updateDashboard() {
		SmartDashboard.putNumber("Gyro", gyro.getYaw());
		SmartDashboard.putNumber("Drive Left Encoder", leftMotor1.getSelectedSensorPosition(0));
		SmartDashboard.putNumber("Drive Right Encoder", rightMotor1.getSelectedSensorPosition(0));
	}

	public double getLeftPosition() {
		double leftRawPos = leftMotor1.getSelectedSensorPosition(0);
		double leftUnitPos = leftRawPos / 1440;
		double leftInchPos = leftUnitPos * 18.85;
		return leftInchPos;
	}

	/**
	 * Resets the left and right encoders to 0 completed clicks.
	 */
	public void clearEncoders() {
		rightMotor1.setSelectedSensorPosition(0, 0, 100);
		leftMotor1.setSelectedSensorPosition(0, 0, 100);
	}

	/**
	 * Called by the system so that we can establish our default command.
	 */
	@Override
	public void initDefaultCommand() {
		setDefaultCommand(new JoystickTankDrive());
	}

	/**
	 * Used to drive and known left and right velocities via PID.
	 * 
	 * @param leftVelocity  the velocity for the left side (clicks / sec ?)
	 * @param rightVelocity the velocity for the right side (clicks / sec ?)
	 */
	public void velocityDrive(double leftVelocity, double rightVelocity) {
		leftMotor1.set(ControlMode.Velocity, leftVelocity);
		rightMotor1.set(ControlMode.Velocity, rightVelocity);
	}

	/**
	 * Used for arcade type driving under velocity PID control.
	 */
	public void arcadeDrive(double power, double curve) {
		double leftMotorOutput = 0.0;
		double rightMotorOutput = 0.0;

		double maxInput = Math.copySign(Math.max(Math.abs(power), Math.abs(curve)), power);

		if (power >= 0.0) {
			// First quadrant, else second quadrant
			if (curve >= 0.0) {
				leftMotorOutput = maxInput;
				rightMotorOutput = power - curve;
			} else {
				leftMotorOutput = power + curve;
				rightMotorOutput = maxInput;
			}
		} else {
			// Third quadrant, else fourth quadrant
			if (curve >= 0.0) {
				leftMotorOutput = power + curve;
				rightMotorOutput = maxInput;
			} else {
				leftMotorOutput = maxInput;
				rightMotorOutput = power - curve;
			}
		}

		velocityDrive(leftMotorOutput * 1500.0, rightMotorOutput * -1500.0);
	}

	/**
	 * Convenience method to set velocity to 0.0 on both sides.
	 */
	public void stop() {
		this.velocityDrive(0.0, 0.0);
	}

	/**
	 * A distance drive PID source that returns raw clicks remaining.
	 */
	private class DistDrivePIDSource implements PIDSource {
		@Override
		public void setPIDSourceType(PIDSourceType pidSource) {
		}

		@Override
		public double pidGet() {
			int leftRawPos = leftMotor1.getSelectedSensorPosition(0);
			// right broken on old bot ... put back later
			// int rightRawPos = -rightMotor1.getSelectedSensorPosition(0);
			// int aveRawPos = (leftRawPos + rightRawPos) / 2;
			int aveRawPos = leftRawPos;
			return aveRawPos;
		}

		@Override
		public PIDSourceType getPIDSourceType() {
			return PIDSourceType.kDisplacement;
		}
	}
}
