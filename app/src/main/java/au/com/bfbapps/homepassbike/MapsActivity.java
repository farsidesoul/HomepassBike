package au.com.bfbapps.homepassbike;

import android.graphics.Color;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

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

import au.com.bfbapps.homepassbike.model.BikeLocation;
import au.com.bfbapps.homepassbike.service.MelbourneBikeService;
import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

	private final double BASE_RADIUS = 5;

	private GoogleMap map;
	private Retrofit retrofit;
	private MelbourneBikeService melbourneBikeService;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_maps);
		setupService();

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
				handleBikeLocationReturn(response.body());
			}

			@Override
			public void onFailure(Throwable throwable) {

			}
		});
	}

	private void handleBikeLocationReturn(List<BikeLocation> bikeLocations){
		for(BikeLocation location : bikeLocations){
			createCircleOnBikeLocation(location);
		}
	}

	private void createCircleOnBikeLocation(BikeLocation location) {
		LatLng coords = new LatLng(location.getCoordinates().getLatitude(),
				location.getCoordinates().getLongitude());

		CircleOptions options = new CircleOptions()
				.radius(BASE_RADIUS * location.getNbbikes())
				.center(coords)
				.strokeColor(Color.RED);

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
	}
}
