package traffic_sim;

public class Auto {
	private int exist;
	
	private int lane;
	private int pos;
	private int behaviour;
	
	private double current_speed;
	private double desired_speed;
	
	private double decel_rate;
	private double acel_rate;
	
	private double slow_thresh;
	
	private double fwd_thresh;
	private double spd_thresh;
	
	private double sde_thresh;
	
	public Auto() {
		exist = 0;
	}
	
	public Auto(int lane, double pos, int behaviour, double current_speed, double desired_speed, 
			double decel_rate, double acel_rate, double slow_thresh, double fwd_thresh, 
			double spd_thresh, double sde_thresh) {
		
		lane = this.lane;
		pos = this.pos;
		behaviour = this.behaviour;		
		
		current_speed = this.current_speed;
		desired_speed = this.desired_speed;
		
		decel_rate = this.decel_rate;
		acel_rate = this.acel_rate;
		
		slow_thresh = this.slow_thresh;
		
		fwd_thresh = this.fwd_thresh;
		spd_thresh = this.spd_thresh;
		
		sde_thresh = this.sde_thresh;		
		
		exist = 1;
	}
	
	public boolean exist() {
		if (exist == 1) {
			return true;
		} else {
			return false;
		}
	}

	public int getLane() {
		return lane;
	}
	
	public int getPos() {
		return pos;
	}
	
	public double getSpeed() {
		return current_speed;
	}
	
	public void step(Auto auto_up_f, Auto auto_up_b, Auto auto_down_f, Auto auto_down_b, Auto auto_forward, 
			double step_size) {
		
		if (behaviour == 1) {
			if (current_speed < desired_speed) {
				if (auto_forward.exist()) {
					if (auto_forward.getPos() - pos < fwd_thresh) {
						if (auto_forward.getSpeed() < current_speed) {
							current_speed -= decel_rate * step_size;
						}
					} else {
						current_speed += acel_rate * step_size;
					}
				} else {
					current_speed += acel_rate * step_size;
				}
			}
		} else  if (behaviour == 2) {
			
		}
	}
}
