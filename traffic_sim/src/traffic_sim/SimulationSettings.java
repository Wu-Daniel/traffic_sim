package traffic_sim;

public interface SimulationSettings {
	double generateSwitchSpeedDifferential(); // In miles per hour
	double generateDesiredSpeed(); // In miles per hour
	
	double generateDesiredDistanceStopped(); // In feet
	
	double generateDistanceToStop();
	double generateTimeNeededToStop();
	
	double generateRequiredLaneChangeSpaceInFront(); // In feet
	double generateRequiredLaneChangeSpaceBehind(); // In feet
	
	double generateAccelerationSpeed(); // In miles per hour per hour
	double generateBrakeSpeed(); // In miles per hour per hour
	
	double generateCarSize(); // In feet
	double generateReactionSpeed(); // In seconds?
	
	double generateInitialDistanceBetweenCars();
	
	double generateEnterTime(); // second per car;
	
	double generateTrackLength();
}
