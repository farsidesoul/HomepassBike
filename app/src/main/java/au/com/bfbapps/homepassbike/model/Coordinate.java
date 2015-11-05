package au.com.bfbapps.homepassbike.model;

public class Coordinate {

	private String type;
	private double[] coordinates;

	public String getType() {
		return type;
	}

	public double[] getCoordinates() {
		return coordinates;
	}

	public double getLatitude(){
		return coordinates[1];
	}

	public double getLongitude(){
		return coordinates[0];
	}
}
