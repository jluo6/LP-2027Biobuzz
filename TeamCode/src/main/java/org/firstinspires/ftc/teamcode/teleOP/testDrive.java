package org.firstinspires.ftc.teamcode.teleOP;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import org.firstinspires.ftc.teamcode.mechanisms.mDriveMotors;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

@TeleOp(name = "testDrive", group = "TeleOp")
public class testDrive extends OpMode {

    // Instantiate mechanisms class object
    mDriveMotors drive = new mDriveMotors();

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

    @Override
    public void init_loop() {
    }

    @Override
    public void start() {
    }

    @Override
    public void loop() {
        // --- GAMEPAD 1 BUTTON-BASED DRIVING ---
        double y = 0;
        double x = 0;
        double rx = 0;

        // Forward / Backward control via D-pad Up / Down
        if (gamepad1.dpad_up) {
            y = 1.0;
        } else if (gamepad1.dpad_down) {
            y = -1.0;
        }

        // Strafing Left / Right via D-pad Left / Right
        if (gamepad1.dpad_left) {
            x = -1.0;
        } else if (gamepad1.dpad_right) {
            x = 1.0;
        }

        // Turning Left / Right via X and B buttons
        if (gamepad1.x) {
            rx = -1.0; // Turn Left
        } else if (gamepad1.b) {
            rx = 1.0;  // Turn Right
        }

        // Speed multi-plier (Maintains your precision bumper feature)
        double speed = gamepad1.right_bumper ? 0.4 : 1.0;

        // Send binary button inputs directly to mecanum drive
        drive.mecanumDrive(y * speed, x * speed, rx * speed);


        // --- GAMEPAD 2 INTAKE PROGRAM ---
        // Note: Replaced "bWasPressed()" (which isn't a standard FTC SDK method) with a standard boolean check.
        if (gamepad2.b) {
            switch (intakeStatus) {
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
}
