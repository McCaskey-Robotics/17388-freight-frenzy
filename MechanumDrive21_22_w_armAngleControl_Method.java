/* Decompiler 20ms, total 78ms, lines 158 */
package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Blinker;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Gyroscope;
import com.qualcomm.robotcore.hardware.DcMotor.ZeroPowerBehavior;
import com.qualcomm.robotcore.hardware.DcMotorSimple.Direction;
import com.qualcomm.robotcore.util.ElapsedTime;

@TeleOp
public class MechanumDrive21_22_w_armAngleControl_Method extends LinearOpMode {
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
   private ElapsedTime runtime = new ElapsedTime();
   double speedMultiplier = 1.0D;
   int flipRotation = 1;
   boolean duckWheel = false;
   double tablePos;
   double elbowPos;
   double pastElbowPos;
   double dElbowPos;
   double pastEqui = 0.0D;
   
   //Setup the armAngleControl() variables
int CurrentArmAngle = 0;
int PreviousArmAngle = 0;
int TargetArmAngle = 0;
double MotorValue = 0;
//This keeps a record of the current motor value so it can be
//manipulated by the code instead of set at a number.
double MeasuredVelocity = 0;
//Change this to whatever the max motor value is on your motor and program
double MaxMotorValue = 1.0;
//Get the initial arm angle reading
//PreviousArmAngle = this.elbow.getCurrentPosition();

   public void runOpMode() {
      this.brMotor = (DcMotor)this.hardwareMap.get(DcMotor.class, "brMotor");
      this.frMotor = (DcMotor)this.hardwareMap.get(DcMotor.class, "frMotor");
      this.blMotor = (DcMotor)this.hardwareMap.get(DcMotor.class, "blMotor");
      this.flMotor = (DcMotor)this.hardwareMap.get(DcMotor.class, "flMotor");
      this.spinner = (DcMotor)this.hardwareMap.get(DcMotor.class, "spinner");
      this.turnTable = (DcMotor)this.hardwareMap.get(DcMotor.class, "turnTable");
      this.elbow = (DcMotor)this.hardwareMap.get(DcMotor.class, "elbow");
      this.intake = (CRServo)this.hardwareMap.get(CRServo.class, "intake");
      this.flMotor.setDirection(Direction.REVERSE);
      this.blMotor.setDirection(Direction.REVERSE);
      this.elbow.setDirection(Direction.REVERSE);
      this.spinner.setZeroPowerBehavior(ZeroPowerBehavior.BRAKE);
      this.elbow.setZeroPowerBehavior(ZeroPowerBehavior.BRAKE);
      this.turnTable.setZeroPowerBehavior(ZeroPowerBehavior.BRAKE);
      this.telemetry.addData("Status", "Initialized");
      this.telemetry.update();
      this.waitForStart();

      while(this.opModeIsActive()) {
         this.mecanumControl();
         this.armAngleControl();
         //this.additionalControl();
         this.telemetry.update();
      }

   }

   public void mecanumControl() {
      float frontPower = -this.gamepad1.left_stick_y;
      float strafePower = this.gamepad1.left_stick_x;
      float rotatePower = this.gamepad1.right_stick_x;
      telemetry.addData("Left Stick X:",this.gamepad1.left_stick_x);
      telemetry.addData("Left Stick Y:",this.gamepad1.left_stick_y);
      telemetry.addData("Right Stick X:",this.gamepad1.right_stick_x);
      
      double brPower = (double)(frontPower - rotatePower + strafePower);
      double blPower = (double)(frontPower + rotatePower - strafePower);
      double frPower = (double)(frontPower - rotatePower - strafePower);
      double flPower = (double)(frontPower + rotatePower + strafePower);
      if (Math.abs(brPower) > 1.0D) {
         brPower = Math.abs(brPower) / brPower;
      }

      if (Math.abs(frPower) > 1.0D) {
         frPower = Math.abs(frPower) / frPower;
      }

      if (Math.abs(blPower) > 1.0D) {
         blPower = Math.abs(blPower) / blPower;
      }

      if (Math.abs(flPower) > 1.0D) {
         flPower = Math.abs(flPower) / flPower;
      }

      if (Math.abs(blPower) > 1.0D) {
         blPower = Math.abs(blPower) / blPower;
      }

      this.brMotor.setPower(brPower);
      this.frMotor.setPower(frPower);
      this.blMotor.setPower(blPower);
      this.flMotor.setPower(flPower);
   
   }

