package traffic_sim;

public class DefaultSettings implements SimulationSettings {

	@Override
	public double generateSwitchSpeedDifferential() {
		return 10.0;
	}

	@Override
	public double generateDesiredSpeed() {
		return 67 + Math.random() * 5;
	}

	@Override
	public double generateDesiredDistanceStopped() {
		return 8.0 + generateCarSize();
	}

	@Override
	public double generateRequiredLaneChangeSpaceInFront() {
		return 32;
	}

	@Override
	public double generateRequiredLaneChangeSpaceBehind() {
		return 40;
	}

	@Override
	public double generateAccelerationSpeed() {
		return 2.87 * Conversions.FeetPerMeter * Conversions.SecondsPerHour;
	}

	@Override
	public double generateBrakeSpeed() {
		return 4.53 * Conversions.FeetPerMeter * Conversions.SecondsPerHour;
	}
	
	@Override
	public double generateDistanceToStop() {
		return 203.0;
	}
	
	@Override
	public double generateTimeNeededToStop() {
		return generateDistanceToStop() * 2 / generateDesiredSpeed();
	}

	@Override
	public double generateCarSize() {
		return 16;
	}

	@Override
	public double generateReactionSpeed() {
		return 0.4;
	}
	
	@Override
	public double generateInitialDistanceBetweenCars() {
		return 10;
	}
	
	@Override
	public double generateEnterTime() {
		return 0.5 * (generateCarSize() + generateDistanceToStop())/(generateDesiredSpeed() * Conversions.FeetPerMile / Conversions.SecondsPerHour);
	}
	
	@Override
	public double generateTrackLength() {
		return 1000;
	}
}
