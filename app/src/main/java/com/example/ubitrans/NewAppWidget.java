package com.example.ubitrans;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.os.CountDownTimer;
import android.widget.RemoteViews;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Handler;

/**
 * Implementation of App Widget functionality.
 */
public class NewAppWidget extends AppWidgetProvider {
 private String hora;
 private String fecha;

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        CharSequence widgetText = "UBITRANS";
        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.new_app_widget);
        RemoteViews viewss = new RemoteViews(context.getPackageName(), R.layout.new_app_widget);
        views.setTextViewText(R.id.texfecha, widgetText);
        String mensaje = "Hora actual : ";
        Calendar calendario = new GregorianCalendar();

        String hora = calendario.getTime().toLocaleString();
        // Instruct the widget manager to update the widget
        DateFormat timeInstance= SimpleDateFormat.getTimeInstance();
      String  hora_sistema=timeInstance.format(Calendar.getInstance().getTime());


        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Date date = new Date();
        DateFormat df = new SimpleDateFormat("EEE, d MMM yyyy");
        String dateee = df.format(Calendar.getInstance().getTime());
        String fecha = dateFormat.format(date);
        views.setTextViewText(R.id.texfecha,  hora_sistema);
        views.setTextViewText(R.id.texfechaano, dateee);
        appWidgetManager.updateAppWidget(appWidgetId, views);



    }

    @Override
    public void onUpdate(final Context context, final AppWidgetManager appWidgetManager, final int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them


        super.onUpdate(context, appWidgetManager, appWidgetIds);

        new CountDownTimer(1000, 1000) {
            public void onTick(long millisUntilFinished) {
                for (int i = 0; i<appWidgetIds.length; i++){
                    int widgetId = appWidgetIds[i];
                    updateAppWidget(context, appWidgetManager, widgetId);

                }
            }

            public void onFinish() {
              this.start();
            }
        }.start();


    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}