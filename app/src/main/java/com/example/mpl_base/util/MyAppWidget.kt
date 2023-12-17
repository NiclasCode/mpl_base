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
            // action to refresh number on the widget
            WidgetActionEnum.REFRESH.toString() -> {
                // calc new number
                val number = CalcUtil.rng()
                // update widget
                updateAppWidget(
                    context!!,
                    AppWidgetManager.getInstance(context),
                    appWidgetId,
                    number
                )
            }
            // action to send notification whether the answer was right or wrong
            WidgetActionEnum.NOTIFY.toString() -> {
                notify(context!!, intent, appWidgetId)
            }
            // action to sync the widget to the number of the incoming intent
            WidgetActionEnum.SYNC.toString() -> {
                // get number from intent
                val number = intent.getIntExtra(RANDOM_NUMBER, 0)
                // update widget
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
    // set texts of the Textviews
    views.setTextViewText(R.id.appwidget_title, context.getString(R.string.is_prime_question))
    views.setTextViewText(R.id.appwidget_text, number.toString())

    // set OnClickPending Intents of the buttons and the title
    views.setOnClickPendingIntent(R.id.appwidget_btn, refreshRandomNumber(context, appWidgetId))
    views.setOnClickPendingIntent(
        R.id.widget_btn_true,
        selectButton(context, appWidgetId, number, true)
    )
    views.setOnClickPendingIntent(
        R.id.widget_btn_false,
        selectButton(context, appWidgetId, number, false)
    )
    views.setOnClickPendingIntent(R.id.appwidget_title, startMainActivity(context, number))

    // Instruct the widget manager to update the widget
    appWidgetManager.updateAppWidget(appWidgetId, views)
}

/**
 * function that returns a pending intent, that starts the main activity and sends the current random number to it
 * @param context Context
 * @param number current number of the widget that should be shown on start of main activity as well
 * @return PendingIntent that launches the main activity and has Extra Random Number
 */
internal fun startMainActivity(context: Context, number: Int): PendingIntent {
    val intent = Intent(context, MainActivity::class.java)
    intent.putExtra(RANDOM_NUMBER, number)
    return PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
}

/**
 * function that returns a pending intent
 * the intent action is to refresh the widget
 * @param context Context
 * @param appWidgetId Id of the widget to be refreshed
 * @return PendingIntent that refreshes the widget
 */
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

/**
 * function that returns a pending intent
 * the intent action is to send a notification whether the answer was right or wrong
 * @param context Context
 * @param appWidgetId Id of the widget to be refreshed
 * @param number number to be checked for prime
 * @param btnClicked Boolean whether button for true or false to is_prime_question is clicked
 * @return PendingIntent that send a notification containing the correctness of the answer
 */
internal fun selectButton(
    context: Context,
    appWidgetId: Int,
    number: Int,
    btnClicked: Boolean
): PendingIntent {
    // request Codes for true/false button have to be different -> one is even the other is odd
    val requestCode = if (btnClicked) appWidgetId * 2 else appWidgetId * 2 + 1
    val intent = Intent(context, MyAppWidget::class.java)
    intent.putExtra(APP_WIDGET_ID, appWidgetId)
    intent.putExtra(RANDOM_NUMBER, number)
    intent.putExtra(context.getString(R.string.btn_pressed), btnClicked)
    intent.flags = Intent.FLAG_RECEIVER_FOREGROUND
    intent.action = WidgetActionEnum.NOTIFY.toString()
    return PendingIntent.getBroadcast(
        context,
        requestCode,
        intent,
        PendingIntent.FLAG_UPDATE_CURRENT
    )
}

/**
 * sends a notification containing the correctness of the answer and the solution
 * @param context Context
 * @param intent current intent containing the inputted answer
 * @param appWidgetId current app Widget Id
 */
internal fun notify(context: Context, intent: Intent, appWidgetId: Int) {
    // get number
    val number = intent.getIntExtra(RANDOM_NUMBER, 0)
    // check if prime
    val isPrime = CalcUtil.checkIfPrime(
        intent.getIntExtra(
            RANDOM_NUMBER,
            0
        )
    )
    // get pressed button
    val btnPressed =
        intent.getBooleanExtra(context.getString(R.string.btn_pressed), false)
    // check whether answer is correct
    val correctPress = (btnPressed == isPrime)

    val title: String
    val text: String
    val icon: Int
    val notifyIntent: Intent

    // set values according to correctness
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

    // send notification
    NotificationUtil.sendNotification(context, title, text, icon, notifyIntent)

    updateAppWidget(
        context,
        AppWidgetManager.getInstance(context),
        appWidgetId,
        number
    )
}