package au.com.bfbapps.homepassbike.activities;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import au.com.bfbapps.homepassbike.R;
import au.com.bfbapps.homepassbike.adapters.SearchDropDownAdapter;
import au.com.bfbapps.homepassbike.fragments.MapsFragment;
import au.com.bfbapps.homepassbike.model.BikeLocation;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity  {

	private static final String SEARCH_OPTION_VISIBLE = "searchOptionVisible";
	private final String STATE_BIKE_LOCATIONS = "bikeLocations";

	@Bind(R.id.toolbar)
	Toolbar toolbar;
	@Bind(R.id.text_toolbar_title)
	TextView toolbarTitle;
	@Bind(R.id.autocomplete_search)
	AutoCompleteTextView searchField;
	@Bind(R.id.image_search_cancel)
	ImageView searchImage;

	private InputMethodManager imm;
	private MapsFragment mapFragment;
	private SearchDropDownAdapter adapter;
	private ArrayList<BikeLocation> bikeLocations;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		ButterKnife.bind(this);

		setSupportActionBar(toolbar);

		imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);

		if(savedInstanceState != null){
			recreateExistingMapFragment(savedInstanceState);
		} else {
			createNewMapFragment();
		}
	}

	//region Fragment Creation
	private void recreateExistingMapFragment(Bundle savedInstanceState) {
		mapFragment = (MapsFragment) getSupportFragmentManager().getFragment(
				savedInstanceState, "mapFragment");
		if(savedInstanceState.getBoolean(SEARCH_OPTION_VISIBLE)){
			searchImage.setVisibility(View.VISIBLE);
		}
		bikeLocations = savedInstanceState.getParcelableArrayList(STATE_BIKE_LOCATIONS);
		setupSearchAdapter();
	}

	private void createNewMapFragment() {
		mapFragment = new MapsFragment();
		bikeLocations = new ArrayList<>();
		hostMapFragment();
	}

	private void hostMapFragment() {
		mapFragment.setOnMapReadyListener(onMapReadyListener);
		getSupportFragmentManager().beginTransaction()
				.add(R.id.relative_main_content, mapFragment)
				.commit();
	}

	//endregion

	//region Keyboard Methods
	private void hideKeyboard(){
		imm.hideSoftInputFromWindow(searchField.getWindowToken(), 0);
	}

	private void showKeyboard(){
		imm.toggleSoftInputFromWindow(searchField.getApplicationWindowToken(), InputMethodManager.SHOW_FORCED, 0);
	}
	//endregion

	//region Search
	private void hideSearch(){
		searchImage.setImageResource(R.mipmap.ic_search_white_24dp);
		searchField.setText("");
		searchField.setVisibility(View.INVISIBLE);
		searchField.setEnabled(false);
		toolbarTitle.setVisibility(View.VISIBLE);
	}

	private void showSearch(){
		searchImage.setImageResource(android.R.drawable.ic_menu_close_clear_cancel);
		searchField.setVisibility(View.VISIBLE);
		searchField.setEnabled(true);
		searchField.requestFocus();
		toolbarTitle.setVisibility(View.INVISIBLE);
	}


	private void setupSearchAdapter() {
		adapter = new SearchDropDownAdapter(this, bikeLocations);
		adapter.setDropDownViewResource(R.layout.adapter_search_list_item);
		searchField.setThreshold(1);
		searchField.setAdapter(adapter);
		searchField.setOnItemClickListener(onSearchItemClickListener);
	}

	private void focusOnSelectedLocation(List<BikeLocation> bikeLocations) {
		for(BikeLocation location : bikeLocations){
			if(location.getFeatureName().equals(searchField.getText().toString())){
				mapFragment.searchForSelectedMarker(location.getFeatureName());
				break;
			}
		}
	}
	//endregion

	//region Listeners

	@OnClick(R.id.image_search_cancel)
	protected void onSearchIconClick(){
		if(searchField.isEnabled()){
			hideKeyboard();
			hideSearch();
		} else {
			showSearch();
			showKeyboard();
		}
	}

	private final AdapterView.OnItemClickListener onSearchItemClickListener =
			new AdapterView.OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			List<BikeLocation> bikeLocations = mapFragment.getBikeLocations();
			focusOnSelectedLocation(bikeLocations);
			hideKeyboard();
			hideSearch();
		}
	};

	private final MapsFragment.OnMapReady onMapReadyListener = new MapsFragment.OnMapReady() {
		@Override
		public void onMapReady() {
			searchImage.setVisibility(View.VISIBLE);
			bikeLocations = mapFragment.getBikeLocations();
			setupSearchAdapter();
		}
	};

	//endregion

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		//Save the fragment's instance
		getSupportFragmentManager().putFragment(outState, "mapFragment", mapFragment);
		outState.putBoolean(SEARCH_OPTION_VISIBLE, searchImage.getVisibility() == View.VISIBLE);
		outState.putParcelableArrayList(STATE_BIKE_LOCATIONS, bikeLocations);
		super.onSaveInstanceState(outState);
	}
}
