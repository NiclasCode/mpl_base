package com.example.mpl_base.util

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.example.mpl_base.R

/**
 * Implementation of App Widget functionality.
 */
class MyAppWidget : AppWidgetProvider() {

    override fun onReceive(context: Context?, intent: Intent?) {
        val appWidgetId = intent!!.getIntExtra(APP_WIDGET_ID, 0)

        when (intent.action) {
            WidgetActionEnum.REFRESH.toString() -> {
                val number = intent.getIntExtra(context!!.getString(R.string.number), 0)
                updateAppWidget(
                    context,
                    AppWidgetManager.getInstance(context),
                    appWidgetId,
                    number
                )
            }

            WidgetActionEnum.NOTIFY.toString() -> {
                var number = 0
                if (context != null) {
                    number = intent.getIntExtra(context.getString(R.string.number), 0)
                    val isPrime = CalcUtil.checkIfPrime(
                        intent.getIntExtra(
                            context.getString(R.string.number),
                            0
                        )
                    )
                    val title: String
                    val text: String
                    val icon: Int
                    val btnPressed =
                        intent.getBooleanExtra(context.getString(R.string.btn_pressed), false)

                    val correctPress = (btnPressed == isPrime)

                    if (correctPress) {
                        title = context.getString(R.string.yay)
                        text = String.format(
                            context.getString(R.string.answer_text),
                            number,
                            if (isPrime) context.getString(R.string.is_text) else context.getString(
                                R.string.is_not_text
                            )
                        )
                        icon = R.drawable.icon_true
                    } else {
                        title = context.getString(R.string.nay)
                        text = String.format(
                            context.getString(R.string.answer_text),
                            number,
                            if (isPrime) context.getString(R.string.is_text) else context.getString(
                                R.string.is_not_text
                            )
                        )
                        icon = R.drawable.icon_false
                    }
                    val notifyIntent = Intent(context, MyAppWidget::class.java)
                    notifyIntent.putExtra(RANDOM_NUMBER, number)
                    notifyIntent.putExtra(IS_PRIME, isPrime)
                    NotificationUtil.createNotificationChannel(context)
                    NotificationUtil.sendNotification(context, title, text, icon, notifyIntent)
                }
                updateAppWidget(
                    context!!,
                    AppWidgetManager.getInstance(context),
                    appWidgetId,
                    number
                )
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
    appWidgetId: Int,
    number: Int
) {
    // Construct the RemoteViews object
    val views = RemoteViews(context.packageName, R.layout.my_app_widget)
    views.setTextViewText(R.id.appwidget_title, context.getString(R.string.appwidget_title))
    views.setTextViewText(R.id.appwidget_text, number.toString())


    views.setOnClickPendingIntent(R.id.appwidget_btn, refreshRandomNumber(context, appWidgetId))
    views.setOnClickPendingIntent(
        R.id.widget_btn_true,
        selectTrue(context, appWidgetId, number, true)
    )
    views.setOnClickPendingIntent(
        R.id.widget_btn_false,
        selectFalse(context, appWidgetId, number, false)
    )

    // Instruct the widget manager to update the widget
    appWidgetManager.updateAppWidget(appWidgetId, views)
}

internal fun refreshRandomNumber(context: Context, appWidgetId: Int): PendingIntent {
    val intent = Intent(context, MyAppWidget::class.java)
    intent.putExtra(APP_WIDGET_ID, appWidgetId)
    intent.putExtra(context.getString(R.string.number), CalcUtil.rng())
    intent.flags = Intent.FLAG_RECEIVER_FOREGROUND
    intent.action = WidgetActionEnum.REFRESH.toString()
    return PendingIntent.getBroadcast(
        context,
        appWidgetId,
        intent,
        PendingIntent.FLAG_UPDATE_CURRENT
    )
}

internal fun selectTrue(
    context: Context,
    appWidgetId: Int,
    number: Int,
    btnClicked: Boolean
): PendingIntent {
    val intent = Intent(context, MyAppWidget::class.java)
    intent.putExtra(APP_WIDGET_ID, appWidgetId)
    intent.putExtra(context.getString(R.string.number), number)
    intent.putExtra(context.getString(R.string.btn_pressed), btnClicked)
    intent.flags = Intent.FLAG_RECEIVER_FOREGROUND
    intent.action = WidgetActionEnum.NOTIFY.toString()
    return PendingIntent.getBroadcast(
        context,
        appWidgetId * 2,
        intent,
        PendingIntent.FLAG_UPDATE_CURRENT
    )
}

internal fun selectFalse(
    context: Context,
    appWidgetId: Int,
    number: Int,
    btnClicked: Boolean
): PendingIntent {
    val intent = Intent(context, MyAppWidget::class.java)
    intent.putExtra(APP_WIDGET_ID, appWidgetId)
    intent.putExtra(context.getString(R.string.number), number)
    intent.putExtra(context.getString(R.string.btn_pressed), btnClicked)
    intent.flags = Intent.FLAG_RECEIVER_FOREGROUND
    intent.action = WidgetActionEnum.NOTIFY.toString()
    return PendingIntent.getBroadcast(
        context,
        appWidgetId * 2 + 1,
        intent,
        PendingIntent.FLAG_UPDATE_CURRENT
    )
}