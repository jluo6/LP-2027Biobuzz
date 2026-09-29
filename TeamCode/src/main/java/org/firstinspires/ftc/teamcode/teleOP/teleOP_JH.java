package org.firstinspires.ftc.teamcode.teleOP;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import org.firstinspires.ftc.teamcode.mechanisms.mDriveMotors;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;


//git commit -am "commit name"
//git push origin main
//git pull origin main

@TeleOp(name = "teleOP_JH",group = "TeleOp")
//@Disabled
public class teleOP_JH extends OpMode {
    // Instantiate my mechanisms class object
    mDriveMotors drive = new mDriveMotors();

    // where your variables and objects declaration goes
    private DcMotor intake;

    private enum IntakeStatus {
        ON,
        OFF
    }

    private IntakeStatus intakeStatus = IntakeStatus.OFF;

    @Override
    public void init() {
        intake = hardwareMap.get(DcMotor.class, "intake");
        intake.setPower(0);

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
        // Read joysticks (Remember, Y stick value is inherently reversed in FTC)
        double y = -gamepad1.left_stick_y;
        double x = gamepad1.left_stick_x;
        double rx = gamepad1.right_stick_x;

        // Feed the live joystick data to your drive class
        drive.mecanumDrive(y, x, rx);

        // Optional: Send data back to the driver station telemetry
        telemetry.addData("Drive Sticks", "Y: %.2f, X: %.2f, RX: %.2f", y, x, rx);
        telemetry.update();



        if (gamepad1.bWasPressed()){
            switch (intakeStatus){
                case ON:
                    intake.setDirection(DcMotorSimple.Direction.REVERSE);
                    intakeStatus = IntakeStatus.OFF;
                    intake.setPower(0);
                    break;

                case OFF:
                    intake.setDirection(DcMotorSimple.Direction.FORWARD);
                    intakeStatus = IntakeStatus.ON;
                    intake.setPower(1);
                    break;
            }
        }



    }

}