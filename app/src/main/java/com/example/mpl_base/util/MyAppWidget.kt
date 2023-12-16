package com.example.mpl_base.util

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.example.mpl_base.R
import com.example.mpl_base.activities.FalseActivity
import com.example.mpl_base.activities.MainActivity
import com.example.mpl_base.activities.TrueActivity

/**
 * Implementation of App Widget functionality.
 */
class MyAppWidget : AppWidgetProvider() {

    override fun onReceive(context: Context?, intent: Intent?) {
        val appWidgetId = intent!!.getIntExtra(APP_WIDGET_ID, 0)

        when (intent.action) {
            WidgetActionEnum.REFRESH.toString() -> {
                val number = CalcUtil.rng()
                updateAppWidget(
                    context!!,
                    AppWidgetManager.getInstance(context),
                    appWidgetId,
                    number
                )
            }

            WidgetActionEnum.NOTIFY.toString() -> {
                notify(context!!, intent, appWidgetId)
            }

            WidgetActionEnum.SYNC.toString() -> {
                val number = intent.getIntExtra(RANDOM_NUMBER, 0)
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
}

internal fun updateAppWidget(
    context: Context,
    appWidgetManager: AppWidgetManager,
    appWidgetId: Int,
    number: Int
) {
    // Construct the RemoteViews object
    val views = RemoteViews(context.packageName, R.layout.my_app_widget)
    views.setTextViewText(R.id.appwidget_title, context.getString(R.string.is_prime_question))
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
    views.setOnClickPendingIntent(R.id.appwidget_title, startMainActivity(context, number))

    // Instruct the widget manager to update the widget
    appWidgetManager.updateAppWidget(appWidgetId, views)
}

internal fun startMainActivity(context: Context, number: Int): PendingIntent {
    val intent = Intent(context, MainActivity::class.java)
    intent.putExtra(RANDOM_NUMBER, number)
    return PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
}


internal fun refreshRandomNumber(context: Context, appWidgetId: Int): PendingIntent {
    val intent = Intent(context, MyAppWidget::class.java)
    intent.putExtra(APP_WIDGET_ID, appWidgetId)
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
    intent.putExtra(RANDOM_NUMBER, number)
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
    intent.putExtra(RANDOM_NUMBER, number)
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

internal fun notify(context: Context, intent: Intent, appWidgetId: Int) {
    val number = intent.getIntExtra(RANDOM_NUMBER, 0)
    val isPrime = CalcUtil.checkIfPrime(
        intent.getIntExtra(
            RANDOM_NUMBER,
            0
        )
    )
    val title: String
    val text: String
    val icon: Int
    val notifyIntent: Intent

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
        notifyIntent = Intent(context, TrueActivity::class.java)
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
        notifyIntent = Intent(context, FalseActivity::class.java)
    }
    notifyIntent.putExtra(RANDOM_NUMBER, number)

    NotificationUtil.createNotificationChannel(context)
    NotificationUtil.sendNotification(context, title, text, icon, notifyIntent)

    updateAppWidget(
        context,
        AppWidgetManager.getInstance(context),
        appWidgetId,
        number
    )
}