package bravin.shi.com.multinested;

import android.app.Application;

import bravin.shi.com.multinested.utils.Utils;

public class MultiApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        //Utils
        Utils.init(this);
    }
}
