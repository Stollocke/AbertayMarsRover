# Abertay Mars Rover Documentation

# class `Rover`

_The `Rover` class is the main controller of the mission. It provides abstraction for the main program, exposing uniquely main phases of the mission_

#### `new Rover()`

The constructor creates a new rover instance, and takes care of setting up the sensors, motors and mapping objects. This method shouldn't need modification

#### `Rover.startMission()`

Empty method, that is called by the `Main` program once the Rover is instantiated and ready to go, and before exploration

#### `Rover.mainBehaviour()`

Empty method, called by the `Main` program once `startMission` has returned. this should contain the global code to be used by the rover to explore its environment.

#### `Rover.finishMission()`

Empty method, called by the `Main` program once `mainBehaviour` has returned. It should contain the instruction to wrap up the mission (return to landing point, for example).

#### `Rover.displayUserMessage(String message)`

Interface method. It provides the different units of the rover (mapping, navigation, instrumentation) with a centralised way to display messages on the LCD screen. [Ultimately, should be able to handle a queue of messages.]

***
# Abertay Mars Rover Documentation

# class `AdvancedRoverNavigator`

_`AdvancedRoverNavigator` provides an easy way of moving the rover towards a point on a coordinate system, move it along a path, and orientating it. Once the `AdvancedRoverNavigator` class is instantiated, any motion of the rover will be registered, and the position updated._

#### `new AdvancedRoverNavigator(DifferentialPilot pilot, InstrumentsKit mast, Rover roverObject)`

The constructor creates the Navigator object and registers the rover's instrument array and driving unit, so that it can use them. The roverObject parameter should point to the main Rover class controlling the robot.

#### boolean `goTo(float x, float y)`

Attempts to make the rover reach the point defined by `x,y`. The position of `0,0` is determined by the starting position of the rover. If the point is reached, the function will return true. If the rover was stopped by an obstacle, it will return false.

#### boolean `rotateTo(double targetHeading)`

Attempts to make the rover face the give heading. (0/360 being the direction the rover was facing at the beginning of the mission). If the motion was successful, returns true. If the rover was stopped, return false.

#### boolean `rotateBy(double offsetAngle)`

Attempts to rotate the rover by the given angle, relative to its current direction. Negative angles are to the left, positive to the right. Returns true if the rotation was successful, and false if the rover was stopped.

#### boolean `travelDistance(double targetDistance)`

Attempts to move the rover by the given distance. Returns true if the distance was travelled, false if the rover was stopped

#### `getX()/getY()/getHeading()`

Allow access to the position and orientation variables of the navigation unit.

***
# Abertay Mars Rover Documentation

# class `InstrumentsKit`

_`InstrumentsKit` provides abstraction for the different sensors mounted on the rover: US on a rotating mast, two touch sensors as bumpers._

#### `new InstrumentsKit(NXTRegulatedMotor headMotor, UltrasonicSensor headSonic, TouchSensor leftBumper, TouchSensor rightBumper, Rover roverObject)`

creates the InstrumentsKit instance.

#### boolean `obstacleInRange()`

Returns true if any obstacle is closer to the rover than the safety range (10cm, checked using the Ultrasonic sensor).

#### boolean `bumpersTriggered()`

Returns true if either one of the bumper's touch sensors is detecting contact. If there is a contact, InstrumentsKit.lastBump will be set to -1 (left) or 1 (right). It will be set to 0 otherwise.

#### boolean `obstacleAhead()`

Returns true if the bumpers and/or the ultrasonic range are triggered.

#### int `bestForwardAngle()`

Make a full sweep between -90 and 90 degrees, measuring the headway every 20 degrees. Returns the bearing where there's the most headroom.