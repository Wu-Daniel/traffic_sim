package traffic_sim;

import java.util.*;

public class Auto {
	private int lane;
	private double position;
	private double currentSpeed;
	
	private double switchSpeedDifferential;
	private double desiredSpeed;
	private double desiredDistance;
	private double requiredLaneChangeSpaceInFront;
	private double requiredLaneChangeSpaceBehind;
	private double accelerationSpeed;
	private double brakeSpeed;
	private double size;
	private double reactionSpeed;
	
	public Auto(SimulationSettings settings, double currentTime, int lane, double pos) {
		this.lane = lane;
		this.position = pos;
		
		switchSpeedDifferential = settings.generateSwitchSpeedDifferential(currentTime);
		
		currentSpeed = settings.generateDesiredSpeed(currentTime);
		desiredSpeed = currentSpeed;
		
		desiredDistance = settings.generateDesiredDistance(currentTime);
		
		requiredLaneChangeSpaceInFront = settings.generateRequiredLaneChangeSpaceInFront(currentTime);
		requiredLaneChangeSpaceBehind = settings.generateRequiredLaneChangeSpaceBehind(currentTime);
		
		accelerationSpeed = settings.generateAccelerationSpeed(currentTime);
		brakeSpeed = settings.generateBrakeSpeed(currentTime);
		
		size = settings.generateCarSize(currentTime);
		
		reactionSpeed = settings.generateReactionSpeed(currentTime);
	}

	public int getLane() {
		return lane;
	}
	
	public double getSize() {
		return size;
	}
	
	public double getPos() {
		return position;
	}
	
	public double getSpeed() {
		return currentSpeed;
	}
	
	public double getReactionSpeed() {
		return reactionSpeed;
	}
	
	// Override this to make different behaviors
	public void handleLaneChange(
			Map<Integer, List<Auto>> state,
			Auto carInFront,
			boolean canChangeLeft,
			boolean canChangeRight,
			boolean frontConstrained) {
		// TODO: Still need to implement this.
	}
	
	public void step(
			Map<Integer, List<Auto>> state, 
			double stepSize) {
		// Update position
		this.position += stepSize * Conversions.HoursPerSecond * this.currentSpeed * Conversions.FeetPerMile;
		//   feet     += seconds  * hours per second           * miles per hour    * feet per mile
		
		List<Auto> currentLane = state.get(this.lane);
		
		// Find the next car in the current lane
		Auto nextCar = null;
		for (Auto car : currentLane) {
			double carPos = car.getPos();
			if (carPos > this.position) {
				if (nextCar == null || carPos < nextCar.getPos()) {
					nextCar = car;
				}
			}
		}
		
		boolean frontConstrained = false;
		if (nextCar == null) { // No car in front of me
			updateSpeedNoConstraint(stepSize);
		} else { // There is a car in front of me
			double desiredPosition = nextCar.getPos() - this.desiredDistance;
			if (this.position < desiredPosition) {
				updateSpeedNoConstraint(stepSize);
			} else {
				frontConstrained = true;
				deccelerate(stepSize);
			}
		}
		
		boolean canChangeLeft = false;
		if (state.containsKey(lane + 1)) {
			List<Auto> nextLane = state.get(lane + 1);
			canChangeLeft = true;
			for (Auto leftCar : nextLane) {
				if (isBlockingLaneChange(leftCar)) {
					canChangeLeft = false;
				}
			}
		}
		
		boolean canChangeRight = false;
		if (lane > 0) {
			List<Auto> previousLane = state.get(lane - 1);
			canChangeRight = true;
			for (Auto rightCar : previousLane) {
				if (isBlockingLaneChange(rightCar)) {
					canChangeRight = false;
				}
			}
		}
		
		handleLaneChange(state, nextCar, canChangeLeft, canChangeRight, frontConstrained);
	}
	
	private boolean isBlockingLaneChange(Auto car) {
		double otherPosition = car.getPos();
		// If the car is in the range from the space required behind to the space required in front, we can't
		// lane change.
		return 
			otherPosition + car.getSize() > this.position - this.requiredLaneChangeSpaceBehind &&
			otherPosition < this.position + this.size + this.requiredLaneChangeSpaceInFront;
	}
	
	private void updateSpeedNoConstraint(double stepSize) {
		if (this.currentSpeed < this.desiredSpeed) {
			accelerate(stepSize);
		} else if (this.currentSpeed > this.desiredSpeed) { // I dont think this can happen but w/e
			deccelerate(stepSize);
		}
	}
	
	private void accelerate(double stepSize) {
		this.currentSpeed += stepSize * Conversions.HoursPerSecond * this.accelerationSpeed;
		// miles per hour += seconds  * hours per second           * miles per hour per hour
	}
	
	private void deccelerate(double stepSize) {
		this.currentSpeed -= stepSize * Conversions.HoursPerMinute * this.brakeSpeed;
		// miles per hour += seconds  * hours per second           * miles per hour per hour
	}
}
