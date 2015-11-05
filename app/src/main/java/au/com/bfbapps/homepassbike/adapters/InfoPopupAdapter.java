package au.com.bfbapps.homepassbike.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

import au.com.bfbapps.homepassbike.R;

public class InfoPopupAdapter implements GoogleMap.InfoWindowAdapter {

	private View infoPopup = null;
	private LayoutInflater inflater = null;

	public InfoPopupAdapter(LayoutInflater inflater){
		this.inflater = inflater;
	}

	@Override
	public View getInfoWindow(Marker marker) {
		return null;
	}

	@Override
	public View getInfoContents(Marker marker) {
		if(infoPopup == null){
			infoPopup = inflater.inflate(R.layout.info_popup, null);
		}

		TextView title = (TextView) infoPopup.findViewById(R.id.text_location_title);
		TextView bikesAvail = (TextView) infoPopup.findViewById(R.id.text_location_bikes_avail);
		TextView slotsAvail = (TextView) infoPopup.findViewById(R.id.text_location_empty_slots);

		String[] info = marker.getSnippet().split("\\|");

		bikesAvail.setText(info[0]);
		slotsAvail.setText(info[1]);

		title.setText(marker.getTitle());
		return infoPopup;
	}
}
