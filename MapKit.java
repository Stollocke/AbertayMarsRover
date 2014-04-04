import java.util.*;

import lejos.nxt.*;
import lejos.geom.Point;
import lejos.robotics.navigation.*;
import lejos.robotics.localization.OdometryPoseProvider;
import lejos.util.*;

/**
* Abertay University - SET
* Mars Rover Project
* MapKit class
* 2014-03-27 Simon Stoll | Cesar Parent
*
* MapKit: Provide mapping delegate functions 
* 
* Provide path following functions and waypoint management
*/

public class MapKit {
    
    private ArrayList<Point> pointsMap;
    private Rover rover;
    
    /**
    * Constructor
    *
    * @param Rover roverObjet the Rover isntance piloting the rover
    */
    public MapKit(Rover roverObject) {
        
        this.pointsMap = new ArrayList<Point>();
        this.rover = roverObject;
    }
    
    /**
    * Adds an obtacle, detected at bearing and distance by the rover
    *
    * @param int bearing the relative heading at which the obstacle was detected
    * @param float distance at which the obstacle was detected
    * @return void
    */
    public void addObstacle(int heading, float distance) {
        Pose currentPosition = this.rover.navigationUnit.getPose();
        
        float bearing = (currentPosition.getHeading() + (float)heading) % 360;
        
        Point obstacle = currentPosition.pointAt(distance, bearing);
        this.pointsMap.add(obstacle);
    }
    
    /**
    * Register an obstacle, based on which bumper was touched
    *
    * @param int bump the bump side (-1, 1, or 2)
    * @return void
    */
    public void addBump(int bump) {
        int bearing = 0;
        if(bump == -1 || bump == 1) {
            bearing = bump == -1 ? -45 : 45;
            this.addObstacle(bearing, 10.0f);
        }
        else if(bump == 2) {
            this.addObstacle(0, 3.0f);
        }
    }
    
    /**
    * Returns an 2-dimension array containing X and Y coordinates of detected objects.
    *
    * @return Point[] array of obstacles
    */
    public Point[] getMap() {
        Point[] map = this.pointsMap.toArray(new Point[this.pointsMap.size()]);
        return map;
    } 
}