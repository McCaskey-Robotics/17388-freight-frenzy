/*
Copyright 2020 FIRST Tech Challenge Team FTC

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
associated documentation files (the "Software"), to deal in the Software without restriction,
including without limitation the rights to use, copy, modify, merge, publish, distribute,
sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial
portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/

/* Gamepad Teleop controls - Joystick controls
    gamepad1.[whatever] (defaults are gamepad 1 and then gamepad 2)
    
    D-Pad = dpad_* ; no idea what goes in place of the star
    Left Stick = left_stick_x , left_stick_y
    Right Stick right_stick_x , right_stick_y
    X-Button = x
    Y-Button = y
    A-Button = a
    B-Button = b
    Left trigger = left_trigger
    Right trigger = right_trigger
    
    Cont Servo stuff:
    Max power forward = .setPower(1)
    Max power backward = .setPower(-1)
    stop = .setPower(0)
    Get servo power = getPower()
    
*/
package org.firstinspires.ftc.teamcode;

import java.util.Locale;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.TouchSensor;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DistanceSensor;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DistanceSensor;
import com.qualcomm.robotcore.hardware.TouchSensor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.util.ElapsedTime;
import android.graphics.Color;
import android.app.Activity;
import android.view.View;

/**
 * This file contains an minimal example of a Linear "OpMode". An OpMode is a 'program' that runs in either
 * the autonomous or the teleop period of an FTC match. The names of OpModes appear on the menu
 * of the FTC Driver Station. When an selection is made from the menu, the corresponding OpMode
 * class is instantiated on the Robot Controller and executed.
 *
 * This particular OpMode just executes a basic Tank Drive Teleop for a PushBot
 * It includes all the skeletal structure that all linear OpModes contain.
 *
 * Remove a @Disabled the on the next line or two (if present) to add this opmode to the Driver Station OpMode list,
 * or add a @Disabled annotation to prevent this OpMode from being added to the Driver Station
 */
@TeleOp

public class MechanumDrive extends LinearOpMode {
    private DcMotor leftbackMotor;
    private DcMotor rightbackMotor;
    private DcMotor leftfrontMotor;
    private DcMotor rightfrontMotor;
    private DcMotor elevator;
    private DcMotor conveyor;
    private DcMotor intake;
    private DcMotor shooter;
    private Servo claw;
    private Servo shooterAngle;
    private Servo stopper;
    private CRServo flicker;
    private TouchSensor elevatorTopButton;
    private TouchSensor elevatorBottomButton;
    private TouchSensor magnetSensor;
    private TouchSensor magnetFlicker;
    private ColorSensor sensorColorIntake;
    private DistanceSensor sensorDistanceIntake;
    private DistanceSensor stackLaser;
    private DistanceSensor wallDistance;
    double speedMultiplier = 1; 
    boolean initialSetup = false;
    boolean shooterOn = false;
    boolean elevatorLowering = false;
    boolean elevatorLifting = false;
    int ringCount = 0;
    int conveyorEncoderSave = 0;
    int flipRotation=1;
    

