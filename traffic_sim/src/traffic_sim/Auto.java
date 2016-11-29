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
	
	public boolean constrained = false;
	
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
	
	public Auto(
			int lane,
			double position,
			double currentSpeed,
			double switchSpeedDifferential,
			double desiredSpeed,
			double desiredDistance,
			double requiredLaneChangeSpaceInFront,
			double requiredLaneChangeSpaceBehind,
			double accelerationSpeed,
			double brakeSpeed,
			double size,
			double reactionSpeed,
			boolean constrained) {
		this.lane = lane;
		this.position = position;
		this.currentSpeed = currentSpeed;
		this.switchSpeedDifferential = switchSpeedDifferential;
		this.desiredSpeed = desiredSpeed;
		this.desiredDistance = desiredDistance;
		this.requiredLaneChangeSpaceInFront = requiredLaneChangeSpaceInFront;
		this.requiredLaneChangeSpaceBehind = requiredLaneChangeSpaceBehind;
		this.accelerationSpeed = accelerationSpeed;
		this.brakeSpeed = brakeSpeed;
		this.size = size;
		this.reactionSpeed = reactionSpeed;
		this.constrained = constrained;
	}
	
	public Auto copy() {
		return new Auto(
				lane, position, currentSpeed,
				switchSpeedDifferential, desiredSpeed,
				desiredDistance, requiredLaneChangeSpaceInFront,
				requiredLaneChangeSpaceBehind, accelerationSpeed,
				brakeSpeed, size, reactionSpeed, constrained);
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
		if (frontConstrained) {
			if (canChangeRight) {
				List<Auto> currentLane = state.get(this.lane);
				List<Auto> desiredLane = state.get(this.lane - 1);
				currentLane.remove(this);
				desiredLane.add(this);
			} else if (canChangeLeft) {
				List<Auto> currentLane = state.get(this.lane);
				List<Auto> desiredLane = state.get(this.lane + 1);
				currentLane.remove(this);
				desiredLane.add(this);
			}
		}
	}
	
	public void step(
			Map<Integer, List<Auto>> observedState,
			Map<Integer, List<Auto>> modifiableState,
			double stepSize) {
		// Update position
		this.position += stepSize * Conversions.HoursPerSecond * this.currentSpeed * Conversions.FeetPerMile;
		//   feet     += seconds  * hours per second           * miles per hour    * feet per mile
		
		List<Auto> currentLane = observedState.get(this.lane);
		
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
			double desiredPosition = nextCar.getPos() - this.desiredDistance * (1 - (nextCar.currentSpeed - currentSpeed) / 5);
			if (this.position < desiredPosition) {
				updateSpeedNoConstraint(stepSize);
				constrained = false;
			} else {
				frontConstrained = true;
				constrained = true;
				deccelerate(stepSize);
			}
		}
		
		boolean canChangeLeft = false;
		if (observedState.containsKey(lane + 1)) {
			List<Auto> nextLane = observedState.get(lane + 1);
			canChangeLeft = true;
			for (Auto leftCar : nextLane) {
				if (isBlockingLaneChange(leftCar)) {
					canChangeLeft = false;
				}
			}
		}
		
		boolean canChangeRight = false;
		if (lane > 0) {
			List<Auto> previousLane = observedState.get(lane - 1);
			canChangeRight = true;
			for (Auto rightCar : previousLane) {
				if (isBlockingLaneChange(rightCar)) {
					canChangeRight = false;
				}
			}
		}
		
		this.constrained = frontConstrained;
		
		if (this.currentSpeed < 0) {
			this.currentSpeed = 0;
		}

		handleLaneChange(modifiableState, nextCar, canChangeLeft, canChangeRight, frontConstrained);
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
		this.currentSpeed -= stepSize * Conversions.HoursPerSecond * this.brakeSpeed;
		// miles per hour += seconds  * hours per second           * miles per hour per hour
	}
	
	public String toString() {
		return lane + " " + position + " " + currentSpeed;
	}
	
	public void loopTrack(double length) {
		this.position = this.position % length;
	}
}
