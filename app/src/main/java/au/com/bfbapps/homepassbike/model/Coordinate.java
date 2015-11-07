package au.com.bfbapps.homepassbike.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Coordinate implements Parcelable{

	private String type;
	private double[] coordinates;

	protected Coordinate(Parcel in) {
		type = in.readString();
		coordinates = in.createDoubleArray();
	}

	public static final Creator<Coordinate> CREATOR = new Creator<Coordinate>() {
		@Override
		public Coordinate createFromParcel(Parcel in) {
			return new Coordinate(in);
		}

		@Override
		public Coordinate[] newArray(int size) {
			return new Coordinate[size];
		}
	};

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

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(type);
		dest.writeDoubleArray(coordinates);
	}
}
