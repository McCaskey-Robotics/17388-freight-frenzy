package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.hardware.rev.Rev2mDistanceSensor;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Blinker;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.DistanceSensor;
import com.qualcomm.robotcore.hardware.Gyroscope;
import com.qualcomm.robotcore.util.ElapsedTime;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

@TeleOp
public class MechanumDrive21_22_Copy
extends LinearOpMode {
    private Blinker control_Hub;
    private Blinker expansion_Hub;
    private DcMotor brMotor;
    private DcMotor frMotor;
    private DcMotor blMotor;
    private DcMotor flMotor;
    private Gyroscope imu_1;
    private Gyroscope imu;
    private DcMotor spinner;
    private DcMotor turnTable;
    private DcMotor elbow;
    private CRServo intake;
    private ColorSensor colorSensor;
    private DistanceSensor elementDetector;
    private Rev2mDistanceSensor distanceSensor;
    private ElapsedTime runtime = new ElapsedTime();
    double speedMultiplier = 1.0;
    int flipRotation = 1;
    boolean duckWheel = false;
    double tablePosDeg;
    double elbowPosDeg;
    double elbowPos;
    double leftBound;
    double rightBound;
    boolean rtState = false;
    boolean intakeState = false;
    double colorGreen;
    boolean yState = false;
    boolean aState = false;
    boolean lbState = false;
    double startingTablePos;
    double startingElbowPos;
    boolean clearLeft;
    boolean clearRight;
    int currentElbowPos;

    public void runOpMode() {
        this.brMotor = (DcMotor)this.hardwareMap.get(DcMotor.class, "brMotor");
        this.frMotor = (DcMotor)this.hardwareMap.get(DcMotor.class, "frMotor");
        this.blMotor = (DcMotor)this.hardwareMap.get(DcMotor.class, "blMotor");
        this.flMotor = (DcMotor)this.hardwareMap.get(DcMotor.class, "flMotor");
        this.spinner = (DcMotor)this.hardwareMap.get(DcMotor.class, "spinner");
        this.turnTable = (DcMotor)this.hardwareMap.get(DcMotor.class, "turnTable");
        this.elbow = (DcMotor)this.hardwareMap.get(DcMotor.class, "elbow");
        this.intake = (CRServo)this.hardwareMap.get(CRServo.class, "intake");
        this.colorSensor = (ColorSensor)this.hardwareMap.get(ColorSensor.class, "colorSensor");
        this.elementDetector = (DistanceSensor)this.hardwareMap.get(DistanceSensor.class, "elementDetector");
        this.distanceSensor = (Rev2mDistanceSensor)this.hardwareMap.get(Rev2mDistanceSensor.class, "distanceSensor");
        this.startingElbowPos = this.elbow.getCurrentPosition();
        this.startingTablePos = this.turnTable.getCurrentPosition();
        this.flMotor.setDirection(DcMotorSimple.Direction.REVERSE);
        this.blMotor.setDirection(DcMotorSimple.Direction.REVERSE);
        this.spinner.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        this.elbow.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        this.turnTable.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        this.telemetry.addData("Status", (Object)"Initialized");
        this.telemetry.update();
        this.waitForStart();
        
        
        while (this.opModeIsActive()) {
            this.mecanumControl();
            this.armControl();
            this.additionalControl();
            
            this.telemetry.addData("Front Distance:", (Object)this.distanceSensor.getDistance(DistanceUnit.INCH));
            this.telemetry.update();
        }
    }

    public void mecanumControl() {
        double frontPower = (double)(-this.gamepad1.left_stick_y) * this.speedMultiplier;
        double strafePower = (double)this.gamepad1.left_stick_x * this.speedMultiplier;
        double rotatePower = this.gamepad1.right_stick_x;
        double brPower = frontPower - rotatePower + strafePower;
        double blPower = frontPower + rotatePower - strafePower;
        double frPower = frontPower - rotatePower - strafePower;
        double flPower = frontPower + rotatePower + strafePower;
        if (Math.abs(brPower) > 1.0) {
            brPower = Math.abs(brPower) / brPower;
        }
        if (Math.abs(frPower) > 1.0) {
            frPower = Math.abs(frPower) / frPower;
        }
        if (Math.abs(blPower) > 1.0) {
            blPower = Math.abs(blPower) / blPower;
        }
        if (Math.abs(flPower) > 1.0) {
            flPower = Math.abs(flPower) / flPower;
        }
        if (Math.abs(blPower) > 1.0) {
            blPower = Math.abs(blPower) / blPower;
        }
        this.brMotor.setPower(brPower);
        this.frMotor.setPower(frPower);
        this.blMotor.setPower(blPower);
        this.flMotor.setPower(flPower);
    }

    public void additionalControl() {
        if(gamepad1.left_trigger > .1){
            spinner.setPower(gamepad1.left_trigger * .50);
        
        }else{
            spinner.setPower(0);
            
        }
        
        /*if (gamepad1.left_bumper && !lbState){
            lbState = true;
        }
        
        if(this.lbState) {
            this.spinner.setPower(0.6);
            //sleep(150);
            //this.spinner.setPower(.4);
            while(gamepad1.left_bumper){}
            lbState = false;
        }
            
        if (this.gamepad1.right_bumper) {
            this.spinner.setPower(-.04);
        } else {
            this.spinner.setPower(0.0);
        }*/
    }

    public void armControl() {
        //this.tablePosDeg = this.tableEncoderToDeg(this.turnTable.getCurrentPosition());
        this.elbowPosDeg = this.elbowEncoderToDeg(this.elbow.getCurrentPosition());
        this.colorGreen = this.colorSensor.green();
        this.telemetry.addData("Green:", (Object)this.colorGreen);
        this.telemetry.addData("Table Angle: ", (Object)this.tablePosDeg);
        this.telemetry.addData("Elbow Angle:", (Object)this.elbowPosDeg);

        this.clearLeft = true;
        this.clearRight = true;
        if (this.tablePosDeg > this.startingTablePos + this.startingElbowPos) {
            this.clearLeft = true;
            this.clearRight = true;
        } else if (this.tablePosDeg < this.leftBound) {
            this.clearLeft = true;
        } else if (this.tablePosDeg > this.rightBound) {
            this.clearRight = true;
        }
        if (this.gamepad1.y && !this.yState && !this.aState) {
            this.yState = true;
        }
        if (this.gamepad1.a && !this.yState && !this.aState) {
            this.aState = true;
        }
        if (this.gamepad1.dpad_up) {
            this.elbow.setPower(1.0);
        } else if (this.gamepad1.dpad_down) {
            this.elbow.setPower(-1.0);
        } else if (this.yState && this.elbowPosDeg < 10.0) {
            this.elbow.setPower(1.0);
        } else if (this.aState && this.elbowPosDeg > -60.0) {
            this.elbow.setPower(-1.0);
        } else {
            this.elbow.setPower(0.0);
            this.aState = false;
            this.yState = false;
        }
        if (this.gamepad1.left_bumper) {
            this.intake.setPower(-1);
        } else {
            this.intake.setPower(0.0);
        }
        if ((double)this.gamepad1.right_trigger > 0.75) {
            boolean bl = this.rtState = !this.rtState;
            while ((double)this.gamepad1.right_trigger > 0.25) {
            }
        }
        if (this.gamepad1.left_bumper) {
            this.intake.setPower((double)(-1));
            this.rtState = false;
        } else if (this.colorGreen > 300.0) {
            this.intake.setPower(0.0);
            this.yState = true;
        } else if (this.rtState) {
            this.intake.setPower(1.0);
        } else {
            this.intake.setPower(0.0);
        }
    }

    private double elbowEncoderToDeg(double ticks) {
        return (ticks - this.startingElbowPos) / 12500.0 * 360.0 - 60.0;
    }
}
