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
	
	public boolean constrained = false;
	
	public Auto(int lane, double pos) {
		this.lane = lane;
		this.position = pos;
		
		switchSpeedDifferential = Settings.switchSpeedDifferential;
		
		currentSpeed = 0;
		desiredSpeed = Settings.calculateDesiredSpeed();
		
		requiredLaneChangeSpaceInFront = Settings.requiredLaneChangeSpaceInFront;
		requiredLaneChangeSpaceBehind = Settings.requiredLaneChangeSpaceBehind;
		
		accelerationSpeed = Settings.calculateAccelerationSpeed();
		brakeSpeed = Settings.calculateBrakeSpeed();
		
		size = Settings.carSize;
		
		reactionSpeed = Settings.reactionSpeed;
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
			boolean constrained) {
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
		this.constrained = constrained;
	}
	
	public Auto copy() {
		return new Auto(
				lane, position, currentSpeed,
				switchSpeedDifferential, desiredSpeed,
				requiredLaneChangeSpaceInFront,
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
	
	public double getDesiredSpeed() {
		return desiredSpeed;
	}
	
	public double getReactionSpeed() {
		return reactionSpeed;
	}
	
	private void changeRight(Map<Integer, List<Auto>> state) {
		List<Auto> currentLane = state.get(this.lane);
		List<Auto> desiredLane = state.get(this.lane - 1);
		currentLane.remove(this);
		desiredLane.add(this);
		this.lane--;
	}
	
	private void changeLeft(Map<Integer, List<Auto>> state) {
		List<Auto> currentLane = state.get(this.lane);
		List<Auto> desiredLane = state.get(this.lane + 1);
		currentLane.remove(this);
		desiredLane.add(this);
		this.lane++;
	}
	
	// Override this to make different behaviors
	public void handleLaneChange(
			Map<Integer, List<Auto>> state,
			Auto carInFront,
			double distanceLeft,  // -1 blocked to the left change
			double distanceRight, // -1 blocked to the right dont change
			double distanceFront, // -1 means no car in front of me so no need to change
			double timeElapsed) {
		if (Math.random() < timeElapsed * Settings.chanceToAttemptLaneChangePerSecond) {
			if (distanceFront != -1 && distanceLeft != -1 && distanceLeft > distanceFront) {
				changeLeft(state);
			} else if (distanceRight != -1){
				changeRight(state);
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
			nextCar.position += Settings.trackLength;
		}
		
		constrained = false;
		double distanceToStop = Settings.calculateTimeNeededToStop() * currentSpeed * Conversions.FeetPerMile * Conversions.HoursPerSecond;
		double desiredDistance = Settings.calculateDesiredDistanceStopped() + distanceToStop * Math.pow(currentSpeed / desiredSpeed, 2);
		
		double desiredPosition;
		if (nextCar == null) {
			desiredPosition = position + 1;
		} else {
			desiredPosition = nextCar.getPos() - desiredDistance;
		}
		
		if (this.position < desiredPosition) {
			updateSpeedNoConstraint(stepSize);
		} else {
			constrained = true;
			deccelerate(stepSize);
		}
		
		double leftSpace = -1;
		if (modifiableState.containsKey(lane + 1)) {
			List<Auto> nextLane = modifiableState.get(lane + 1);
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
				leftSpace = car.getPos() - position;
			} else {
				leftSpace = -1;
			}
		}
		
		double rightSpace = -1;
		if (lane > 0) {
			List<Auto> previousLane = modifiableState.get(lane - 1);
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
				rightSpace = car.getPos() - position;
			} else {
				rightSpace = -1;
			}
		}
		
		if (this.currentSpeed < 0) {
			this.currentSpeed = 0;
		}

		double frontDistance = -1;
		if (nextCar != null) {
			frontDistance = nextCar.position - position;
		}
		if (Settings.laneChangeEnabled) {
			handleLaneChange(modifiableState, nextCar, leftSpace, rightSpace, frontDistance, stepSize);
		}
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
	
	public int loopTrack(double length) {
		boolean looped = this.position > length;
		this.position = this.position % length;
		if (looped) {
			return 1;
		} else {
			return 0;
		}
	}
}
