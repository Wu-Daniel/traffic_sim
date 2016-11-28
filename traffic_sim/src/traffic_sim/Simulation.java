package traffic_sim;

import java.util.*;

public class Simulation {
	private SimulationSettings settings;
	private double currentTime;
	private int laneCount;
	
	// This holds previous simulated states so that we can handle reaction speeds
	Map<Double, Map<Integer, List<Auto>>> states = new HashMap<Double, Map<Integer, List<Auto>>>();
	
	public Simulation(SimulationSettings settings, int lanes) {
		this.settings = settings;
		this.currentTime = 0;
		this.laneCount = lanes;
	}
	
	public void Step(double timeStep) {
		currentTime += timeStep;
		Map<Integer, List<Auto>> lanes = new HashMap<Integer, List<Auto>>();
		for (int i = 0; i < laneCount; i++)
		{
			lanes.put(i, new ArrayList<Auto>());
		}
		states.put(currentTime, lanes);
	}
	
	public Map<Integer, List<Auto>> Snapshot() {
		return states.get(currentTime);
	}
}
