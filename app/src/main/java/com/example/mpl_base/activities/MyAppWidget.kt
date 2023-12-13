package com.example.mpl_base.activities

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.security.identity.PersonalizationData
import android.widget.RemoteViews
import com.example.mpl_base.R
import com.example.mpl_base.util.APP_WIDGET_ID
import com.example.mpl_base.util.CalcUtil
import com.example.mpl_base.util.WidgetActionEnum

/**
 * Implementation of App Widget functionality.
 */
class MyAppWidget : AppWidgetProvider() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val appWidgetId = intent!!.getIntExtra(APP_WIDGET_ID, 0)

        when(intent.action){
            WidgetActionEnum.REFRESH.toString() -> {
                val number = CalcUtil.rng()
                updateAppWidget(context!!, AppWidgetManager.getInstance(context), appWidgetId, number)
            }
        }
        super.onReceive(context, intent)
    }
    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        // There may be multiple widgets active, so update all of them
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId, 0)
        }
    }

    override fun onEnabled(context: Context) {
        // Enter relevant functionality for when the first widget is created
    }

    override fun onDisabled(context: Context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

internal fun updateAppWidget(
    context: Context,
    appWidgetManager: AppWidgetManager,
    number: Int,
    appWidgetId: Int
) {
    val widgetText = context.getString(R.string.appwidget_text)
    // Construct the RemoteViews object
    val views = RemoteViews(context.packageName, R.layout.my_app_widget)
    views.setTextViewText(R.id.appwidget_title, context.getString((R.string.appwidget_title)))
    views.setTextViewText(R.id.appwidget_text, number.toString())

    views.setOnClickPendingIntent(R.id.appwidget_btn, refrshRandomNumber(context, R.id.appwidget_text))
    // Instruct the widget manager to update the widget
    appWidgetManager.updateAppWidget(appWidgetId, views)
}

internal fun refrshRandomNumber(context: Context, appWidgetId: Int):PendingIntent{
    val refreshIntent = Intent(context, MyAppWidget::class.java)
    refreshIntent.putExtra(APP_WIDGET_ID, appWidgetId)
    refreshIntent.flags = Intent.FLAG_RECEIVER_FOREGROUND
    refreshIntent.action = WidgetActionEnum.REFRESH.toString()
    return PendingIntent.getBroadcast(context, appWidgetId, refreshIntent, PendingIntent.FLAG_UPDATE_CURRENT)
}