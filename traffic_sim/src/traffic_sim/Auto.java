package traffic_sim;

import java.util.*;

public class Auto {
	private int lane;
	private double position;
	private double currentSpeed;
	
	private double switchSpeedDifferential;
	private double desiredSpeed;
	private double requiredLaneChangeSpaceInFront;
	private double requiredLaneChangeSpaceBehind;
	private double accelerationSpeed;
	private double brakeSpeed;
	private double size;
	private double reactionSpeed;
	private double chanceToChangeLanesPerSecond = 1;
	
	private SimulationSettings settings;
	
	public Auto(SimulationSettings settings, int lane, double pos) {
		this.lane = lane;
		this.position = pos;
		
		switchSpeedDifferential = settings.generateSwitchSpeedDifferential();
		
		currentSpeed = 0;
		desiredSpeed = settings.generateDesiredSpeed();
		
		requiredLaneChangeSpaceInFront = settings.generateRequiredLaneChangeSpaceInFront();
		requiredLaneChangeSpaceBehind = settings.generateRequiredLaneChangeSpaceBehind();
		
		accelerationSpeed = settings.generateAccelerationSpeed();
		brakeSpeed = settings.generateBrakeSpeed();
		
		size = settings.generateCarSize();
		
		reactionSpeed = settings.generateReactionSpeed();
		
		this.settings = settings;
	}
	
	public Auto(
			int lane,
			double position,
			double currentSpeed,
			double switchSpeedDifferential,
			double desiredSpeed,
			double requiredLaneChangeSpaceInFront,
			double requiredLaneChangeSpaceBehind,
			double accelerationSpeed,
			double brakeSpeed,
			double size,
			double reactionSpeed,
			SimulationSettings settings) {
		this.lane = lane;
		this.position = position;
		this.currentSpeed = currentSpeed;
		this.switchSpeedDifferential = switchSpeedDifferential;
		this.desiredSpeed = desiredSpeed;
		this.requiredLaneChangeSpaceInFront = requiredLaneChangeSpaceInFront;
		this.requiredLaneChangeSpaceBehind = requiredLaneChangeSpaceBehind;
		this.accelerationSpeed = accelerationSpeed;
		this.brakeSpeed = brakeSpeed;
		this.size = size;
		this.reactionSpeed = reactionSpeed;
		this.settings = settings;
	}
	
	public Auto copy() {
		return new Auto(
				lane, position, currentSpeed,
				switchSpeedDifferential, desiredSpeed,
				requiredLaneChangeSpaceInFront,
				requiredLaneChangeSpaceBehind, accelerationSpeed,
				brakeSpeed, size, reactionSpeed,
				settings);
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
	
	public double getDesiredSpeed() {
		return desiredSpeed;
	}
	
	public double getReactionSpeed() {
		return reactionSpeed;
	}
	
	// Override this to make different behaviors
	public void handleLaneChange(
			Map<Integer, List<Auto>> state,
			Auto carInFront,
			double distanceLeft,
			double distanceRight,
			double distanceFront,
			double timeElapsed) {
		if (Math.random() < timeElapsed * chanceToChangeLanesPerSecond) {
			if (distanceFront != -1) {
				if (distanceRight > distanceFront && distanceRight > distanceLeft) {
					List<Auto> currentLane = state.get(this.lane);
					List<Auto> desiredLane = state.get(this.lane - 1);
					currentLane.remove(this);
					desiredLane.add(this);
					this.lane--;
				} else if (distanceLeft > distanceFront && distanceLeft > distanceRight) {
					List<Auto> currentLane = state.get(this.lane);
					List<Auto> desiredLane = state.get(this.lane + 1);
					currentLane.remove(this);
					desiredLane.add(this);
					this.lane++;
				}
			}
		}
	}
	
	public void step(
			Map<Integer, List<Auto>> observedState,
			Map<Integer, List<Auto>> modifiableState,
			double stepSize,
			boolean looped) {
		// Update position
		this.position += stepSize * Conversions.HoursPerSecond * this.currentSpeed * Conversions.FeetPerMile;
		//   feet     += seconds  * hours per second           * miles per hour    * feet per mile
		
		List<Auto> currentLane = observedState.get(this.lane);
		
		// Find the next car in the current lane
		Auto nextCar = null;
		Auto lastCar = null;
		for (Auto car : currentLane) {
			double carPos = car.getPos();
			if (carPos > this.position) {
				if (nextCar == null || carPos < nextCar.getPos()) {
					nextCar = car;
				}
			}
			if (lastCar == null || carPos < lastCar.getPos()) {
				lastCar = car;
			}
		}
		if (nextCar == null && looped) {
			nextCar = lastCar.copy();
			nextCar.position += settings.generateTrackLength();
		}
		
		boolean frontConstrained = false;
		double distanceToStop = settings.generateTimeNeededToStop() * currentSpeed * Conversions.FeetPerMile * Conversions.HoursPerSecond;
		double desiredDistance = settings.generateDesiredDistanceStopped() + distanceToStop * Math.pow(currentSpeed / desiredSpeed, 2);
		
		double desiredPosition;
		if (nextCar == null) {
			desiredPosition = position + 1;
		} else {
			desiredPosition = nextCar.getPos() - desiredDistance;
		}
		
		if (this.position < desiredPosition) {
			updateSpeedNoConstraint(stepSize);
		} else {
			frontConstrained = true;
			deccelerate(stepSize);
		}
		
		double leftSpace = 0;
		if (observedState.containsKey(lane + 1)) {
			List<Auto> nextLane = observedState.get(lane + 1);
			boolean canChange = true;
			Auto car = null;
			for (Auto otherCar : nextLane) {
				if (otherCar.getPos() > this.position && (car == null || otherCar.getPos() < car.getPos())) {
					car = otherCar;
				}
				if (isBlockingLaneChange(otherCar)) {
					canChange = false;
					break;
				}
			}
			
			if (canChange && car != null) {
				leftSpace = position - car.getPos();
			} else {
				leftSpace = -1;
			}
		}
		
		double rightSpace = 0;
		if (lane > 0) {
			List<Auto> previousLane = observedState.get(lane - 1);
			boolean canChange = true;
			Auto car = null;
			for (Auto otherCar : previousLane) {
				if (otherCar.getPos() > this.position && (car == null || otherCar.getPos() < car.getPos())) {
					car = otherCar;
				}
				if (isBlockingLaneChange(otherCar)) {
					canChange = false;
					break;
				}
			}
			
			if (canChange && car != null) {
				rightSpace = position - car.getPos();
			} else {
				rightSpace = -1;
			}
		}
		
		if (this.currentSpeed < 0) {
			this.currentSpeed = 0;
		}

		handleLaneChange(modifiableState, nextCar, leftSpace, rightSpace, nextCar.position - position, stepSize);
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
		// miles per hour -= seconds  * hours per second           * miles per hour per hour
	}
	
	public String toString() {
		return lane + " " + position + " " + currentSpeed;
	}
	
	public void loopTrack(double length) {
		this.position = this.position % length;
	}
}
