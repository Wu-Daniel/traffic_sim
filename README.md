# traffic_sim
Math 381 group 3 project 2


C_LEN = 5; CAR LENGTH
TRACK_LEN = 1000.0; LENGTH OF SIMULATION TRACK
OBS_LEN = 100.0; LENGTH OF OBSERVED TRACK 
DENSITY = 0.5; CAR DENSITY (PROPORTION OF CAR TO SPACE ON ROAD)
TYPE = 1; BEHAVIOUR TYPE
	
C_PROP = {0.25,0.25,0.25,0.25}; PROPORTION OF CARS PER LANE (LANE 1 PROP, LANE 2 PROP, ...}
B_PROP = {.5,.5}; PROPORTION OF BEHAVIOUR (BEHAVIOUR 1, BEHAVIOUR 2)
	
STEPS = 100; (NUMBER OF STEPS FOR SIMULATION)
STEP_SIZE = .01; (TIME STEP)

DECEL_RATE = 2.0; DECELERATION RATE
ACEL_RATE = 2.0; ACELLERATION RATE
ACCEL_VAR = 1.0; AVERAGE ACCELERATION VARIANCE

AVG_SPEED = 50.0; AVERAGE SPEED OF CAR
SPEED_VAR = 5.0; AVERAGE SPEED VARIANCE
	
SLOW_DIST_THRESH = 10.0; AMOUNT	OF DISTANCE BETWEEN CAR IN FRONT TO TRIGGER SLOW/SWITCH LANES
SLOW_DIST_VAR = 5.0; DISTANCE VARIANCE
	
FWD_SWITCH_THRESH = 10.0; AVERAGE AMOUNT OF DISTANCE NEEDED IN FRONT TO SWITCH
FWD_SWITCH_VAR = 5.0; AVERAGE VARIANCE FOR FRONT DISTANCE
SPD_SWITCH_THRESH = 10.0; AVERAGE AMOUNT OF SPEED DIFFERENCE NEEDED TO SWITCH
SPD_SWITCH_VAR = 5.0; AVERAGE VARIANCE FOR SPEED DIFFERENCE
	
SDE_SWITCH_THRESH = 10.0; AVERAGE AMOUNT OF DISTANCE NEEDED BEHIND SIDE IN LANE TO SWITCH
SDE_SWITCH_VAR = 5.0; AVERAGE VARIANCE FOR BEHIND SIDE DISTANCE
