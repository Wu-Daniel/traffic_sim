package traffic_sim;

public class Settings {

	public static final double switchSpeedDifferential = 10.0;
	public static final double requiredLaneChangeSpaceInFront = 32;
	public static final double requiredLaneChangeSpaceBehind = 40;
	public static final double distanceToStop = 203.0;
	public static final double carSize = 16;
	public static final double reactionSpeed = 0.4;
	public static final double initialDistanceBetweenCars = 10;
	public static final double trackLength = 1000;
	public static final int laneCount = 5;
	public static final double stepSize = 0.0166;
	public static final int initialSteps = 5000;
	public static final int stepsPerFrame = 10;
	public static final int carCountPerLane = 16;
	public static final double chanceToAttemptLaneChangePerSecond = 1;
	
	public static final boolean looped = true;
	public static final boolean laneChangeEnabled = true;
	public static final RenderingStyle renderingStyle = RenderingStyle.Speed;
	
	public static double calculateDesiredSpeed() {
		return 67 + Math.random() * 10;
	}

	public static double calculateDesiredDistanceStopped() {
		return 8.0 + carSize;
	}

	public static double calculateAccelerationSpeed() {
		return 2.87 * Conversions.FeetPerMeter * Conversions.SecondsPerHour;
	}

	public static double calculateBrakeSpeed() {
		return 4.53 * Conversions.FeetPerMeter * Conversions.SecondsPerHour;
	}
	
	public static double calculateTimeNeededToStop() {
		return distanceToStop * 2 / calculateDesiredSpeed();
	}
	
	public static double calculateEnterTime() {
		return 0.5 * (carSize + distanceToStop)/(calculateDesiredSpeed() * Conversions.FeetPerMile / Conversions.SecondsPerHour);
	}
}
