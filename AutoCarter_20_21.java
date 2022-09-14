package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.hardware.rev.Rev2mDistanceSensor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import com.qualcomm.robotcore.hardware.Blinker;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DistanceSensor;
import com.qualcomm.robotcore.hardware.Gyroscope;
import com.qualcomm.robotcore.hardware.CRServo;

@Autonomous

public class AutoCarter_20_21 extends LinearOpMode{
    private Blinker control_Hub;
    private Blinker expansion_Hub;
    private DcMotor blMotor;
    private DcMotor brMotor;
    private ColorSensor colorSensor;
    private DistanceSensor distanceSensor;
    private DcMotor elbow;
    private ColorSensor elementDetector;
    private DcMotor flMotor;
    private DcMotor frMotor;
    private Gyroscope imu_1;
    private Gyroscope imu;
    private CRServo intake;
    private DcMotorEx spinner;
    private DcMotor turnTable;

public void runOpMode() {
        this.brMotor = (DcMotor)this.hardwareMap.get(DcMotor.class, "brMotor");
        this.frMotor = (DcMotor)this.hardwareMap.get(DcMotor.class, "frMotor");
        this.blMotor = (DcMotor)this.hardwareMap.get(DcMotor.class, "blMotor");
        this.flMotor = (DcMotor)this.hardwareMap.get(DcMotor.class, "flMotor");
        this.spinner = (DcMotorEx)this.hardwareMap.get(DcMotorEx.class, "spinner");
        this.turnTable = (DcMotor)this.hardwareMap.get(DcMotor.class, "turnTable");
        this.elbow = (DcMotor)this.hardwareMap.get(DcMotor.class, "elbow");
        this.intake = (CRServo)this.hardwareMap.get(CRServo.class, "intake");
        this.colorSensor = (ColorSensor)this.hardwareMap.get(ColorSensor.class, "colorSensor");
        //this.elementDetector = (DistanceSensor)this.hardwareMap.get(DistanceSensor.class, "elementDetector");
        //this.distanceSensor = (Rev2mDistanceSensor)this.hardwareMap.get(Rev2mDistanceSensor.class, "distanceSensor");
        //this.startingElbowPos = this.elbow.getCurrentPosition();
        //this.startingTablePos = this.turnTable.getCurrentPosition();
        this.flMotor.setDirection(DcMotorSimple.Direction.REVERSE);
        this.blMotor.setDirection(DcMotorSimple.Direction.REVERSE);
        this.spinner.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        this.elbow.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        this.spinner.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        this.turnTable.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        this.telemetry.addData("Status", (Object)"Initialized");
        this.telemetry.update();
        this.waitForStart();
        this.telemetry.addData("Speed:", spinner.getVelocity());
        
        while (this.opModeIsActive()) {
            this.brMotor.setPower(1);
            this.frMotor.setPower(-1);
            this.blMotor.setPower(-1);
            this.flMotor.setPower(1);
            sleep(100);
            this.brMotor.setPower(-0.25);
            this.frMotor.setPower(-0.25);
            this.blMotor.setPower(-0.25);
            this.flMotor.setPower(-0.25);
            sleep(1300);
            this.brMotor.setPower(0);
            this.frMotor.setPower(0);
            this.blMotor.setPower(0);
            this.flMotor.setPower(0);
            this.spinner.setPower(1);
            sleep(150);
            this.spinner.setPower(.019);
            sleep(2900);
            spinner.setPower(0);
            this.brMotor.setPower(1);
            this.frMotor.setPower(-1);
            this.blMotor.setPower(-1);
            this.flMotor.setPower(1);
            sleep(200);
            this.brMotor.setPower(1);
            this.frMotor.setPower(1);
            this.blMotor.setPower(1);
            this.flMotor.setPower(1);
            sleep(600);
            this.brMotor.setPower(-1);
            this.frMotor.setPower(1);
            this.blMotor.setPower(1);
            this.flMotor.setPower(-1);
            sleep(500);
            this.brMotor.setPower(1);
            this.frMotor.setPower(1);
            this.blMotor.setPower(1);
            this.flMotor.setPower(1);
            sleep(1250);
            this.brMotor.setPower(-1);
            this.frMotor.setPower(1);
            this.blMotor.setPower(1);
            this.flMotor.setPower(-1);
            sleep(500);
            this.brMotor.setPower(1);
            this.frMotor.setPower(1);
            this.blMotor.setPower(1);
            this.flMotor.setPower(1);
            sleep(200);
            this.brMotor.setPower(0);
            this.frMotor.setPower(0);
            this.blMotor.setPower(0);
            this.flMotor.setPower(0);
            break;
        
        }
    }
    
}