package traffic_sim;

import java.io.PrintWriter;
import java.util.*;

public class SimSpace {
	
	private static ArrayList<ArrayList<Auto>> auto_dat = new ArrayList<ArrayList<Auto>>();
	private static ArrayList<int[]> sim_count = new ArrayList<int[]>();
	private static double[] c_prop; 
	private static int[] count;

	private static final int C_LEN = 5;
	private static final double TRACK_LEN = 1000.0;
	private static final double OBS_POINT = 100.0;

	private static final double DENSITY = 0.5;
	private static final int TYPE = 1;
	
	private static final double[] C_PROP = {0.25,0.25,0.25,0.25};
	private static final double[] B_PROP = {.5,.5};
	
	private static final int STEPS = 100;
	private static final double STEP_SIZE = .01;
	private static final int STEP_REPROP = 10;
	
	private static final double DECEL_RATE = 10.0;
	private static final double ACCEL_RATE = 10.0;
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
		sort();
		snapshot("input.txt");
		runSim(100);
		snapshot("post_sim.txt");
		result();
	}
	public static void runSim() {
		
	}
	
	public static void runSim(int steps) {
		System.out.println("running Simulations for : " + steps + " steps");
		for (int i = 0; i < steps; i++) {
			add();
			restruct();
			sort();
			iterate();
			compare();
			remove();
		}
		System.out.println("simulation complete ... ");
		System.out.println();
	}
	
	public static void add() {
		
	}
	
	public static void restruct() {
		for (int i = 0; i < auto_dat.size(); i++) {
			ArrayList<Auto> temp = auto_dat.get(i);
			for (int j = 0; j < temp.size(); j++) {
				int lane_t = temp.get(j).getLane();
				if (lane_t != i + 1) {
					Auto auto_t = temp.get(j);
					temp.remove(j);
					auto_dat.get(lane_t-1).add(auto_t);
				}
			}
		}
	}	
	
	public static void sort() {
		for (int i = 0; i < auto_dat.size(); i++) {
			Collections.sort(auto_dat.get(i));
			//ArrayList<Auto> temp = auto_dat.get(i);
			//Collections.sort(temp);
			
		}
	}
		
	public static void iterate() {
		count = new int[4];
		
		Auto auto_up_f;
		Auto auto_up_b;
		Auto auto_down_f;
		Auto auto_down_b;
		Auto auto_forward; 
		
		for (int i = 0; i < auto_dat.size(); i++) {
			ArrayList<Auto> lane_arr = auto_dat.get(i);
					
			for (int j = 0; j < lane_arr.size(); j++) {
				Auto target = lane_arr.get(j);
				
				if (i == auto_dat.size() - 1) {
					auto_up_f = new Auto();
					auto_up_b = new Auto();					
				} else {
					ArrayList<Auto> up = auto_dat.get(i+1);
					Auto[] up_arr = findFwdBwd(target,up);
					auto_up_f = up_arr[0];
					auto_up_b = up_arr[1];
				}
				
				if (i == 0) {
					auto_down_f = new Auto();
					auto_down_b = new Auto();				
				} else {
					ArrayList<Auto> down = auto_dat.get(i-1);
					Auto[] down_arr = findFwdBwd(target,down);
					auto_down_f = down_arr[0];
					auto_down_b = down_arr[1];
				}
				if (j == lane_arr.size() - 1) {
					auto_forward = new Auto();
				} else {
					auto_forward = lane_arr.get(j+1);
				}
				
				/*
				System.out.println(target.toString());
				System.out.println(auto_up_f.toString());
				System.out.println(auto_up_b.toString());
				System.out.println(auto_down_f.toString());
				System.out.println(auto_down_b.toString());
				System.out.println(auto_forward.toString());
				System.out.println();
				*/
				
				boolean pass = target.step(auto_up_f, auto_up_b, auto_down_f, auto_down_b, auto_forward, STEP_SIZE, OBS_POINT);
				if (pass) {
					count[target.getLane() - 1] ++;
				}
			}		
		}
	}
	
	public static void compare() {
		//System.out.println(Arrays.toString(count));
		sim_count.add(count);
	}
	
	public static void remove() {
		
	}
	
	public static Auto[] findFwdBwd(Auto target, ArrayList<Auto> lane) {
		
		int ind; Auto temp;
		Auto[] result = new Auto[2];
		if (lane.get(0).getPos() < target.getPos()) {
			ind = 0;
			temp = lane.get(ind);
			while (temp.getPos() < target.getPos() && ind != lane.size() - 1) {
				ind ++;
				temp = lane.get(ind);
			}
			if (ind == lane.size() - 1) {
				result[0] = new Auto();
				result[1] = lane.get(ind);
			} else {
				result[0] = lane.get(ind);
				result[1] = lane.get(ind - 1);
			}
		} else {
			result[0] = lane.get(0);
			result[1] = new Auto();
		}
		return result;
	}
		
	public static void genInit(int type) {
		System.out.println("genInit start ...");
		
		c_prop = C_PROP;
		int num_lane = c_prop.length;
		
		// density = num_car * c_len / track_len
		int cpl =  (int) ((DENSITY * TRACK_LEN / (C_LEN) ) - 1); 
		System.out.println("cpl : " + cpl);
		if (type == 1) {
			for (int i = 0; i < num_lane; i++) {
				ArrayList<Auto> lane_arr = new ArrayList<Auto>();
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

					/*
					Auto auto = new Auto(lane, pos, behav, desired_speed, current_speed, decel_rate, accel_rate, 
							slow_thresh, fwd_thresh, spd_thresh, sde_thresh);	
					*/
					
					Auto auto = new Auto(lane, pos, 1, desired_speed, current_speed, decel_rate, accel_rate, 
							slow_thresh, fwd_thresh, spd_thresh, sde_thresh);	
					
					
					lane_arr.add(auto);
				}
				auto_dat.add(lane_arr);
			}
		}
		System.out.println("gen finish ... ");
		System.out.println();
	}
	
	public static void snapshot(String filename) {
		try {
			System.out.println("snapshot saving to : " + filename);
			PrintWriter writer = new PrintWriter(filename, "UTF-8");
			for (ArrayList<Auto> auto_arr : auto_dat) {
				for (Auto auto : auto_arr) {
					//writer.println(auto.getLane() + "," + auto.getPos() + "," + auto.getBehaviour() + ","+ auto.getSpeed());
					writer.println(auto.toString());
				}
			}
			writer.close();
			System.out.println("snapshot complete ...");
			System.out.println();
		} catch (Exception e) {
			System.out.println("ERROR");
		}
	}
	
	public static void result() {
		int[] temp = new int[4];
		for (int i = 0; i < sim_count.size();i ++) {
			int[] temp1 = sim_count.get(i);
			temp[0] += temp1[0];
			temp[1] += temp1[1];
			temp[2] += temp1[2];
			temp[3] += temp1[3];
		}
		try {
			PrintWriter writer = new PrintWriter("total throughput", "UTF-8");
			writer.println(Arrays.toString(temp));
		} catch (Exception e) {
			System.out.println("ERROR");
		}
		System.out.println(Arrays.toString(temp));
	}
}
