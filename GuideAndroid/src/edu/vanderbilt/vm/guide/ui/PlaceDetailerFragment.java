
package edu.vanderbilt.vm.guide.ui;

import java.text.DecimalFormat;

import android.os.Handler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import edu.vanderbilt.vm.guide.R;
import edu.vanderbilt.vm.guide.container.Place;
import edu.vanderbilt.vm.guide.db.GuideDBOpenHelper;
import edu.vanderbilt.vm.guide.util.DBUtils;
import edu.vanderbilt.vm.guide.util.GlobalState;
import edu.vanderbilt.vm.guide.util.GuideConstants;
import edu.vanderbilt.vm.guide.util.ImageDownloader;

public class PlaceDetailerFragment extends SherlockFragment {
	
    private Place     mPlace;
    private TextView  tvPlaceName;
    private TextView  tvPlaceCat;
    private TextView  tvPlaceHours;
    private TextView  tvPlaceGeo;
    private TextView  tvPlaceMediae;
    private TextView  tvPlaceDesc;
    private ImageView ivPlaceImage;
    
    private View    mView;
    private Menu    mMenu;
    private Handler mHandler;
    private boolean isOnAgenda = false;
    private ImageDownloader.BitmapDownloaderTask mDlTask;
    
    private static final Logger logger    = LoggerFactory.getLogger("ui.PlaceDetailerFragment");
    private static final String PLC_ID    = "id";
    private static final DecimalFormat df = new DecimalFormat("#.##");

    /**
     * Create a new instance of the PlaceDetail fragment. This method returns a
     * Fragment which you can add to a ViewGroup.
     *
     * @param ctx
     * @param id the UniqueId of the place to be detailed.
     * @return
     */
    static PlaceDetailerFragment newInstance(Context ctx, int id) {
        PlaceDetailerFragment frag = (PlaceDetailerFragment) Fragment
        		.instantiate(
        				ctx, 
        				"edu.vanderbilt.vm.guide.ui.PlaceDetailerFragment");

        Bundle arg = new Bundle();
        arg.putInt(PLC_ID, id);
        frag.setArguments(arg);
        
        return frag;
    }

    @Override
    public View onCreateView(
    		LayoutInflater inflater, 
    		ViewGroup container, 
    		Bundle savedInstanceState) {
        mView = inflater.inflate(
        		R.layout.fragment_place_detailer, 
        		container, 
        		false);
        return mView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setHasOptionsMenu(true);
        mHandler = new Handler();

        if (mPlace == null) {
            Bundle args = getArguments();
            logger.debug("Using information from argument");
            if (args != null) {
                int placeId = args.getInt(
                		PLC_ID,
                        GuideConstants.BAD_PLACE_ID);
                mPlace = DBUtils.getPlaceById(
                		placeId, 
                		GlobalState.getReadableDatabase(null));
            }
        }

        /* Check if this place is already on Agenda */
        if (GlobalState.getUserAgenda().isOnAgenda(mPlace)) {
            isOnAgenda = true;
        } else {
            isOnAgenda = false;
        }
        
        setupUI();
        updateInformation();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mDlTask != null) {
            logger.trace("Cancelling image download task");
            mDlTask.cancel(true);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.place_detail_activity, menu);
        this.mMenu = menu;

        if (isOnAgenda) {
             // The default icon is a "+" therefore change to "-"
            mMenu.findItem(R.id.menu_add_agenda).setIcon(
                    getResources().getDrawable(R.drawable.content_remove));
        } else {
            // Use default icon "+" as defined in xml
        }
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
            case R.id.menu_add_agenda:
                addRemoveToAgenda();
                return true;
            case R.id.menu_map:
                MapViewer.openPlace(getActivity(), mPlace.getUniqueId());
                return true;
            case android.R.id.home:
                GuideMain.open(getActivity());
                return true;
            case R.id.menu_about:
                About.open(getActivity());
                return true;
            default:
                return false;
        }
    }

    private void addRemoveToAgenda() {
        if (isOnAgenda) {
            GlobalState.getUserAgenda().remove(mPlace);
            mMenu.findItem(R.id.menu_add_agenda).setIcon(
                    getResources().getDrawable(R.drawable.content_new));
            Toast.makeText(getActivity(), "Removed from Agenda", Toast.LENGTH_SHORT).show();
        } else {
            GlobalState.getUserAgenda().add(mPlace);
            mMenu.findItem(R.id.menu_add_agenda).setIcon(
                    getResources().getDrawable(R.drawable.content_remove));
            Toast.makeText(getActivity(), "Added to Agenda", Toast.LENGTH_SHORT).show();
        }
        isOnAgenda = !isOnAgenda;
    }

    /**
     * To change the Place detailed on the page.
     * 
     * @param plc
     */
    void setPlaceDetailed(Place plc) {
        mPlace = plc;
        updateInformation();
    }

    /*
     * Update the information showed in the various Views based on mPlace
     */
    private void updateInformation() {
        tvPlaceName  .setText(mPlace.getName());
        tvPlaceCat   .setText(Html.fromHtml(
        		"<b>Category:</b> " + 
        		mPlace.getCategories().get(0)));
        
        tvPlaceHours .setText(Html.fromHtml(
        		"<b>Hours:</b> " + mPlace.getHours()));
        
        tvPlaceGeo.setText(Html.fromHtml(
                "<b>Geopoint:</b> " 
                + df.format(mPlace.getLatitude()) + ", " 
                + df.format(mPlace.getLongitude())));
        
        tvPlaceMediae.setText(Html.fromHtml(
        		"<em>" + "Medias available." + "</em>"));
        
        tvPlaceDesc  .setText(mPlace.getDescription());

        logger.trace("Starting image download task");
        mDlTask = new ImageDownloader.BitmapDownloaderTask(ivPlaceImage);

        mDlTask.setRunOnFinishDownload(new Runnable() {
            @Override public void run() {
                mHandler.postDelayed(
                        new Runnable() {
                            @Override public void run() {
                                ivPlaceImage.setVisibility(View.VISIBLE); }},

                        500); }}); // MAGIC

        mDlTask.execute(mPlace.getPictureLoc());
    }

    private void setupUI() {
        tvPlaceName   = (TextView)  mView.findViewById(R.id.fpd_tv1);
        tvPlaceCat    = (TextView)  mView.findViewById(R.id.fpd_tv2);
        tvPlaceHours  = (TextView)  mView.findViewById(R.id.fpd_tv3);
        tvPlaceGeo    = (TextView)  mView.findViewById(R.id.fpd_tv4);
        tvPlaceMediae = (TextView)  mView.findViewById(R.id.fpd_tv5);
        tvPlaceDesc   = (TextView)  mView.findViewById(R.id.fpd_tv6);
        ivPlaceImage  = (ImageView) mView.findViewById(R.id.fpd_image1);
    }

}
