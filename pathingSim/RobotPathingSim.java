package pathingSim;

import javax.swing.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Line2D;

public class RobotPathingSim {


    //for now, negative numbers don't work because this is directly scaled to the java window coordinate frame
    //TODO: fix later
    public static final double[][] PATH = {
        {60,60},
        {290, 120},
        {220,165},
        {240, 210},
        {150, 195}
    };

    static PurePursuit pathing = new PurePursuit(PATH);
    static robotSimPanel robotSim = new robotSimPanel(PATH);
    static JLabel label = new JLabel("Robot Coordinates: (" + robotSim.getRobotX() + ", " + robotSim.getRobotY() + ") Robot Angle: " + robotSim.getRobotA());
    static JTextArea out = new JTextArea("out");

    private static void createAndShowGUI() {
        //Create and set up the window.
        JFrame frame = new JFrame("Pathing Sim");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel topSection = new JPanel();

        frame.add(topSection, BorderLayout.NORTH);


        
        frame.getContentPane().add(robotSim, BorderLayout.CENTER);

        label.setMinimumSize(new Dimension(20,40));
        topSection.add(label, BorderLayout.WEST);

        JButton start = new JButton("start");
        start.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e){
                pathSimulator();
            }
        });
        topSection.add(start, BorderLayout.EAST);

        frame.add(topSection, BorderLayout.NORTH);

        JScrollPane scrollPane = new JScrollPane(out);
        scrollPane.setPreferredSize(new Dimension(600, 100));

        frame.add(scrollPane, BorderLayout.SOUTH);

        //Display the window.
        frame.pack();
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        //Schedule a job for the event-dispatching thread:
        //creating and showing this application's GUI.
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
    }

    private static void pathSimulator() {
        Ticker timeCount = new Ticker();  
        MotionProfile1D motionProfile = new MotionProfile1D(0.3, 0.02, timeCount);     
        Timer timer;

        ActionListener loop = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                timeCount.up();
                double[] goalPoint = pathing.findPointOnPath(robotSim.getRobotX(), robotSim.getRobotY(), robotSim.getRobotA());
                int[] direction = MovementFunctions.createMovementVector(new double[] {robotSim.getRobotX(), robotSim.getRobotY(), Math.toRadians(robotSim.getRobotA())}, goalPoint);
                System.out.println("moving in direction: [" + direction[0] + ", " + direction[1] + ", " + direction[2] + "]");
                robotSim.moveRobotInDirection(direction[0], direction[1], direction[2], motionProfile.getTargetSpeed());
                robotSim.setTargetPoint((int) Math.round(goalPoint[0]), (int) Math.round(goalPoint[1]));
            
                label.setText("Robot Coordinates: (" + robotSim.getRobotX() + ", " + robotSim.getRobotY() + ") Robot Angle: " + robotSim.getRobotA());

                
                out.append("\n Last found index: " + pathing.getLastFoundIndex());
                if(pathing.getDistanceFromEnd() <= 25) {
                    motionProfile.startSlowDown();
                }
        
                if (motionProfile.currentPhase == MotionProfile1D.Phase.STOPPED) {
                    ((Timer) e.getSource()).stop();
                }
            }
        };
           
        timer = new Timer(100, loop);
        timer.setRepeats(true);
        timer.setInitialDelay(2);
        timer.start();
        motionProfile.startSpeedUp();
    }
                
}
                
class robotSimPanel extends JPanel {
                
    final double PIXEL_CONSTANT = 1;
    final int robotWidth = 20;
    final double r = (robotWidth/2)*Math.sqrt(2);
                
    public int robotX = 20;
    public int robotY = 20;
    public double robotA = 0;
    double path[][];
    public int[] targetPoint = {20, 20};
                
    public robotSimPanel(double[][] robotPath) {
        setBorder(BorderFactory.createLineBorder(Color.black));
        path = robotPath;
        setPreferredSize(getPreferredSize());
    }
                
    public Dimension getPreferredSize() {
        return new Dimension(600,600);
    }
                
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        BasicStroke pathStroke = new BasicStroke(2,
            BasicStroke.CAP_BUTT,
            BasicStroke.JOIN_MITER,
            10.0f, new float[] {8f, 4f}, 0.0f);

        BasicStroke straightLine = new BasicStroke(1);

        BasicStroke boldLine = new BasicStroke(3);

        Graphics2D g2D = (Graphics2D) g.create();
                
        
        g2D.setStroke(boldLine);
        g2D.setColor(new Color(0,0,255));
        g2D.drawRect(targetPoint[0], targetPoint[1], 3, 3);
        
        //calculating points of robot "square" based on angle
        Point a = new Point((int) Math.round(robotX - (r * Math.sin(robotA+(Math.PI/4)))), (int) Math.round(robotY - (r*Math.cos(robotA+(Math.PI/4)))));
        Point b = new Point((int) Math.round(robotX - (r * Math.sin(robotA-(Math.PI/4)))), (int) Math.round(robotY - (r*Math.cos(robotA-(Math.PI/4)))));
        Point c = new Point((int) Math.round(robotX - (r * Math.sin(robotA+(3*Math.PI/4)))), (int) Math.round(robotY - (r*Math.cos(robotA+(3*Math.PI/4)))));
        Point d = new Point((int) Math.round(robotX - (r * Math.sin(robotA-(3*Math.PI/4)))), (int) Math.round(robotY - (r*Math.cos(robotA-(3*Math.PI/4)))));
               
        g2D.setStroke(straightLine);
        g2D.setColor(new Color(255, 0, 0));
        g2D.draw(new Line2D.Float(a, b));

        
        g2D.setColor(new Color(0,0,0));
        g2D.draw(new Line2D.Float(b,d));
        g2D.draw(new Line2D.Float(c,d));
        g2D.draw(new Line2D.Float(c,a));

        g2D.setColor(new Color(73, 196, 51));
        g2D.drawOval(robotX-(30), robotY-(30), 60,60);

        
        g2D.setStroke(pathStroke);
        g2D.setColor(new Color(0,0,0));

        for(int i=1; i < path.length; i++) {
            //draw the path
            g2D.drawLine((int) Math.round(path[i-1][0] * PIXEL_CONSTANT), (int) Math.round(path[i-1][1] * PIXEL_CONSTANT), (int) Math.round(path[i][0] * PIXEL_CONSTANT), (int) Math.round(path[i][1] * PIXEL_CONSTANT));
            //System.out.println("drew path line " + i + " of " + (path.length-1));
        }

        
                
    }
                
    private void moveRobotToLocation(int x, int y, double theta, boolean drawPath) {
        if ((robotX!=x) || (robotY!=y)) {
            robotX=x;
            robotY=y;
            robotA = theta;
            repaint();
        } 
    }
                
    public void moveRobotInDirection(int x, int y, int theta, double speed) {
        int robotNewX = robotX + (int) Math.round(x*speed);
        int robotNewY = robotY + (int) Math.round(y*speed);
        double robotNewA = Math.toRadians(robotA) + Math.round(theta*speed);
        
        moveRobotToLocation(robotNewX, robotNewY, robotNewA, true);
    }


    public int getRobotX() {
        return robotX;
    }

    public int getRobotY() {
        return robotY;
    }

    public double getRobotA() {
        return robotA;
    }

    public void setTargetPoint(int x, int y) {
        targetPoint[0] = x;
        targetPoint[1] = y;
        repaint();
    }


}