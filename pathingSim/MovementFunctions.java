package pathingSim;


public class MovementFunctions {

    //creates a "direction" for the robot to go in.
    //also returns an optimal angle to go to but that's optional depending on usage
    public static double[] createMovementVector(double[] robotPose, double[] targetPoint) {
        double relativeX = targetPoint[0] - robotPose[0];
        double relativeY = targetPoint[1] - robotPose[1];

        double absoluteAngle = Math.atan2(relativeY,relativeX);

        double relativeAngle = angleWrap( -(absoluteAngle+Math.PI/2) - (robotPose[2]));

        double divisor = Math.max(Math.abs(relativeX), Math.abs(relativeY));

        System.out.println("relative angle: " + Math.toDegrees(relativeAngle));


        return new double[] {
                -Math.sin(relativeAngle), // WHY IS THIS NEGATIVE IDK WHY??
                Math.cos(relativeAngle),
                relativeAngle
        };
    }


    //wraps the angle so it stays within -pi and pi
    public static double angleWrap(double angle) {
        while(angle < -Math.PI) {
            angle += 2 * Math.PI;
        }
        while(angle > Math.PI){
            angle -= 2 * Math.PI;
        }
        return angle;
    }

    public static double proportionalAngleCorrection(double optimalAngle, double robotAngle) {
        return angleWrap(optimalAngle - robotAngle);
    }
} 
