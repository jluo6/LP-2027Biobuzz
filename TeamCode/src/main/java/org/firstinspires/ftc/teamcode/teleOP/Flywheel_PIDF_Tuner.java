
package org.firstinspires.ftc.teamcode.teleOP;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;

@TeleOp(name = "Flywheel_PIDF_Tuner", group = "Test")
public class Flywheel_PIDF_Tuner extends OpMode {

    private DcMotorEx launcher;

    private final double TARGET_VELOCITY = 1250;
    private final double MIN_VELOCITY = 1200;

    private double p = 40;
    private double i = 0;
    private double d = 0;
    private double f = 12.5;

    private final String[] coefficientNames = {"P", "I", "D", "F"};

    private double adjustmentStep = 1.0;
    private int selected = 0;
    private boolean launcherOn = false;

    private double startTime = 0;
    private double spinUpTime = -1;

    @Override
    public void init() {
        launcher = hardwareMap.get(DcMotorEx.class, "launcher");

        launcher.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        launcher.setVelocity(0);

        updatePIDF();

        telemetry.addData("Status", "Initialized");
        telemetry.update();
    }

    @Override
    public void loop() {

        if (gamepad2.aWasPressed()) {
            launcherOn = !launcherOn;

            if (launcherOn) {
                startTime = getRuntime();
                spinUpTime = -1;
            }
        }

        if (gamepad2.dpadRightWasPressed()) {
            selected = (selected + 1) % 4;
        }

        if (gamepad2.dpadLeftWasPressed()) {
            selected = (selected + 3) % 4;
        }

        if (gamepad2.xWasPressed()) {
            adjustmentStep = 1.0;
        }

        if (gamepad2.bWasPressed()) {
            adjustmentStep = 0.1;
        }

        if (gamepad2.yWasPressed()) {
            adjustmentStep = 0.01;
        }

        if (gamepad2.dpadUpWasPressed()) {
            adjustPIDF(1);
        }

        if (gamepad2.dpadDownWasPressed()) {
            adjustPIDF(-1);
        }

        if (gamepad2.leftBumperWasPressed()) {
            p = 40;
            i = 0;
            d = 0;
            f = 12.5;
            updatePIDF();
        }

        if (launcherOn) {
            launcher.setVelocity(TARGET_VELOCITY);
        } else {
            launcher.setVelocity(0);
        }

        double currentVelocity = Math.abs(launcher.getVelocity());

        double error = launcherOn
                ? TARGET_VELOCITY - currentVelocity
                : 0;

        boolean launcherReady =
                launcherOn && currentVelocity >= MIN_VELOCITY;

        if (launcherReady && spinUpTime < 0) {
            spinUpTime = getRuntime() - startTime;
        }

        telemetry.addData("Launcher", launcherOn ? "ON" : "OFF");
        telemetry.addData("Target Velocity", TARGET_VELOCITY);
        telemetry.addData("Current Velocity", currentVelocity);
        telemetry.addData("Velocity Error", error);
        telemetry.addData("Launcher Ready", launcherReady);

        telemetry.addData("Selected Coefficient", coefficientNames[selected]);
        telemetry.addData("Adjustment Step", adjustmentStep);

        telemetry.addData("P", "%.2f", p);
        telemetry.addData("I", "%.2f", i);
        telemetry.addData("D", "%.2f", d);
        telemetry.addData("F", "%.2f", f);

        if (spinUpTime >= 0) {
            telemetry.addData("Spin Up Time", "%.2f seconds", spinUpTime);
        } else {
            telemetry.addData("Spin Up Time", "Not Reached");
        }

        telemetry.update();
    }

    private void adjustPIDF(int direction) {

        double change = direction * adjustmentStep;

        switch (selected) {
            case 0:
                p = roundValue(Math.max(0, p + change));
                break;

            case 1:
                i = roundValue(Math.max(0, i + change));
                break;

            case 2:
                d = roundValue(Math.max(0, d + change));
                break;

            case 3:
                f = roundValue(Math.max(0, f + change));
                break;
        }

        updatePIDF();
    }

    private double roundValue(double value) {
        return Math.round(value * 100.0) / 100.0;
    }

    private void updatePIDF() {
        launcher.setPIDFCoefficients(
                DcMotor.RunMode.RUN_USING_ENCODER,
                new PIDFCoefficients(p, i, d, f)
        );
    }

    @Override
    public void stop() {
        launcher.setVelocity(0);
        launcherOn = false;
    }
}
