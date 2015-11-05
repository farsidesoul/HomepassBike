package au.com.bfbapps.homepassbike.managers;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.List;

import au.com.bfbapps.homepassbike.model.BikeLocation;

public class PreferencesManager {

	public static final String LOCATION_LIST_KEY = "locations";

	private Context context;
	private SharedPreferences prefs;
	private Gson gson;

	public PreferencesManager(Context context){
		this.context = context;
		prefs = PreferenceManager.getDefaultSharedPreferences(context);
		gson = new Gson();
	}

	public void saveLocationsToPrefs(List<BikeLocation> locations){
		String locationJson = gson.toJson(locations);
		prefs.edit().putString(LOCATION_LIST_KEY, locationJson).apply();
	}

	public List<BikeLocation> getLocationsFromPrefs(){
		return gson.fromJson(prefs.getString(LOCATION_LIST_KEY, ""),
				new TypeToken<List<BikeLocation>>(){}.getType());
	}
}
