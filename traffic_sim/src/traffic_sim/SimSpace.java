package traffic_sim;

public class SimSpace {
	private static Auto[] auto_arr;
	
	private static final int C_LEN = 5;
	private static final double TRACK_LEN = 1000.0;
	private static final double OBS_LEN = 100.0;

	private static final double density = 0.5;
	private static final int type = 1;
	
	private static final double[] C_PROP = {0.25,0.25,0.25,0.25};
	private static final double[] B_PROP = {.5,.5};
	
	private static final int STEPS = 100;
	private static final double STEP_SIZE = .01;
	
	private static final double DECEL_RATE = 2.0;
	private static final double ACEL_RATE = 2.0;
	
	private static final double AVG_SPEED = 50.0;
	
	private static final double FWD_SWITCH_THRESH = 10.0;
	private static final double SPD_SWITCH_THRESH = 10.0;
	
	private static final double BWD_SWITCH_THRESH = 10.0;
	private static final double SDE_SWITCH_THRESH = 10.0;
	
	public static void main(String[] args) {
		genInit(type);
		
		
	}
	public static void runSim() {
		
	}
	
	public static void runSim(int steps) {
		
	}
	
	public static void genInit(int type) {
		if (type == 1) {
			
		}
		
	}
	
	public static void Snapshot() {
		
	}
}
