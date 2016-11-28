package traffic_sim;

public class Auto implements Comparable<Auto>{
	private int exist;
	
	private int lane;
	private double pos;
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
		
		this.lane = lane;
		this.pos = pos;
		this.behaviour = behaviour;		
		
		this.current_speed = current_speed;
		this.desired_speed = desired_speed;
		
		this.decel_rate = decel_rate;
		this.acel_rate = acel_rate;
		
		this.slow_thresh = slow_thresh;
		
		this.fwd_thresh = fwd_thresh;
		this.spd_thresh = spd_thresh;
		
		this.sde_thresh = sde_thresh;		
		
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
	
	public double getPos() {
		return pos;
	}
	
	public double getSpeed() {
		return current_speed;
	}
	
	public int getBehaviour() {
		return behaviour;
	}
	
	public void step(Auto auto_up_f, Auto auto_up_b, Auto auto_down_f, Auto auto_down_b, Auto auto_forward, 
			double step_size) {
		
		if (behaviour == 1) {
			
			if (auto_forward.getPos() - pos < fwd_thresh) {
				current_speed -= decel_rate * step_size;
			} else if (current_speed < desired_speed) {
				current_speed += acel_rate * step_size;
			}
			
			pos += current_speed * step_size;			
		} else  if (behaviour == 2) {
			
		}
	}
	
	public int compareTo(Auto other) {
		return Double.compare(pos,other.pos);
	}
	
	public String toString() {
		if (exist == 0) {
			return "null";
		} else {
			return lane + "," + pos + "," + behaviour + "," + current_speed + "," + desired_speed + "," +
					decel_rate + "," + acel_rate + "," + slow_thresh + "," + fwd_thresh + "," + 
					spd_thresh + "," + sde_thresh;		
		}
	}
}
