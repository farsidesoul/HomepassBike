package au.com.bfbapps.homepassbike.model;

import java.util.Date;

public class BikeLocation {

	private Coordinate coordinates;
	private String featurename;
	private int id;
	private int nbbikes;
	private int nbemptydoc;
	private String terminalname;
	private Date uploaddate;

	public Coordinate getCoordinates() {
		return coordinates;
	}

	public String getFeatureName() {
		return featurename;
	}

	public int getId() {
		return id;
	}

	public int getNbbikes() {
		return nbbikes;
	}

	public int getNbemptydoc() {
		return nbemptydoc;
	}

	public String getTerminalname() {
		return terminalname;
	}

	public Date getUploaddate() {
		return uploaddate;
	}
}
