package au.com.bfbapps.homepassbike.fragments;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
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

public class MapsFragment extends Fragment implements OnMapReadyCallback {

	private final double BASE_RADIUS = 5;
	private final float DEFAULT_ZOOM = 12.5f;

	private GoogleMap map;
	private Retrofit retrofit;
	private MelbourneBikeService melbourneBikeService;
	private PreferencesManager prefs;
	private OnMapReady onMapReadyListener;

	private List<Marker> markers;
	private List<BikeLocation> bikeLocations;

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.activity_maps, container, false);
		setupService();

		prefs = new PreferencesManager(getActivity());
		markers = new ArrayList<>();

		// Obtain the SupportMapFragment and get notified when the map is ready to be used.
		SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
				.findFragmentById(R.id.map);
		mapFragment.getMapAsync(this);

		return v;
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
				if (response.body() != null) {
					prefs.saveLocationsToPrefs(response.body());
					handleBikeLocationReturn(response.body());
				} else {
					Toast.makeText(
							getActivity(),
							"Unable to retrieve location data",
							Toast.LENGTH_SHORT).show();
				}
			}

			@Override
			public void onFailure(Throwable throwable) {
				Toast.makeText(
						getActivity(),
						"Unable to retrieve location data",
						Toast.LENGTH_SHORT).show();
			}
		});
	}

	private void handleBikeLocationReturn(List<BikeLocation> bikeLocations){
		this.bikeLocations = bikeLocations;
		if(onMapReadyListener != null){
			onMapReadyListener.onMapReady();
		}

		clearExistingMarkers();

		for(BikeLocation location : bikeLocations){
			createMarkerWithCircleAtLocation(location);
		}
	}

	private void createMarkerWithCircleAtLocation(BikeLocation location) {
		LatLng coords = new LatLng(location.getCoordinates().getLatitude(),
				location.getCoordinates().getLongitude());
		createCircleOnBikeLocation(location, coords);
		createMarkerWithLabel(location, coords);
	}

	private void clearExistingMarkers() {
		map.clear();
		markers = new ArrayList<>();
	}

	private void createMarkerWithLabel(BikeLocation location, LatLng coords) {
		MarkerOptions options = new MarkerOptions()
				.position(coords)
				.draggable(false)
				.title(location.getFeatureName())
				.snippet("Bikes Available: " + location.getNbbikes() + "|Empty Slots: " + location.getNbemptydoc())
				.alpha(0f);


		Marker marker = map.addMarker(options);
		markers.add(marker);
	}

	private void createCircleOnBikeLocation(BikeLocation location, LatLng coords) {
		CircleOptions options = new CircleOptions()
				// If there are no bikes, give it the smallest width
				.radius(BASE_RADIUS * (location.getNbbikes() == 0 ? 1 : location.getNbbikes()))
				.center(coords)
				.strokeColor(getCircleStrokeColor())
				.fillColor(getCircleFillColor());

		// Add large circle first
		map.addCircle(options);

		// Add smaller circle at exact location
		options.radius(BASE_RADIUS);
		map.addCircle(options);
	}

	private int getCircleFillColor() {
		return getResources().getColor(R.color.circleFillColor);
	}

	private int getCircleStrokeColor(){
		return getResources().getColor(R.color.circleStrokeColor);
	}

	@Override
	public void onMapReady(GoogleMap googleMap) {
		map = googleMap;

		// If we already have some locations stored, we'll use them while we wait for the update
		if(prefs.getLocationsFromPrefs() != null){
			handleBikeLocationReturn(prefs.getLocationsFromPrefs());
		}

		getBikeLocationsFromServer();

		moveMapToInitialLocation();
	}

	private void moveMapToInitialLocation() {

		CameraPosition cameraPosition = new CameraPosition.Builder()
				.target(new LatLng(-37.82, 144.96)) // Melbourne
				.zoom(DEFAULT_ZOOM)
				.build();

		map.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
		map.setInfoWindowAdapter(new InfoPopupAdapter(getActivity().getLayoutInflater()));
	}

	public void searchForSelectedMarker(String title){
		findSelectedMarker(title);
	}

	private void findSelectedMarker(String title) {
		for(Marker marker : markers){
			if(marker.getTitle().equals(title)){
				moveToMarker(marker);
				break;
			}
		}
	}

	private void moveToMarker(Marker marker) {
		marker.showInfoWindow();
		CameraPosition cameraPosition = new CameraPosition.Builder()
				.target(marker.getPosition())
				.zoom(16f)
				.build();
		map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
	}

	public List<BikeLocation> getBikeLocations(){
		return bikeLocations;
	}

	public void setOnMapReadyListener(OnMapReady listener){
		onMapReadyListener = listener;
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {

		super.onSaveInstanceState(outState);
	}

	public interface OnMapReady{
		void onMapReady();
	};
}
