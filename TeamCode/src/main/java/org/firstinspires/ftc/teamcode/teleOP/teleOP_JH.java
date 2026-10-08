package org.firstinspires.ftc.teamcode.teleOP;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import org.firstinspires.ftc.teamcode.mechanisms.mDriveMotors;

import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;


//git commit -am "commit name"
//git push origin main
//git pull origin main
//adb connect 192.168.43.1:5555

@TeleOp(name = "teleOP_JH",group = "TeleOp")
//@Disabled
public class teleOP_JH extends OpMode {
    // Instantiate my mechanisms class object
    mDriveMotors drive = new mDriveMotors();

    // where your variables and objects declaration goes
    private DcMotor intake;
    private CRServo leftintake;
    private CRServo rightintake;

    private enum IntakeStatus {
        ON,
        OFF
    }

    private IntakeStatus intakeStatus = IntakeStatus.OFF;

    @Override
    public void init() {
        intake = hardwareMap.get(DcMotor.class, "intake");
        leftintake = hardwareMap.get(CRServo.class, "left_intake");
        rightintake = hardwareMap.get(CRServo.class, "right_intake");

        intake.setPower(0);
        leftintake.setPower(0);
        rightintake.setPower(0);

        drive.init(hardwareMap);

        telemetry.addData("Status", "Initialized");

    }

    /*
     * Code to run REPEATEDLY after the driver hits INIT, but before they hit START
     */
    @Override
    public void init_loop() {

    }

    /*
     * Code to run ONCE when the driver hits START
     */
    @Override
    public void start() {

    }

    /*
     * Code to run REPEATEDLY after the driver hits START but before they hit STOP
     */
    @Override
    public void loop() {
        //GAMEPAD 1 PROGRAMS

        // Read joysticks (Remember, Y stick value is inherently reversed in FTC)
        double y = -gamepad1.left_stick_y;
        double x = gamepad1.left_stick_x;
        double rx = gamepad1.right_stick_x;

        // Deadzone

        y = deadzone(y);
        x = deadzone(x);
        rx = deadzone(rx);

        // Squared response
        /*
        Normally if we press a joystick it gives a linear power value such as 0.7=70% power
        but if I change input value into a square root then 0.7=49% power which makes it precise
        so the end don't change much but the middle become less sensitive
         */

        y = Math.copySign(Math.pow(Math.abs(y), 2), y);
        x = Math.copySign(Math.pow(Math.abs(x), 2), x);
        rx = Math.copySign(Math.pow(Math.abs(rx), 2), rx);

        // Precision mode
        /*
        This is a shorter version of
        double speed;

        if (gamepad1.right_bumper) {
            speed = 0.4;
        } else {
            speed = 1.0;
        }

        What it does is it tells the robot to drive normally but limit everything to a certain %
         */

        double speed = gamepad1.right_bumper ? 0.4 : 1.0;

        drive.mecanumDrive(
                y * speed,
                x * speed,
                rx * speed
        );



        //GAMEPAD 2 PROGRAMS


        if (gamepad2.bWasPressed()){
            switch (intakeStatus){
                case ON:
                    intake.setDirection(DcMotorSimple.Direction.FORWARD);
                    intakeStatus = IntakeStatus.OFF;
                    intake.setPower(0);
                    leftintake.setPower(0);
                    rightintake.setPower(0);
                    break;

                case OFF:
                    intake.setDirection(DcMotorSimple.Direction.REVERSE);
                    intakeStatus = IntakeStatus.ON;
                    intake.setPower(1);
                    leftintake.setPower(1);
                    rightintake.setPower(1);
                    break;
            }
        }



    }

    /*
    What the "deadzone" do is basically saying that
    If the joystick is very close to zero, pretend it's exactly zero.
     */
    private double deadzone(double value) {
        return Math.abs(value) < 0.05 ? 0 : value;
    }

}
