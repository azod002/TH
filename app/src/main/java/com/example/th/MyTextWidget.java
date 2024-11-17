package com.example.th;


import android.appwidget.AppWidgetProvider;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import androidx.room.Room;
import com.example.th.Database.db.AppDatabase;
import com.example.th.Database.db.Entity.ContentDB;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MyTextWidget extends AppWidgetProvider {

    private static final ExecutorService executorService = Executors.newSingleThreadExecutor();

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        AppDatabase database = Room.databaseBuilder(context.getApplicationContext(),
                AppDatabase.class, "ContentAppDB").build();

        for (int appWidgetId : appWidgetIds) {
            executorService.execute(() -> {
                List<ContentDB> last3Entries = database.getContentDao().getLast3Entries();

                StringBuilder widgetTextBuilder = new StringBuilder();
                for (int i = last3Entries.size() - 1; i >= 0; i--) {
                    widgetTextBuilder.append(last3Entries.get(i).getContent());
                    widgetTextBuilder.append(", ");
                    if (i != 0) {
                        widgetTextBuilder.append("\n");
                    }
                }
                String widgetText = widgetTextBuilder.toString();

                RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.my_text_widget);
                views.setTextViewText(R.id.widget_text, widgetText);

                appWidgetManager.updateAppWidget(appWidgetId, views);
            });
        }
    }

    @Override
    public void onEnabled(Context context) {
    }

    @Override
    public void onDisabled(Context context) {
        executorService.shutdown();
    }

    public static void updateAllWidgets(Context context) {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        ComponentName thisWidget = new ComponentName(context, MyTextWidget.class);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);

        Intent intent = new Intent(context, MyTextWidget.class);
        intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds);

        context.sendBroadcast(intent);
    }
}



