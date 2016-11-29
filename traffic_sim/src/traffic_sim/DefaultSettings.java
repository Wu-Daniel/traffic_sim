package traffic_sim;

public class DefaultSettings implements SimulationSettings {

	@Override
	public double generateSwitchSpeedDifferential(double currentTime) {
		return 10.0;
	}

	@Override
	public double generateDesiredSpeed(double currentTime) {
		return 65;
	}

	@Override
	public double generateDesiredDistance(double currentTime) {
		return 30;
	}

	@Override
	public double generateRequiredLaneChangeSpaceInFront(double currentTime) {
		return 10;
	}

	@Override
	public double generateRequiredLaneChangeSpaceBehind(double currentTime) {
		return 10;
	}

	@Override
	public double generateAccelerationSpeed(double currentTime) {
		return 25000;
	}

	@Override
	public double generateBrakeSpeed(double currentTime) {
		return 100000;
	}

	@Override
	public double generateCarSize(double currentTime) {
		return 10;
	}

	@Override
	public double generateEnteringFrequency(double currentTime) {
		return 5;
	}

	@Override
	public double generateLeavingFrequency(double currentTime) {
		return 5;
	}

	@Override
	public double generateReactionSpeed(double currentTime) {
		return 0.3;
	}

	@Override
	public double proportionOfGreedyCars(double currentTime) {
		return 0;
	}

	@Override
	public double proportionOfRuleFollowingCars(double currentTime) {
		return 0;
	}

	@Override
	public double proportionOfCasualCars(double currentTime) {
		return 1;
	}
	
	@Override
	public double generateInitialDistanceBetweenCars() {
		return 10;
	}
}
