package au.com.bfbapps.homepassbike.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import au.com.bfbapps.homepassbike.R;
import au.com.bfbapps.homepassbike.model.BikeLocation;
import butterknife.Bind;
import butterknife.ButterKnife;

public class SearchDropDownAdapter extends ArrayAdapter<BikeLocation> {

	private Context context;
	private List<BikeLocation> locations;

	public SearchDropDownAdapter(Context context, List<BikeLocation> locations) {
		super(context, R.layout.adapter_search_list_item, locations);
		this.context = context;
		this.locations = locations;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LocationHolder holder;

		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		if(convertView != null){
			holder = (LocationHolder) convertView.getTag();
		} else {
			convertView = inflater.inflate(R.layout.adapter_search_list_item, parent, false);
			holder = new LocationHolder(convertView);
			convertView.setTag(holder);
		}

		BikeLocation location = locations.get(position);
		holder.locationName.setText(location.getFeatureName());
		holder.bikesAvailable.setText(String.format("Available Bikes: %s", location.getNbbikes()));

		return convertView;
	}
	class LocationHolder {
		@Bind(R.id.text_location_name) TextView locationName;
		@Bind(R.id.text_location_bikes_avail) TextView bikesAvailable;

		public LocationHolder(View view){
			ButterKnife.bind(this, view);
		}
	}
}
