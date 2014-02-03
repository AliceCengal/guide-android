package edu.vanderbilt.vm.guide.util;

/**
 * Provides the bridge between the Android system and the App's service layer.
 * The Context object is needed to access the services provided by the Android
 * system. This class will be instantiated by the system as soon as the process
 * for the app is created. Each service class should provide an `initialize` method
 * which accept a Context object to be called by the SystemEnvoy instance created
 * by the system.
 * 
 * @author athran
 */
public class SystemEnvoy extends android.app.Application {
	@Override public void onCreate() {
		super.onCreate();
		GlobalState.initializeGlobalState(this);
		Geomancer.activateGeolocation(this);
	}
}