   public void additionalControl() {
      this.elbowPos = (double)this.elbow.getCurrentPosition();
      this.tablePos = (double)this.turnTable.getCurrentPosition();
      
      this.telemetry.addData("TablePos", this.tablePos);
      this.telemetry.addData("elbowPos", this.elbowPos);
      
      if (this.gamepad1.dpad_left) {
         if (this.elbowPos > -20.0D && this.tablePos <= 15.0D) {
            this.telemetry.addData("stopped left", "");
         } else {
            this.turnTable.setPower(-0.5D);
         }
      } else if (this.gamepad1.dpad_right) {
         if (this.elbowPos < 0.0D && this.tablePos >= 72.0D) {
            this.telemetry.addData("stopped right", "");
         } else {
            this.turnTable.setPower(0.5D);
         }
      } else {
         this.turnTable.setPower(0.0D);
      }
      
   
      //Manual up/down motion of arm
      if (this.gamepad1.dpad_up) {
         this.elbow.setPower(-0.05D);
      } else if (this.gamepad1.dpad_down) {
         this.elbow.setPower(0.05D);
         this.pastEqui = 0.0D;
      } else {
         this.elbow.setPower(0.0D);
      }
      
      /*
      this.elbowPos = (double)this.elbow.getCurrentPosition();
      if ((int)this.runtime.milliseconds() % 10 == 0) {
         this.pastElbowPos = this.elbowPos;
      }
      */

      this.dElbowPos = this.elbowPos - this.pastElbowPos;
      this.telemetry.addData("deltaElbowPos: ", this.elbowPos - this.pastElbowPos);
      
      
      
      if (!this.gamepad1.dpad_up && !this.gamepad1.dpad_down) {
         if (this.elbowPos < this.pastElbowPos) {
            this.elbow.setPower(0.05);
         } else if (this.elbowPos > this.pastElbowPos) {
            this.elbow.setPower(-0.05);
         } else {
            this.elbow.setPower(0.0D);
         }
      }

      if (this.gamepad1.left_bumper) {
         this.spinner.setPower(0.3D);
      } else if (this.gamepad1.right_bumper) {
         this.spinner.setPower(-0.3D);
      } else {
         this.spinner.setPower(0.0D);
      }

      if (!this.gamepad1.a && (double)this.gamepad1.left_stick_y < -0.1D) {
      }
      
      pastElbowPos = elbowPos;//this is how we make it the past
   }
   
   
   
   
   
   //Method for holding a robot arm at a certain angle when gravity is trying to pull it down
public void armAngleControl(){



//Copy and paste the following code within the star comment to above your runOpMode code (make sure to uncomment it up there)
/*
//Setup the armAngleControl() variables
int CurrentArmAngle = 0;
int PreviousArmAngle = 0;
int TargetArmAngle = 0;
float MotorValue = 0;
//This keeps a record of the current motor value so it can be
//manipulated by the code instead of set at a number.
float MeasuredVelocity = 0;
//Change this to whatever the max motor value is on your motor and program
float MaxMotorValue = 1.0;
//Get the initial arm angle reading
PreviousArmAngle = elbowMotor.getCurrentPosition();
*/




/*Only get the previous arm angle if the speed of arm is not measuring zero because it could
still be moving even if v=0 (at first v=0 when it's actually moving at 20 degrees per second
and this code adjusts sensitivity until real v=0)*/
if(MeasuredVelocity!=0)
{
PreviousArmAngle = this.elbow.getCurrentPosition();
}
sleep(5);
CurrentArmAngle = this.elbow.getCurrentPosition();
MeasuredVelocity = CurrentArmAngle - PreviousArmAngle;
//While the buttons are pressed, slowly add power to the motors until max power.
while(this.gamepad1.dpad_up && MotorValue<MaxMotorValue)
{
MotorValue = MotorValue+0.05; //Positive power for up.
this.elbow.setPower(MotorValue);
sleep(10);
//Force it to have a starting velocity so it can start reading the PreviousArmAngle
MeasuredVelocity=1;
}
while(this.gamepad1.dpad_down && MotorValue>-MaxMotorValue)
{
MotorValue = MotorValue-0.05; //Negative power for down.
this.elbow.setPower(MotorValue);
sleep(10);
//Force it to have a starting velocity so it can start reading the PreviousArmAngle
MeasuredVelocity=1;
}
//fix overshot target angle on the way up until the arm stops moving
if(MeasuredVelocity > 0 && MotorValue>-MaxMotorValue && !this.gamepad1.dpad_up && !this.gamepad1.dpad_down)
{
MotorValue=MotorValue-0.05;
this.elbow.setPower(MotorValue);
}
//fix overshot target angle on the way down until the arm stops moving
else if(MeasuredVelocity < 0 && MotorValue<MaxMotorValue && !this.gamepad1.dpad_up && !this.gamepad1.dpad_down)
{
MotorValue=MotorValue+0.05;
this.elbow.setPower(MotorValue);
}
}
}





