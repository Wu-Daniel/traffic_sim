package traffic_sim;

public class Auto {
	private int lane;
	private int pos;
	private int behaviour;
	private double current_speed;
	private double desired_speed;
	private double decel_rate;
	private double acel_rate;
	private double fwd_thresh;
	private double bwd_thresh;
	
	public Auto(int lane, double pos, int behaviour, double current_speed, double desired_speed, 
			double decel_rate, double acel_rate, double fwd_thresh, double bwd_thresh) {
		lane = this.lane;
		pos = this.pos;
		behaviour = this.behaviour;		
		current_speed = this.current_speed;
		desired_speed = this.desired_speed;
		decel_rate = this.decel_rate;
		acel_rate = this.acel_rate;
		fwd_thresh = this.fwd_thresh;
		bwd_thresh = this.bwd_thresh;
	}
	
	
}
