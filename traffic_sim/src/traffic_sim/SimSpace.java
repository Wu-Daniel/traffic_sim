package traffic_sim;

import java.util.*;

public class SimSpace {
	private static ArrayList<Auto> auto_arr = new ArrayList<Auto>();
	private static double[] c_prop; 
	
	private static final int C_LEN = 5;
	private static final double TRACK_LEN = 1000.0;
	private static final double OBS_LEN = 100.0;

	private static final double DENSITY = 0.5;
	private static final int TYPE = 1;
	
	private static final double[] C_PROP = {0.25,0.25,0.25,0.25};
	private static final double[] B_PROP = {.5,.5};
	
	private static final int STEPS = 100;
	private static final double STEP_SIZE = .01;
	private static final int STEP_REPROP = 10;
	
	private static final double DECEL_RATE = 5.0;
	private static final double ACEL_RATE = 5.0;
	private static final double ACCEL_VAR = 1.0;
	
	private static final double AVG_SPEED = 50.0;
	private static final double SPEED_VAR = 5.0;
	
	private static final double SLOW_DIST_THRESH = 10.0;
	private static final double SLOW_DIST_VAR = 5.0;
	
	private static final double FWD_SWITCH_THRESH = 10.0;
	private static final double FWD_SWITCH_VAR = 5.0;
	private static final double SPD_SWITCH_THRESH = 10.0;
	private static final double SPD_SWITCH_VAR = 5.0;
	
	private static final double SDE_SWITCH_THRESH = 10.0;
	private static final double SDE_SWITCH_VAR = 5.0;
	
	public static void main(String[] args) {
		genInit(TYPE);
		System.out.println("gen finish ... ");
		
	}
	public static void runSim() {
		
	}
	
	public static void runSim(int steps) {
		
	}
	
	public static void genInit(int type) {
		c_prop = C_PROP;
		int num_lane = c_prop.length;
		
		// density = num_car * c_len / track_len
		int cpl =  (int) (num_lane * ((DENSITY * TRACK_LEN / (C_LEN) ) - 1)); 
		
		if (type == 1) {
			for (int i = 0; i < num_lane; i++) {
				for (int j = 0; j < cpl; j++ ) {
					int behav;
					if (Math.random() < .5) {
						behav = 0;
					} else {
						behav = 1;
					}
					Auto temp = new Auto(i,(j * (i % 2)) * (TRACK_LEN / (cpl + 1)), 1 /*behav*/,);
					auto_arr.add(temp);
				}
			}
		}
		
	}
	
	public static void snapshot() {
		
	}
}
