package traffic_sim;

public interface SimulationSettings {
	double generateSwitchSpeedDifferential(double currentTime); // In miles per hour
	double generateDesiredSpeed(double currentTime); // In miles per hour
	double generateDesiredDistance(double currentTime); // In feet
	double generateRequiredLaneChangeSpaceInFront(double currentTime); // In feet
	double generateRequiredLaneChangeSpaceBehind(double currentTime); // In feet
	double generateAccelerationSpeed(double currentTime); // In miles per hour per hour
	double generateBrakeSpeed(double currentTime); // In miles per hour per hour
	double generateCarSize(double currentTime); // In feet
	double generateEnteringFrequency(double currentTime); // Cars per hour?
	double generateLeavingFrequency(double currentTime); // Cars per hour?
	double generateReactionSpeed(double currentTime); // In seconds?
	
	double proportionOfGreedyCars(double currentTime); // These can be any number and the simulation should generate with normalized frequency
	double proportionOfRuleFollowingCars(double currentTime);
	double proportionOfCasualCars(double currentTime);
	
	double generateInitialDistanceBetweenCars();
}
