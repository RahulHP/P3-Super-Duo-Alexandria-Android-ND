package barqsoft.footballscores;

import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViewsService;

/**
 * Created by root on 2/4/16.
 */
public class ScoresWidgetRemoteViewsService extends RemoteViewsService{

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        Log.i("Service","CREATED");
        return new WidgetRemoteViewsFactory(this);
    }
}
