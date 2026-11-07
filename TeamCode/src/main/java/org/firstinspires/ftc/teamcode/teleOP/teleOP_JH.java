
package org.firstinspires.ftc.teamcode.teleOP;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import org.firstinspires.ftc.teamcode.mechanisms.mDriveMotors;

import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;


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

    private DcMotorEx launcher;
    private CRServo windmillServo;

    private final int LAUNCHER_TARGET_VELOCITY = 1250;
    private final int LAUNCHER_MIN_VELOCITY = 1000;
    private final int LAUNCHER_READY_VELOCITY = 1210;

    private boolean launcherOn = false;
    private boolean windmillOn = false;

    private enum IntakeStatus {
        ON,
        OFF,
        REVERSE
    }

    private IntakeStatus intakeStatus = IntakeStatus.OFF;

    void launch() {
        if (launcherOn) {
            launcher.setVelocity(LAUNCHER_TARGET_VELOCITY);
        } else {
            launcher.setVelocity(LAUNCHER_MIN_VELOCITY);
        }

        double currentVelocity = Math.abs(launcher.getVelocity());

        if (intakeStatus == IntakeStatus.REVERSE) {
            windmillOn = true;
            windmillServo.setPower(-1.0);
        } else {
            windmillOn = launcherOn &&
                    currentVelocity >= LAUNCHER_READY_VELOCITY;

            windmillServo.setPower(windmillOn ? 1.0 : 0.0);
        }
    }

    @Override
    public void init() {
        intake = hardwareMap.get(DcMotor.class, "intake");
        leftintake = hardwareMap.get(CRServo.class, "left_intake");
        rightintake = hardwareMap.get(CRServo.class, "right_intake");
        launcher = hardwareMap.get(DcMotorEx.class, "launcher");
        windmillServo = hardwareMap.get(CRServo.class, "windmillServo");

        launcher.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        launcher.setPIDFCoefficients(
                DcMotor.RunMode.RUN_USING_ENCODER,
                new PIDFCoefficients(32.0, 0, 0, 12.32)
        );

        intake.setDirection(DcMotorSimple.Direction.FORWARD);

        intake.setPower(0);
        leftintake.setPower(0);
        rightintake.setPower(0);
        launcher.setVelocity(0);
        windmillServo.setPower(0);

        drive.init(hardwareMap);

        telemetry.addData("Status", "Initialized");
        telemetry.update();
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

        double speed = gamepad1.left_bumper ? 0.4 : 1.0;

        drive.mecanumDrive(
                y * speed,
                x * speed,
                rx * speed
        );



        //GAMEPAD 2 PROGRAMS

        if (gamepad2.aWasPressed()) {
            launcherOn = !launcherOn;

            if (launcherOn && intakeStatus == IntakeStatus.REVERSE) {
                intakeStatus = IntakeStatus.OFF;
            }
        }

        if (gamepad2.rightBumperWasPressed()) {
            if (intakeStatus == IntakeStatus.ON) {
                intakeStatus = IntakeStatus.OFF;
            } else {
                intakeStatus = IntakeStatus.ON;
            }
        }

        if (gamepad2.leftBumperWasPressed()) {
            if (intakeStatus == IntakeStatus.REVERSE) {
                intakeStatus = IntakeStatus.OFF;
            } else {
                intakeStatus = IntakeStatus.REVERSE;
                launcherOn = false;
            }
        }


        switch (intakeStatus) {
            case ON:
                intake.setPower(1.0);
                leftintake.setPower(1.0);
                rightintake.setPower(1.0);
                break;

            case REVERSE:
                intake.setPower(-1.0);
                leftintake.setPower(-1.0);
                rightintake.setPower(-1.0);
                break;

            case OFF:
                intake.setPower(0);
                leftintake.setPower(0);
                rightintake.setPower(0);
                break;
        }

        launch();

        double currentVelocity = launcher.getVelocity();

        boolean launcherReady =
                launcherOn &&
                        Math.abs(currentVelocity) >= LAUNCHER_READY_VELOCITY;

        telemetry.addData("Drive Speed", speed);
        telemetry.addData("Intake Status", intakeStatus);
        telemetry.addData("Launcher Velocity", currentVelocity);
        telemetry.addData("Launcher Target",
                launcherOn ? LAUNCHER_TARGET_VELOCITY : LAUNCHER_MIN_VELOCITY);
        telemetry.addData("Launcher Ready", launcherReady);
        telemetry.addData("Windmill",
                intakeStatus == IntakeStatus.REVERSE
                        ? "REVERSE"
                        : (windmillOn ? "ON" : "OFF"));
        telemetry.update();
    }

    /*
    What the "deadzone" does is basically saying that
    If the joystick is very close to zero, pretend it's exactly zero.
     */
    private double deadzone(double value) {
        return Math.abs(value) < 0.05 ? 0 : value;
    }

    @Override
    public void stop() {
        drive.mecanumDrive(0, 0, 0);

        intake.setPower(0);
        leftintake.setPower(0);
        rightintake.setPower(0);

        launcher.setVelocity(0);
        windmillServo.setPower(0);

        launcherOn = false;
        windmillOn = false;
        intakeStatus = IntakeStatus.OFF;
    }

}
