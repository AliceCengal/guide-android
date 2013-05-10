
package edu.vanderbilt.vm.guide.ui;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.MenuItem;

import android.database.Cursor;
import android.location.Location;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import edu.vanderbilt.vm.guide.R;
import edu.vanderbilt.vm.guide.container.Place;
import edu.vanderbilt.vm.guide.db.GuideDBConstants;
import edu.vanderbilt.vm.guide.db.GuideDBOpenHelper;
import edu.vanderbilt.vm.guide.ui.adapter.AgendaAdapter;
import edu.vanderbilt.vm.guide.ui.listener.GeomancerListener;
import edu.vanderbilt.vm.guide.util.DBUtils;
import edu.vanderbilt.vm.guide.util.Geomancer;
import edu.vanderbilt.vm.guide.util.GlobalState;
import edu.vanderbilt.vm.guide.util.ImageDownloader;

public class AgendaFragment extends SherlockFragment implements GeomancerListener {

    private static final Logger logger = LoggerFactory.getLogger("ui.AgendaFragment");

    private final int DESCRIPTION_LENGTH = 100;
    
    private View mRoot;
    
    private Cursor mAllPlacesCursor;
    
    private Place mCurrentPlace;
    
    private ImageDownloader.BitmapDownloaderTask mDlTask = null;
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRoot = inflater.inflate(R.layout.fragment_agenda, container, false);
        return mRoot;
    }
    
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        
        GuideDBOpenHelper helper = new GuideDBOpenHelper(getActivity());
        String[] columns = {
                GuideDBConstants.PlaceTable.NAME_COL, GuideDBConstants.PlaceTable.CATEGORY_COL,
                GuideDBConstants.PlaceTable.LATITUDE_COL,
                GuideDBConstants.PlaceTable.LONGITUDE_COL, GuideDBConstants.PlaceTable.ID_COL,
                GuideDBConstants.PlaceTable.DESCRIPTION_COL,
                GuideDBConstants.PlaceTable.IMAGE_LOC_COL
        };
        mAllPlacesCursor = DBUtils.getAllPlaces(columns, helper.getReadableDatabase());
        //helper.close();
        
        
        ListView lv = (ListView) mRoot.findViewById(R.id.agenda_list);
        lv.setAdapter(new AgendaAdapter(getActivity(), GlobalState.getUserAgenda()));

        
        // Add an empty agenda indicator
        TextView emptyIndicator = (TextView) getActivity().getLayoutInflater().inflate(R.layout.agenda_empty, null, false);
        ((ViewGroup) lv.getParent()).addView(emptyIndicator);
        lv.setEmptyView(emptyIndicator);
        
        
        updateLocation(Geomancer.getDeviceLocation());
        setHasOptionsMenu(true);
    }

    
    @Override
    public void onResume() {
        super.onResume();
        logger.trace("AgendaFragment: OnResume called");
        Geomancer.registerGeomancerListener(this);
    }
    
    @Override
    public void onPause() {
        super.onPause();
        Geomancer.removeGeomancerListener(this);
    }
    
    /*
    public void onReselect() {
        try {
            ((ListView) mRoot.findViewById(R.id.s_l_listview1)).invalidateViews();
            
        } catch (IllegalStateException e) {
            logger.info("Caught IllegalStateException: ", e);
        }
    }
*/
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_sort_alphabetic:
                GlobalState.getUserAgenda().sortAlphabetically();
                ((ListView) mRoot.findViewById(R.id.s_l_listview1)).invalidateViews();
                Toast.makeText(getActivity(), "Agenda is sorted alphabetically", Toast.LENGTH_SHORT)
                        .show();
                return true;

            case R.id.menu_sort_distance:
                GlobalState.getUserAgenda().sortByDistance();
                ((ListView) mRoot.findViewById(R.id.s_l_listview1)).invalidateViews();
                Toast.makeText(getActivity(), "Agenda is sorted by distance", Toast.LENGTH_SHORT)
                        .show();
                return true;
                
            default:
                return false;
        }
    }
    
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mDlTask != null) {
            logger.trace("Cancelling image download task");
            mDlTask.cancel(true);
        }
        
        mAllPlacesCursor.close();
    }
    
    @Override
    public void updateLocation(Location loc) {
        if (loc == null) {
            return;
        }

        int closestIx = Geomancer.findClosestPlace(loc, mAllPlacesCursor);
        if (closestIx == -1) {
            Toast.makeText(getActivity(), "Couldn't find closest place", Toast.LENGTH_LONG).show();
        } else {
            mAllPlacesCursor.moveToPosition(closestIx);
            mCurrentPlace = DBUtils.getPlaceFromCursor(mAllPlacesCursor);
            fillViews();
        }
        
    }
    
    private void fillViews() {
        String desc = mCurrentPlace.getDescription();
        if (desc.length() > DESCRIPTION_LENGTH) {
            desc = desc.substring(0, DESCRIPTION_LENGTH).concat("...");
        }
        
        ((TextView) mRoot.findViewById(R.id.current_tv_name)).setText(
                Html.fromHtml("<b>" + mCurrentPlace.getName() + "</b> " + desc));
        
        ImageView iv = (ImageView)mRoot.findViewById(R.id.current_img);
        mDlTask = new ImageDownloader.BitmapDownloaderTask(iv);
        logger.trace("Starting image download task");
        mDlTask.execute(mCurrentPlace.getPictureLoc());

        
        
    }

}
