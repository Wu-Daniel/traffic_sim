package traffic_sim;

import java.util.*;

public class SimSpace {	
	public static void main(String[] args) {
		System.out.println("sim started ...");
		SimulationSettings settings = new DefaultSettings();
		Simulation simulation = new Simulation(settings, 4);
		System.out.println("sim finish");
	}
}