    @Override
    public void runOpMode() {

        leftbackMotor = hardwareMap.get(DcMotor.class, "backLeft");
        rightbackMotor = hardwareMap.get(DcMotor.class, "backRight");
        leftfrontMotor = hardwareMap.get(DcMotor.class, "frontLeft");
        rightfrontMotor = hardwareMap.get(DcMotor.class, "frontRight");
        elevator = hardwareMap.get(DcMotor.class, "elevator");
        conveyor = hardwareMap.get(DcMotor.class, "conveyor");
        intake = hardwareMap.get(DcMotor.class, "intake");
        shooter = hardwareMap.get(DcMotor.class, "shooter");
        claw = hardwareMap.get(Servo.class, "claw");
        shooterAngle = hardwareMap.get(Servo.class, "shooterAngle");
        stopper = hardwareMap.get(Servo.class, "stopper");
        flicker = hardwareMap.get(CRServo.class, "flicker");
        elevatorTopButton = hardwareMap.get(TouchSensor.class, "elevatorTopButton");
        elevatorBottomButton = hardwareMap.get(TouchSensor.class, "elevatorBottomButton");
        magnetSensor = hardwareMap.get(TouchSensor.class, "magnetSensor");
        magnetFlicker = hardwareMap.get(TouchSensor.class, "magnetFlicker");
        sensorColorIntake = hardwareMap.get(ColorSensor.class, "sensorColorIntake");
        sensorDistanceIntake = hardwareMap.get(DistanceSensor.class, "sensorColorIntake");
        stackLaser = hardwareMap.get(DistanceSensor.class, "stackLaser");
        wallDistance = hardwareMap.get(DistanceSensor.class, "wallDistance");
        rightfrontMotor.setDirection(DcMotor.Direction.REVERSE);
        rightbackMotor.setDirection(DcMotor.Direction.REVERSE);
        elevator.setDirection(DcMotor.Direction.REVERSE);

        telemetry.addData("Status", "Initialized");
        telemetry.update();
        // Wait for the game to start (driver presses PLAY)
        waitForStart();
    //elevator.setMode(DcMotor.RunMode.RUN_TO_POSITION);
    // run until the end of the match (driver presses STOP)
        while (opModeIsActive()) {
           
        // move 
        mecanumControl();
      telemetry.addData("position",elevator.getCurrentPosition() ) ; 
        elevator.setPower(gamepad1.right_stick_y);
        
        }//while opmode is active
        
        
    }//run opmode
    public void mecanumControl(){
    //false if wheel bars create a diamond shape
        boolean mecanumLayout_isX = true;
    
    //variables for later use 
        double max_motorPower = 0;
        int mecanumAdjust = 1;
        
    //list of variables to keep track of each direction's contribution to power in each wheel
        double powerDueTo_forwardBackward_frontLeft = 0;
        double powerDueTo_forwardBackward_frontRight = 0;
        double powerDueTo_forwardBackward_backLeft = 0;
        double powerDueTo_forwardBackward_backRight = 0;
        
        double powerDueTo_strafe_frontLeft = 0;
        double powerDueTo_strafe_frontRight = 0;
        double powerDueTo_strafe_backLeft = 0;
        double powerDueTo_strafe_backRight = 0;
        
        double powerDueTo_rotate_frontLeft = 0;
        double powerDueTo_rotate_frontRight = 0;
        double powerDueTo_rotate_backLeft = 0;
        double powerDueTo_rotate_backRight = 0;
        
    //variables to keep track of actual power sent to wheels based on averages for aboec 
        double average_motorPower_frontLeft = 0;
        double average_motorPower_frontRight = 0;
        double average_motorPower_backLeft = 0;
        double average_motorPower_backRight = 0;
    
    //let driver switch between slow, precise and not that 
        if(gamepad1.right_bumper && speedMultiplier == 1){
            speedMultiplier = 0.33;
        //get computer in loop to prevent flickering
        while(gamepad1.right_bumper){}
        }
        else if(gamepad1.right_bumper && speedMultiplier == 0.33){
            speedMultiplier = 1;
            //get computer in loop to prevent flickering
        while(gamepad1.right_bumper){}
        }
    //negate strafe variables to account for diamond wheel layout
        if(mecanumLayout_isX == false){
            mecanumAdjust = -1;
        }
    //add deadspace so joystick must be pushed 10% to account for accidental taps 
        if(Math.abs(gamepad1.left_stick_y) > .2){
        //x.x, y.y mean decimal input from controller joystick like 1.0 or 0.7
        //set variables for forward/backward situation to left stick y 
            powerDueTo_forwardBackward_frontLeft = gamepad1.left_stick_y;
            powerDueTo_forwardBackward_frontRight = gamepad1.left_stick_y;
            powerDueTo_forwardBackward_backLeft = gamepad1.left_stick_y;
            powerDueTo_forwardBackward_backRight = gamepad1.left_stick_y;
        }
        if(Math.abs(gamepad1.left_stick_x) > .2){
        //set variables for left/right situmacation to left stick x 
            powerDueTo_strafe_frontLeft = -gamepad1.left_stick_x*mecanumAdjust;
            powerDueTo_strafe_frontRight = gamepad1.left_stick_x*mecanumAdjust;
            powerDueTo_strafe_backLeft = gamepad1.left_stick_x*mecanumAdjust;
            powerDueTo_strafe_backRight = -gamepad1.left_stick_x*mecanumAdjust;
        }
        if(Math.abs(gamepad1.right_stick_x) > .2){
        //set variables for turn situation to right stick x 
            powerDueTo_rotate_frontLeft = -gamepad1.right_stick_x*flipRotation*0.7;
            powerDueTo_rotate_frontRight = gamepad1.right_stick_x*flipRotation*0.7;
            powerDueTo_rotate_backLeft = -gamepad1.right_stick_x*flipRotation*0.7;
            powerDueTo_rotate_backRight = gamepad1.right_stick_x*flipRotation*0.7;
        }//setting variables if joystick moved out of deadspace
        
        //set actual motor power variables to average of above varibles for corresponding wheel
            average_motorPower_frontLeft = ((powerDueTo_forwardBackward_frontLeft+powerDueTo_strafe_frontLeft+powerDueTo_rotate_frontLeft)/3);
            average_motorPower_frontRight = ((powerDueTo_forwardBackward_frontRight+powerDueTo_strafe_frontRight+powerDueTo_rotate_frontRight)/3);
            average_motorPower_backLeft = ((powerDueTo_forwardBackward_backLeft+powerDueTo_strafe_backLeft+powerDueTo_rotate_backLeft)/3);
            average_motorPower_backRight = ((powerDueTo_forwardBackward_backRight+powerDueTo_strafe_backRight+powerDueTo_rotate_backRight)/3);
        
        //take abs value of motor powers, find max value, divide all by 
        //normalizes all motors to max at 1 or -1, make sure to not / by 0
            max_motorPower = Math.max(Math.max(Math.abs(average_motorPower_frontLeft), Math.abs(average_motorPower_frontRight)), Math.max(Math.abs(average_motorPower_backLeft), Math.abs(average_motorPower_backRight)));
            if(max_motorPower != 0){
                average_motorPower_frontLeft /= max_motorPower;
                average_motorPower_frontRight /= max_motorPower;
                average_motorPower_backLeft /= max_motorPower;
                average_motorPower_backRight /= max_motorPower;
            }
        //drive motors at power calculated for them
            leftfrontMotor.setPower(average_motorPower_frontLeft*speedMultiplier);
            rightfrontMotor.setPower(average_motorPower_frontRight*speedMultiplier);
            leftbackMotor.setPower(average_motorPower_backLeft*speedMultiplier);
            rightbackMotor.setPower(average_motorPower_backRight*speedMultiplier);
    }//mecanum control
}//public class
