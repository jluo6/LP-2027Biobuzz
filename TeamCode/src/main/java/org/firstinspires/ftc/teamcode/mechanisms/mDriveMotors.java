package org.firstinspires.ftc.teamcode.mechanisms;

import static com.qualcomm.robotcore.hardware.DcMotor.ZeroPowerBehavior.BRAKE;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class mDriveMotors {
    // Name variables
    private DcMotor leftFront;
    private DcMotor rightFront;
    private DcMotor leftBack;
    private DcMotor rightBack;

    public void init(HardwareMap hwMap){
        /*
        Define Variables/Objects
        Gives variables values which in this case is the name in control hub
         */
        leftFront = hwMap.get(DcMotor.class, "left_front_drive");
        rightFront = hwMap.get(DcMotor.class, "right_front_drive");
        leftBack = hwMap.get(DcMotor.class, "left_back_drive");
        rightBack = hwMap.get(DcMotor.class, "right_back_drive");

        /*
        The constructor
        Tells the motors to run through velocity instead of ticks
         */
        leftFront.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        rightFront.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        leftBack.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        rightBack.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        /*
        Tells the Motor to brake when stopped
        Tells the Motors to reverse when stopped
         */
        leftFront.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        rightFront.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        leftBack.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        rightBack.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        rightFront.setDirection(DcMotorSimple.Direction.REVERSE);
        rightBack.setDirection(DcMotorSimple.Direction.REVERSE);

        leftFront.setDirection(DcMotorSimple.Direction.REVERSE);
        leftBack.setDirection(DcMotorSimple.Direction.REVERSE);
    }

    /*
    The Methods
     */
    public void setMotorSpeed (double speed){
        // This accepts value from -1.0 to 1.0
        leftFront.setPower(speed);
        rightFront.setPower(speed);
        leftBack.setPower(speed);
        rightBack.setPower(speed);
    }

    // Accepts the gamepad stick coordinates as arguments from the OpMode
    public void mecanumDrive (double y, double x, double rx){
        double strafe = x * 1.1; // Counteract imperfect strafing

        // Denominator is the largest motor power (absolute value) or 1
        // This ensures all the powers maintain the same ratio,
        // but only if at least one is out of the range [-1, 1]
        double denominator = Math.max(Math.abs(y) + Math.abs(strafe) + Math.abs(rx), 1);
        double frontLeftPower = (y + strafe + rx) / denominator;
        double backLeftPower = (y - strafe + rx) / denominator;
        double frontRightPower = (y - strafe - rx) / denominator;
        double backRightPower = (y + strafe - rx) / denominator;

        // Using the exact motor variables declared above
        leftFront.setPower(frontLeftPower);
        leftBack.setPower(backLeftPower);
        rightFront.setPower(frontRightPower);
        rightBack.setPower(backRightPower);
    }
}