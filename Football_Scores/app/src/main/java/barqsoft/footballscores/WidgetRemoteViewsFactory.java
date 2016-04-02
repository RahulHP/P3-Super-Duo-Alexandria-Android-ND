package barqsoft.footballscores;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Binder;
import android.os.Bundle;
import android.util.Log;
import android.widget.AdapterView;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by root on 2/4/16.
 */
public class WidgetRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {
    private static String LOG_TAG = "WidgetRemoteViewsFactory";
    private static final String[] FOOTBALL_SCORES_COLUMNS = {
            DatabaseContract.scores_table.MATCH_ID,
            DatabaseContract.scores_table.DATE_COL,
            DatabaseContract.scores_table.TIME_COL,
            DatabaseContract.scores_table.HOME_COL,
            DatabaseContract.scores_table.AWAY_COL,
            DatabaseContract.scores_table.HOME_GOALS_COL,
            DatabaseContract.scores_table.AWAY_GOALS_COL,
            DatabaseContract.scores_table.LEAGUE_COL,
            DatabaseContract.scores_table.MATCH_DAY
    };

    private static final int MATCH_ID = 0;
    private static final int HOME_COL = 3;
    private static final int AWAY_COL = 4;
    private static final int HOME_GOALS_COL = 5;
    private static final int AWAY_GOALS_COL = 6;

    private Cursor data = null;
    private Context mContext;

    public WidgetRemoteViewsFactory(Context mContext) {
        Log.i(LOG_TAG,"CREATED");
        this.mContext = mContext.getApplicationContext();
    }

    @Override
    public void onCreate() {
        Log.i(LOG_TAG,"CREATED");
    }

    @Override
    public void onDataSetChanged() {
        if (data != null) {
            data.close();
        }

        final long identityToken = Binder.clearCallingIdentity();
        Uri uri = DatabaseContract.scores_table.buildScoreWithDate();
        data = mContext.getContentResolver().query(uri,
                FOOTBALL_SCORES_COLUMNS,
                null,
                new String[]{new SimpleDateFormat("yyyy-MM-dd").format(new Date(System.currentTimeMillis()))},
                DatabaseContract.scores_table.HOME_GOALS_COL + " ASC");
        Binder.restoreCallingIdentity(identityToken);
    }

    @Override
    public void onDestroy() {
        if (data != null) {
            data.close();
            data = null;
        }
    }

    @Override
    public int getCount() {
        return data == null ? 0 : data.getCount();
    }

    @Override
    public RemoteViews getViewAt(int position) {

        if (position == AdapterView.INVALID_POSITION || data == null || !data.moveToPosition(position)) {
            Log.i(LOG_TAG,"NOTHING");
            return null;
        }

        RemoteViews views = new RemoteViews(mContext.getPackageName(), R.layout.widget_football_score);

        String homeTeam = data.getString(HOME_COL);
        Log.i(LOG_TAG,homeTeam);
        int homeGoals = data.getInt(HOME_GOALS_COL);
        String awayTeam = data.getString(AWAY_COL);
        int awayGoals = data.getInt(AWAY_GOALS_COL);


        // Add the data to the RemoteViews
        views.setTextViewText(R.id.home_team, homeTeam);
        views.setTextViewText(R.id.away_team, awayTeam);
        views.setTextViewText(R.id.match_score, Utilies.getScores(homeGoals, awayGoals));
        views.setContentDescription(R.id.home_team,homeTeam);
        views.setContentDescription(R.id.away_team,awayTeam);

        // Set FillInIntent to be used in the PendingIntentTemplate
        Bundle bundle = new Bundle();
        bundle.putInt(ScoresWidgetProvider.EXTRA_ITEM, data.getInt(MATCH_ID));
        Intent fillInIntent = new Intent();
        fillInIntent.putExtras(bundle);
        views.setOnClickFillInIntent(R.id.widget, fillInIntent);

        return views;
    }

    @Override
    public RemoteViews getLoadingView() {
        return new RemoteViews(mContext.getPackageName(), R.layout.widget_football_score);
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        if (data.moveToPosition(position))
            return data.getLong(MATCH_ID);
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }
}
