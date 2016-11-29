package traffic_sim;

public class Conversions {
	public static final int FeetPerMile = 5280;
	public static final double MilesPerFoot = 1.0 / (double)FeetPerMile;
	public static final int SecondsPerMinute = 60;
	public static final double MinutesPerSecond = 1.0 / (double)SecondsPerMinute;
	public static final int MinutesPerHour = 60;
	public static final double HoursPerMinute = 1.0 / (double)MinutesPerHour;
	public static final int SecondsPerHour = SecondsPerMinute * MinutesPerHour;
	public static final double HoursPerSecond = 1.0 / (double)SecondsPerHour;
	public static final double FeetPerMeter = 3.28;
	public static final double MetersPerFoot = 1.0 / FeetPerMeter;
}
