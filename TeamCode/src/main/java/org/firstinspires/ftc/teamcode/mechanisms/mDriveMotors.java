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
    public void mecanumDrive(double y, double x, double rx) {

        // Strafing compensation
        // This needs to be tuned based on how mecanum drive shifts
        double strafe = x * 1.1;

        // Calculate powers

        double frontLeftPower = y + strafe + rx;
        double backLeftPower = y - strafe + rx;
        double frontRightPower = y - strafe - rx;
        double backRightPower = y + strafe - rx;

        // Normalize
        /*
        If we used the original FTC mecanum code
        double denominator =
        Math.max(
            Math.abs(y) + Math.abs(strafe) + Math.abs(rx),
            1
        );
        This will give us values more than 1 which is something that the motor can read
        so I changed it into calculating motor power first
        */


        double max = Math.max(
                Math.abs(frontLeftPower),
                Math.max(
                        Math.abs(backLeftPower),
                        Math.max(
                                Math.abs(frontRightPower),
                                Math.abs(backRightPower)
                        )
                )
        );

        /*
        Through this equation on the bottom in example if we had values of
        FL = 1.8
        BL = 1.2
        FR = 0.2
        BR = 0.8
        it will produce largest value of 1.8
        and what this equation does is then divide all the value by the largest value so
        FL = 1.8 / 1.8
        BL = 1.2 / 1.8
        FR = 0.2 / 1.8
        BR = 0.8 /1.8
        which gives us
        FL = 1
        BL = 0.67
        FR = 0.11
        BR = 0.44
        therefore the motor could read these values and it wouldn't get over 1
        */

        if (max > 1.0) {
            frontLeftPower /= max;
            backLeftPower /= max;
            frontRightPower /= max;
            backRightPower /= max;
        }

        leftFront.setPower(frontLeftPower);
        leftBack.setPower(backLeftPower);
        rightFront.setPower(frontRightPower);
        rightBack.setPower(backRightPower);
    }
}