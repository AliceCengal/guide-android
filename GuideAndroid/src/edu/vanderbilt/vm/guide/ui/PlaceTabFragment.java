package edu.vanderbilt.vm.guide.ui;

/**
 * @author Athran, Nick
 * This Fragment shows the categories of places and the user's current location
 */

import java.util.ArrayList;

import android.annotation.TargetApi;
import android.app.Fragment;
import android.database.Cursor;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import edu.vanderbilt.vm.guide.R;
import edu.vanderbilt.vm.guide.container.Place;
import edu.vanderbilt.vm.guide.db.GuideDBConstants;
import edu.vanderbilt.vm.guide.db.GuideDBOpenHelper;
import edu.vanderbilt.vm.guide.ui.adapter.AgendaAdapter;
import edu.vanderbilt.vm.guide.ui.adapter.PlaceListAdapter;
import edu.vanderbilt.vm.guide.util.DBUtils;
import edu.vanderbilt.vm.guide.util.Geomancer;
import edu.vanderbilt.vm.guide.util.GlobalState;

@TargetApi(16)
public class PlaceTabFragment extends Fragment implements OnClickListener {
	private final int DESCRIPTION_LENGTH = 35;

	private ListView mListView;
	private TextView mCurrPlaceName;
	private TextView mCurrPlaceDesc;
	private EditText mSearchBox;
	private LinearLayout mCurrentPlaceBar;
	private Place mCurrPlace;
	private Menu mMenu;

	@Override
	public View onCreateView(LayoutInflater inflatr, ViewGroup containr,
			Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		return inflatr.inflate(R.layout.fragment_place_list, containr, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		mCurrPlaceName = (TextView) getActivity().findViewById(
				R.id.currentPlaceName);
		mCurrPlaceDesc = (TextView) getActivity().findViewById(
				R.id.currentPlaceDesc);
		
		mListView = (ListView) getActivity()
				.findViewById(R.id.placeTablistView);
		
		mListView.setAdapter(new AgendaAdapter(getActivity(), 
				GlobalState.getAllPlace(getActivity())));
		mListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Place place = (Place) mListView.getItemAtPosition(position);
				PlaceDetailer.open(getActivity(), place.getUniqueId());
			}
		});

		/*
		 * Tells you what is the closest building to your location right now
		 */
		Location loc = Geomancer.getDeviceLocation();
		mCurrPlace = null;
		if (loc != null) {
			setCurrentPlace(Geomancer.findClosestPlace(loc,
					GlobalState.getPlaceList(getActivity())));
		}
		
		// Prevent the soft keyboard from popping up at startup.
		getActivity().getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

		mSearchBox = (EditText) getActivity().findViewById(
				R.id.placeTabSearchEdit);
		mSearchBox.setOnClickListener(this);

		mCurrentPlaceBar = (LinearLayout) getActivity().findViewById(
				R.id.current_place_bar);
		mCurrentPlaceBar.setOnClickListener(this);
		
		setHasOptionsMenu(true);
		
	}

	// this method of setting up OnClickListener seems to be necessary when you
	// want to access class variables
	@Override
	public void onClick(View v) {
		if (v == mCurrPlaceDesc || v == mCurrPlaceName || v == mCurrentPlaceBar) {
			PlaceDetailer.open(getActivity(), mCurrPlace.getUniqueId());
		} else if (v == mSearchBox) {
			// mSearchBox.setFocusable(true);
		}

	}
	
	public void setCurrentPlace(Place plc) {
		mCurrPlace = plc;
		mCurrPlaceName.setText(mCurrPlace.getName());
		
		String desc = mCurrPlace.getDescription();
		if (desc.length() > DESCRIPTION_LENGTH) {
			mCurrPlaceDesc.setText(desc.substring(0, DESCRIPTION_LENGTH)
					+ "...");
		} else {
			mCurrPlaceDesc.setText(desc + "...");
		}
	}
	
	public boolean onCreateOptionsMenu(Menu menu){
		mMenu = menu;
		return true;
	}
	
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_refresh:
			
			Toast.makeText(getActivity(), "Place list refreshed", 
					Toast.LENGTH_SHORT).show();
			return true;
		
		case R.id.menu_sort_alphabetic:
			
			Toast.makeText(getActivity(), "PlacesList is sorted alphabetically",
					Toast.LENGTH_SHORT).show();
			return true;
			
		case R.id.menu_sort_distance:
			
			Toast.makeText(getActivity(), "PlacesList is sorted by distance",
					Toast.LENGTH_SHORT).show();
			return true;
		default: return false;
		}
	}
	
}
