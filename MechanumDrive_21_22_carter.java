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
public class MechanumDrive_21_22_carter extends LinearOpMode {
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
   double pastElbowPos;
   double dElbowPos;
   double pastEqui = 0.0D;
   
   //Setup the armAngleControl() variables
int CurrentArmAngle = 0;
int PreviousArmAngle = 0;
int TargetArmAngle = 0;
double MotorValue = 0;
int currentElbowPos;
int currentTablePos;
int startingElbowPos = this.elbow.getCurrentPosition();
int startingTablePos = this.turnTable.getCurrentPosition();
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
         //this.armAngleControl();
         this.additionalControl();
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
      this.currentElbowPos = this.elbow.getCurrentPosition();
      this.currentTablePos = this.turnTable.getCurrentPosition();
      
      this.telemetry.addData("TablePos", this.currentTablePos);
      this.telemetry.addData("elbowPos", this.currentElbowPos);
      
   //turntable and elbow code
   if(1==1/* currentElbowPos >= startingElbowPos + 0 */){
      if(gamepad1.dpad_left /* && currentTablePos >= startingTable-0 */) {
         turnTable.setPower(-1.0);
      } else if(gamepad1.dpad_right /* && currentTablePos <= startingTable+0 */) {
         turnTable.setPower(1.0);
      } else {
         turnTable.setPower(0);
      }
   }
   
   if(gamepad1.dpad_up) {
      elbow.setPower(1.0);
   } else if(gamepad1.dpad_down) {
      elbow.setPower(-1.0);
   } else {
      elbow.setPower(0);
   }
   
   
}
}





