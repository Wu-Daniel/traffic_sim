package traffic_sim;

import java.util.*;

public class Simulation {
	public int counter = 0;
	public double currentTime;
	public int carCount = 0;
	
	// This holds previous simulated states so that we can handle reaction speeds
	Map<Double, Map<Integer, List<Auto>>> states = new HashMap<Double, Map<Integer, List<Auto>>>();
	
	public Simulation() {
		this.currentTime = 0;

		Map<Integer, List<Auto>> initialLanes = new HashMap<Integer, List<Auto>>();
		for (int i = 0; i < Settings.laneCount; i++) {
			List<Auto> initialAutos = new ArrayList<Auto>();
			double currentCarPosition = 0;
			for (int j = 0; j < Settings.carCount; j++) {
				initialAutos.add(new Auto(i, currentCarPosition));
				currentCarPosition += Settings.initialDistanceBetweenCars;
			}
			initialLanes.put(i, initialAutos);
		}
		states.put(0.0, initialLanes);
	}
	
	public void step(double timeStep) {
		double lastTime = currentTime;
		currentTime += timeStep;
		
		Map<Integer, List<Auto>> lastState = states.get(lastTime);
		double highestReactionTime = 0;
		for (int i = 0; i < Settings.laneCount; i++) {
			List<Auto> lane = lastState.get(i);
			List<Auto> autosToRemove = new ArrayList<Auto>();
			List<Auto> loopList = new ArrayList<Auto>();
			for (Auto car : lane) {
				loopList.add(car);
			}
			for (Auto car : loopList) {
				double closestTime = 0;
				double desiredTime = currentTime - car.getReactionSpeed();
				for (double stateTime : states.keySet()) {
					if (Math.abs(stateTime - desiredTime) < Math.abs(closestTime - desiredTime)) {
						closestTime = stateTime;
					}
				}
				Map<Integer, List<Auto>> reactionState = states.get(closestTime);
				car.step(reactionState, lastState, timeStep, Settings.looped);
				
				if (car.getReactionSpeed() > highestReactionTime) {
					highestReactionTime = car.getReactionSpeed();
				}
				
				if (!Settings.looped) {
					if (car.getPos() > Settings.trackLength) {
						autosToRemove.add(car);
					}
				} else {
					car.loopTrack(Settings.trackLength);
				}
			}
			lane.removeAll(autosToRemove);
		}
		
		List<Double> removeList = new ArrayList<Double>();
		for (double time : states.keySet()) {
			if (currentTime - highestReactionTime > time) {
				removeList.add(time);
			}
		}
		for (double timeToRemove : removeList) {
			if (timeToRemove != lastTime) {
				states.remove(timeToRemove);
			}
		}
		
		// add new time
		Map<Integer, Double> minPositions = new HashMap<Integer, Double>();
		Map<Integer, List<Auto>> lanes = new HashMap<Integer, List<Auto>>();
		for (int i = 0; i < Settings.laneCount; i++) {
			List<Auto> newLane = new ArrayList<Auto>();
			lanes.put(i, newLane);
			double minPos = Settings.laneCount;
			for (Auto car : states.get(lastTime).get(i)) {
				Auto clone = car.copy();
				newLane.add(clone);
				if (minPos > car.getPos()) {
					minPos = car.getPos();
				}
			}
			minPositions.put(i, minPos);
		}
		
		if (!Settings.looped) {
			carCount = 0;
			for (int key : lastState.keySet()) {
				List<Auto> lane = lastState.get(key);
				carCount += lane.size();
			}
			
			if (counter > Settings.calculateEnterTime() / timeStep) {
				double index = Math.random() * carCount;
				for (int i = 0; i < Settings.laneCount; i++) {
					List<Auto> lane = lanes.get(i);
					if (lane.size() < index) {
						index = index - lane.size();
					} else {
						double positionToAdd = minPositions.get(i);
						if (positionToAdd > 0) {
							positionToAdd = 0;
						}
						lane.add(new Auto(0, positionToAdd - Settings.carSize));
						break;
					}
				}
				counter = 0;
			} else {
				counter ++;
			}
		}
		
		states.put(currentTime, lanes);
	}
	
	public Map<Integer, List<Auto>> snapshot() {
		return states.get(currentTime);
	}
}
