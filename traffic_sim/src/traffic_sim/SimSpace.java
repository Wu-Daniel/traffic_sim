package traffic_sim;

import java.io.PrintWriter;
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
	private static final double ACCEL_RATE = 5.0;
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
		snapshot("input.txt");
	}
	public static void runSim() {
		
	}
	
	public static void runSim(int steps) {
		
	}
	
	public static void genInit(int type) {
		c_prop = C_PROP;
		int num_lane = c_prop.length;
		
		// density = num_car * c_len / track_len
		int cpl =  (int) ((DENSITY * TRACK_LEN / (C_LEN) ) - 1); 
		System.out.println(cpl);
		if (type == 1) {
			for (int i = 0; i < num_lane; i++) {
				for (int j = 0; j < cpl; j++ ) {
					int lane = i + 1;
					double pos = ((j + (i % 2)) * (TRACK_LEN / (cpl + 1)));
					int behav = (int) Math.floor(Math.random()*2 + 1);
					double desired_speed = (Math.random()-.5) * SPEED_VAR + AVG_SPEED;
					double current_speed = desired_speed;
					double decel_rate = (Math.random()-.5) * ACCEL_VAR + DECEL_RATE;
					double accel_rate = (Math.random()-.5) * ACCEL_VAR + ACCEL_RATE;
					double slow_thresh = (Math.random()-.5) * SLOW_DIST_VAR + SLOW_DIST_THRESH;
					double fwd_thresh = (Math.random()-.5) * FWD_SWITCH_VAR + FWD_SWITCH_THRESH;
					double spd_thresh = (Math.random()-.5) * SPD_SWITCH_VAR + SPD_SWITCH_THRESH;
					double sde_thresh = (Math.random()-.5) * SDE_SWITCH_VAR + SDE_SWITCH_THRESH;

					Auto auto = new Auto(lane, pos, behav, desired_speed, current_speed, decel_rate, accel_rate, 
							slow_thresh, fwd_thresh, spd_thresh, sde_thresh);	
					auto_arr.add(auto);
				}
			}
		}
		
	}
	
	public static void snapshot(String filename) {
		System.out.println(auto_arr.size());
		try {
			PrintWriter writer = new PrintWriter(filename, "UTF-8");
			for (Auto auto : auto_arr) {
				writer.println(auto.getLane() + "," + auto.getPos() + "," + auto.getSpeed());
			}
			writer.close();
		} catch (Exception e) {
			System.out.println("ERROR");
		}
	}
}
