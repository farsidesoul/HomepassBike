package au.com.bfbapps.homepassbike.activities;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.TextView;

import au.com.bfbapps.homepassbike.R;
import au.com.bfbapps.homepassbike.fragments.MapsFragment;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity  {

	@Bind(R.id.toolbar)
	Toolbar toolbar;
	@Bind(R.id.text_toolbar_title)
	TextView toolbarTitle;
	@Bind(R.id.autocomplete_search)
	AutoCompleteTextView searchField;
	@Bind(R.id.image_search_cancel)
	ImageView searchImage;

	private InputMethodManager imm;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		ButterKnife.bind(this);

		setSupportActionBar(toolbar);

		imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);

		hostMapFragment();
	}

	private void hostMapFragment() {
		MapsFragment mapFragment = new MapsFragment();
		mapFragment.setOnMapReadyListener(onMapReadyListener);
		getSupportFragmentManager().beginTransaction()
				.add(R.id.relative_main_content, mapFragment)
				.commit();
	}

	private void hideKeyboard(){
		imm.hideSoftInputFromWindow(searchField.getWindowToken(), 0);
	}

	private void showKeyboard(){
		imm.toggleSoftInputFromWindow(searchField.getApplicationWindowToken(), InputMethodManager.SHOW_FORCED, 0);
	}

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
		toolbarTitle.setVisibility(View.INVISIBLE);
	}

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

	private final MapsFragment.OnMapReady onMapReadyListener = new MapsFragment.OnMapReady() {
		@Override
		public void onMapReady() {
			searchImage.setVisibility(View.VISIBLE);
		}
	};
}
