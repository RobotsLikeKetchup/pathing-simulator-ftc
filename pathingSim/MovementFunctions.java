package pathingSim;


public class MovementFunctions {

    //creates a "direction" for the robot to go in.
    //also returns an optimal angle to go to but that's optional depending on usage
    public static int[] createMovementVector(double[] robotPose, double[] targetPoint) {
        double relativeX = targetPoint[0] - robotPose[0];
        double relativeY = targetPoint[1] - robotPose[1];

        double absoluteAngle = Math.atan2(relativeX,relativeY);

        double relativeAngle = angleWrap(absoluteAngle - robotPose[2]);

        double divisor = Math.max(relativeX, relativeY);


        return new int[] {
                (int) Math.round(10 * relativeX/divisor),
                (int) Math.round(10 * relativeY/divisor),
                (int) Math.round(10 * relativeAngle)
        };
    }

    public static double angleWrap(double angle) {
        while(angle < -Math.PI) {
            angle += 2 * Math.PI;
        }
        while(angle > Math.PI){
            angle -= 2 * Math.PI;
        }
        return angle;
    }
} 
