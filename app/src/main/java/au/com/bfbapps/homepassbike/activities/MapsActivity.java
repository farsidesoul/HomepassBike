package au.com.bfbapps.homepassbike.activities;

import android.graphics.Color;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.List;

import au.com.bfbapps.homepassbike.R;
import au.com.bfbapps.homepassbike.adapters.InfoPopupAdapter;
import au.com.bfbapps.homepassbike.managers.PreferencesManager;
import au.com.bfbapps.homepassbike.model.BikeLocation;
import au.com.bfbapps.homepassbike.service.MelbourneBikeService;
import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

	private final double BASE_RADIUS = 3;

	private GoogleMap map;
	private Retrofit retrofit;
	private MelbourneBikeService melbourneBikeService;
	private PreferencesManager prefs;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_maps);
		setupService();
		prefs = new PreferencesManager(this);

		if(prefs.getLocationsFromPrefs() != null){
			handleBikeLocationReturn(prefs.getLocationsFromPrefs());
		}

		// Obtain the SupportMapFragment and get notified when the map is ready to be used.
		SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
				.findFragmentById(R.id.map);
		mapFragment.getMapAsync(this);
	}

	private void setupService() {
		setupRetrofitBuilder();
		setupBikeService();
	}

	private void setupRetrofitBuilder() {
		Gson gson = new GsonBuilder()
				.setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS")
				.create();
		retrofit = new Retrofit.Builder()
				.baseUrl("https://data.melbourne.vic.gov.au")
				.addConverterFactory(GsonConverterFactory.create(gson))
				.build();
	}

	private void setupBikeService() {
		melbourneBikeService = retrofit.create(MelbourneBikeService.class);
	}

	private void getBikeLocationsFromServer() {
		Call<List<BikeLocation>> call = melbourneBikeService.listLocations();
		call.enqueue(new Callback<List<BikeLocation>>() {
			@Override
			public void onResponse(Response<List<BikeLocation>> response, Retrofit retrofit) {
				if(response.body() != null){
					prefs.saveLocationsToPrefs(response.body());
					handleBikeLocationReturn(response.body());
				} else {
					Toast.makeText(
							MapsActivity.this,
							"Unable to retrieve location data",
							Toast.LENGTH_SHORT).show();
				}
			}

			@Override
			public void onFailure(Throwable throwable) {

			}
		});
	}

	private void handleBikeLocationReturn(List<BikeLocation> bikeLocations){
		for(BikeLocation location : bikeLocations){
			LatLng coords = new LatLng(location.getCoordinates().getLatitude(),
					location.getCoordinates().getLongitude());
			createCircleOnBikeLocation(location, coords);
			createMarkerWithLabel(location, coords);
		}
	}

	private void createMarkerWithLabel(BikeLocation location, LatLng coords) {
		MarkerOptions options = new MarkerOptions()
				.position(coords)
				.draggable(false)
				.title(location.getFeatureName())
				.snippet("Bikes Available: " + location.getNbbikes() + "|Empty Slots: " + location.getNbemptydoc())
				.alpha(0f);

		map.addMarker(options);
	}

	private void createCircleOnBikeLocation(BikeLocation location, LatLng coords) {
		CircleOptions options = new CircleOptions()
				.radius(BASE_RADIUS * location.getNbbikes())
				.center(coords)
				.strokeColor(getResources().getColor(R.color.circleColor));

		map.addCircle(options);
	}
	
	
	/**
	 * Manipulates the map once available.
	 * This callback is triggered when the map is ready to be used.
	 * This is where we can add markers or lines, add listeners or move the camera. In this case,
	 * we just add a marker near Sydney, Australia.
	 * If Google Play services is not installed on the device, the user will be prompted to install
	 * it inside the SupportMapFragment. This method will only be triggered once the user has
	 * installed Google Play services and returned to the app.
	 */
	@Override
	public void onMapReady(GoogleMap googleMap) {
		map = googleMap;
		getBikeLocationsFromServer();

		LatLng melbourne = new LatLng(-37.81, 144.96);

		CameraPosition cameraPosition = new CameraPosition.Builder()
				.target(melbourne)
				.zoom(12)
				.build();

		map.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
		map.setInfoWindowAdapter(new InfoPopupAdapter(getLayoutInflater()));
	}
}
