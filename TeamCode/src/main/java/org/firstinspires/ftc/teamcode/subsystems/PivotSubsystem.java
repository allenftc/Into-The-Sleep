package org.firstinspires.ftc.teamcode.subsystems;

import com.arcrobotics.ftclib.command.SubsystemBase;
import com.arcrobotics.ftclib.controller.PIDController;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.teamcode.PivotConstants;
import org.firstinspires.ftc.teamcode.SlideConstants;
import org.firstinspires.ftc.teamcode.lib.AnalogEncoder;
import org.firstinspires.ftc.teamcode.lib.SquIDController;
import org.firstinspires.ftc.teamcode.lib.Util;

public class PivotSubsystem extends SubsystemBase {
    private DcMotor motor0, motor1;
    private double currentPos = 0;
    private PIDController pid = new PIDController(SlideConstants.kP, SlideConstants.kI, SlideConstants.kD);
    private SquIDController squid = new SquIDController();
    AnalogEncoder encoder;


    public PivotSubsystem(HardwareMap hMap) {
        motor0 = hMap.dcMotor.get("tilt0");
        motor0.setDirection(DcMotorSimple.Direction.REVERSE);
        motor1 = hMap.dcMotor.get("tilt1");
        motor1.setDirection(DcMotorSimple.Direction.REVERSE);
        encoder = new AnalogEncoder("sensOrange", hMap);
    }
    @Override
    public void periodic() {
        currentPos = encoder.getAngle();
    }
    public void setPower(double power) {
        motor0.setPower(power*PivotConstants.direction);
        motor1.setPower(-power*PivotConstants.direction);
    }
    public void tiltToPos(double target) {
        encoder.setInverted(PivotConstants.encoderInvert);
        encoder.setPositionOffset(PivotConstants.offset);

        squid.setPID(PivotConstants.kP);

        double power = squid.calculate(target, getCurrentPosition());
        if (currentPos < PivotConstants.topPos && power < 0) {
            power = 0;
        }
        setPower(power);

    }
    /**
    * @param target in inches, use the same one as the pid target
    */
    public boolean isClose(double target) {
        return Util.inRange(target, currentPos, PivotConstants.tolerance);
    }

    public double getCurrentPosition() {
        return currentPos;
    }

    public void stop() {
        motor0.setPower(0);
        motor1.setPower(0);
    }
}
