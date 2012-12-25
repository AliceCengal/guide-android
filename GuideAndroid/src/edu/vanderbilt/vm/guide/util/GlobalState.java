package edu.vanderbilt.vm.guide.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.content.Context;
import android.database.Cursor;
import edu.vanderbilt.vm.guide.annotations.NeedsTesting;
import edu.vanderbilt.vm.guide.container.Agenda;
import edu.vanderbilt.vm.guide.container.Place;
import edu.vanderbilt.vm.guide.db.GuideDBConstants;
import edu.vanderbilt.vm.guide.db.GuideDBOpenHelper;

/**
 * This class holds singletons of certain objects we need to share throughout
 * the application, such as the user's agenda. This is simpler and easier than
 * using a SQLite database to hold the agenda and allows us to use several
 * methods to make data transactions with the agenda easier.
 * 
 * @author nicholasking
 * 
 */
@NeedsTesting(lastModifiedDate = "12/22/12")
public class GlobalState {

	private static Agenda userAgendaSingleton = new Agenda();
	
	/**
	 * @deprecated The place list singleton should no longer be used.
	 * A SQLite database serves this purpose.
	 */
	@Deprecated
	private static List<Place> sPlaceList;
	
	private static Logger logger = LoggerFactory.getLogger("util.GlobalState");

	private GlobalState() {
		throw new AssertionError("Do not instantiate this class.");
	}

	public static Agenda getUserAgenda() {
		return userAgendaSingleton;
	}

	/**
	 * Returns a list of places stored in the places.json file.
	 * 
	 * @deprecated Query the places table of the SQLite database instead
	 * @param context the Context to use to open an input stream
	 * @return a list of all places in the places.json file
	 */
	@Deprecated
	public static List<Place> getPlaceList(Context context) {
		if (sPlaceList == null) {
			try {
				sPlaceList = JsonUtils.readPlacesFromInputStream(context.getAssets()
						.open(GuideDBConstants.PLACES_JSON_NAME));
			} catch (IOException e) {
				logger.error("JSON import failed", e);
			}
		}
		return sPlaceList;
	}

	/**
	 * Returns a place given the unique ID of that place
	 * @deprecated Query the places table of the SQLite database instead
	 * @param id The id of the place to find
	 * @return The place with the given id
	 */
	@Deprecated
	public static Place getPlaceById(int id) {
		if (sPlaceList == null) {
			return null;
		}

		for (int n = 0; n < sPlaceList.size(); n++) {
			if (sPlaceList.get(n).getUniqueId() == id) {
				return sPlaceList.get(n);
			}
		}

		return null; // If search failed
	}
	
	// History Singleton
	private static Agenda userHistory = new Agenda();
	
	static {
		resetHistory();
	}
	
	/**
	 * Returns the user's history.
	 * 
	 * @return user History
	 */
	public static Agenda getUserHistory() {
		return userHistory;
	}
	
	/**
	 * Add a place to history. The list is arranged most recent first
	 * 
	 * @param plc
	 */
	public static void addHistory(Place plc){
		if (userHistory.size() == 0){
			userHistory.add(plc);
		} else if (userHistory.get(0).getUniqueId() == 1000) {
			userHistory.overwrite(new Agenda());
			userHistory.add(plc);
		} else if (userHistory.isOnAgenda(plc)){
			// bring this place to the top

			Agenda temp = new Agenda();
			temp.add(plc);
			
			for (int i = 0; i < userHistory.size(); i++){
				temp.add(userHistory.get(i));
			} // TODO
			
		} else {
			userHistory.add(plc);
		}
		
	}
	
	/**
	 * Empties the history.
	 */
	public static void resetHistory(){
		if (userHistory == null){
			userHistory = new Agenda();
		} else {
			userHistory.overwrite(new Agenda());
		}
		
		Place temp = (new Place.Builder())
				.setName("History is Empty")
				.setUniqueId(1000).build(); // TODO
		userHistory.add(temp);
	}
	// END History Singleton
	
	// Place list by Agenda
	private static Agenda newPlaceList = new Agenda();
	
	public static Agenda getAllPlace(Context ctx) {
		if (newPlaceList.size() == 0) {
			
			GuideDBOpenHelper helper = new GuideDBOpenHelper(ctx);
			String[] columns = { GuideDBConstants.PlaceTable.NAME_COL,
					GuideDBConstants.PlaceTable.CATEGORY_COL,
					GuideDBConstants.PlaceTable.ID_COL };
			Cursor cursor = DBUtils.getAllPlaces(columns, helper.getReadableDatabase());
			
			cursor.moveToFirst();
			while (!cursor.isLast()) {
				newPlaceList.add(DBUtils.getPlaceFromCursor(cursor));
				cursor.moveToNext();
			}
			newPlaceList.add(DBUtils.getPlaceFromCursor(cursor));
			helper.close();
		}
		return newPlaceList;
	}
}
