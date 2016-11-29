package traffic_sim;

import java.util.*;

public class Simulation {
	public SimulationSettings settings;
	public double currentTime;
	public int laneCount;
	public double length;
	
	// This holds previous simulated states so that we can handle reaction speeds
	Map<Double, Map<Integer, List<Auto>>> states = new HashMap<Double, Map<Integer, List<Auto>>>();
	
	public Simulation(SimulationSettings settings, int lanes, double length) {
		this.settings = settings;
		this.currentTime = 0;
		this.laneCount = lanes;
		this.length = length;
		
		Map<Integer, List<Auto>> initialLanes = new HashMap<Integer, List<Auto>>();
		List<Auto> initialAutos = new ArrayList<Auto>();
		double distance = settings.generateInitialDistanceBetweenCars();
		for (double position = 0; position < length / 2.5; position += distance) {
			initialAutos.add(new Auto(settings, currentTime, 0, position));
		}
		initialLanes.put(0, initialAutos);
		states.put(0.0, initialLanes);
	}
	
	public void step(double timeStep) {
		double lastTime = currentTime;
		currentTime += timeStep;
		
		Map<Integer, List<Auto>> lastState = states.get(lastTime);
		for (int i = 0; i < laneCount; i++) {
			List<Auto> lane = lastState.get(i);
			for (Auto car : lane) {
				double closestTime = 0;
				double desiredTime = currentTime - car.getReactionSpeed();
				for (double stateTime : states.keySet()) {
					if (Math.abs(stateTime - desiredTime) < Math.abs(closestTime - desiredTime)) {
						closestTime = stateTime;
					}
				}
				Map<Integer, List<Auto>> reactionState = states.get(closestTime);
				car.step(reactionState, lastState, timeStep);
				car.loopTrack(length);
			}
		}
		
		Map<Integer, List<Auto>> lanes = new HashMap<Integer, List<Auto>>();
		for (int i = 0; i < laneCount; i++) {
			List<Auto> newLane = new ArrayList<Auto>();
			lanes.put(i, newLane);
			for (Auto car : states.get(lastTime).get(i)) {
				Auto clone = car.copy();
				newLane.add(clone);
			}
		}
		states.put(currentTime, lanes);
	}
	
	public Map<Integer, List<Auto>> snapshot() {
		return states.get(currentTime);
	}
}
