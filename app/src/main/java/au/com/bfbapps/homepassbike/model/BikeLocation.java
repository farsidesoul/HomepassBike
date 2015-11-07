package au.com.bfbapps.homepassbike.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

public class BikeLocation implements Parcelable{

	private Coordinate coordinates;
	private String featurename;
	private int id;
	private int nbbikes;
	private int nbemptydoc;
	private String terminalname;
	private Date uploaddate;

	protected BikeLocation(Parcel in) {
		coordinates = in.readParcelable(Coordinate.class.getClassLoader());
		featurename = in.readString();
		id = in.readInt();
		nbbikes = in.readInt();
		nbemptydoc = in.readInt();
		terminalname = in.readString();
	}

	public static final Creator<BikeLocation> CREATOR = new Creator<BikeLocation>() {
		@Override
		public BikeLocation createFromParcel(Parcel in) {
			return new BikeLocation(in);
		}

		@Override
		public BikeLocation[] newArray(int size) {
			return new BikeLocation[size];
		}
	};

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

	@Override
	public String toString() {
		return featurename;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeParcelable(coordinates, flags);
		dest.writeString(featurename);
		dest.writeInt(id);
		dest.writeInt(nbbikes);
		dest.writeInt(nbemptydoc);
		dest.writeString(terminalname);
	}
}
