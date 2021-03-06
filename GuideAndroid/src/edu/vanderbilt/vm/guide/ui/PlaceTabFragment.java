
package edu.vanderbilt.vm.guide.ui;

/**
 * @author Athran, Nick
 * This Fragment shows the categories of places and the user's current location
 */

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;
import edu.vanderbilt.vm.guide.R;
import edu.vanderbilt.vm.guide.db.GuideDBConstants;
import edu.vanderbilt.vm.guide.ui.adapter.IndexedCursorAdapter;
import edu.vanderbilt.vm.guide.ui.adapter.PlaceCursorAdapter;
import edu.vanderbilt.vm.guide.ui.listener.PlaceListClickListener;
import edu.vanderbilt.vm.guide.util.DBUtils;
import edu.vanderbilt.vm.guide.util.GlobalState;

public class PlaceTabFragment extends SherlockFragment {

    
    
    @SuppressWarnings("unused")
    private static final Logger LOGGER = LoggerFactory.getLogger("ui.PlaceTabFragment");
    
    private Cursor mAllPlacesCursor; // A cursor holding all places in the db


    @Override
    public View onCreateView(
    		LayoutInflater inflater, 
    		ViewGroup container, 
    		Bundle savedInstanceState) {
        return inflater.inflate(R.layout.single_list, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Query for places and setup ListView
        String[] columns = {
                GuideDBConstants.PlaceTable.NAME_COL, 
                GuideDBConstants.PlaceTable.CATEGORY_COL,
                GuideDBConstants.PlaceTable.LATITUDE_COL,
                GuideDBConstants.PlaceTable.LONGITUDE_COL, 
                GuideDBConstants.PlaceTable.ID_COL,
                GuideDBConstants.PlaceTable.DESCRIPTION_COL,
                GuideDBConstants.PlaceTable.IMAGE_LOC_COL
        };
        mAllPlacesCursor = DBUtils.getAllPlaces(
        		columns, GlobalState.getReadableDatabase());
        
        ListView lv = (ListView) getView().findViewById(R.id.s_l_listview1);
        lv.setAdapter(new IndexedCursorAdapter(
        		getActivity(), 
        		mAllPlacesCursor, 
                IndexedCursorAdapter.SORT_ALPHABETICALLY));
        lv.setOnItemClickListener(new PlaceListClickListener(getActivity()));

        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.place_list, menu);
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        ListView lv = (ListView) getView().findViewById(R.id.s_l_listview1);
        switch (item.getItemId()) {
            case R.id.menu_sort_alphabetic:
                
                lv.setAdapter(new IndexedCursorAdapter(
                		getActivity(), 
                		mAllPlacesCursor, 
                        IndexedCursorAdapter.SORT_ALPHABETICALLY));
                
                Toast.makeText(
                		getActivity(), 
                		"Places List is sorted alphabetically",
                        Toast.LENGTH_SHORT).show();
                return true;

            case R.id.menu_sort_distance:
                
                lv.setAdapter(new IndexedCursorAdapter(
                		getActivity(), 
                		mAllPlacesCursor, 
                        IndexedCursorAdapter.SORT_BY_DISTANCE));

                Toast.makeText(
                		getActivity(), 
                		"Places List is sorted by distance",
                        Toast.LENGTH_SHORT).show();
                return true;

            case R.id.menu_sort_category:
                
                lv.setAdapter(new IndexedCursorAdapter(
                		getActivity(), 
                		mAllPlacesCursor, 
                        IndexedCursorAdapter.SORT_BY_CATEGORY));
                
                Toast.makeText(
                		getActivity(), 
                		"Places List is sorted by category",
                        Toast.LENGTH_SHORT).show();
                return true;
                
            default:
                return false;
        }
    }
    
    public void viewListFromCursor(Cursor cursor) {
        ((ListView) getView().findViewById(R.id.s_l_listview1)).setAdapter(
        		new PlaceCursorAdapter(getActivity(), cursor));
    }

}
