package edu.vanderbilt.vm.guide.ui;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.actionbarsherlock.app.SherlockFragmentActivity;

import edu.vanderbilt.vm.guide.R;
import edu.vanderbilt.vm.guide.ui.controllers.Controller;
import edu.vanderbilt.vm.guide.ui.controllers.TourManagerController;

public class ActTourEditor extends SherlockFragmentActivity {

/**
 * Opens the Tour Editor Activity.
 * 
 * @param ctx
 */
public static void open(Context ctx) {

	Intent i = new Intent();
	i.setClass(ctx, ActTourEditor.class);
	ctx.startActivity(i);

}


// Model
public static List<String> mModel = new ArrayList<String>(); 

public Controller mController;

// Messages
public static final int MESSAGE_BACKPRESSED = 0;


@Override
public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.single_pane);
    mController = TourManagerController.getInstance(this, R.id.sp_pane1);
}

@Override
public void onBackPressed() {
    if (!mController.handleMessage(MESSAGE_BACKPRESSED))
        super.onBackPressed();
}

}







