package edu.vanderbilt.vm.guide.util;

public class SystemEnvoy extends android.app.Application {
	@Override public void onCreate() {
		super.onCreate();
		GlobalState.initializeGlobalState(this);
		Geomancer.activateGeolocation(this);
	}
}