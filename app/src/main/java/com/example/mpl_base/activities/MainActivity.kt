package com.example.mpl_base.activities

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.mpl_base.R
import com.example.mpl_base.util.APP_WIDGET_ID
import com.example.mpl_base.util.CalcUtil
import com.example.mpl_base.util.MyAppWidget
import com.example.mpl_base.util.NotificationUtil
import com.example.mpl_base.util.RANDOM_NUMBER
import com.example.mpl_base.util.WidgetActionEnum

class MainActivity : AppCompatActivity() {
    private lateinit var randomNumberTv: TextView
    private lateinit var randomizeBtn: Button
    private lateinit var trueBtn: ImageButton
    private lateinit var falseBtn: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        NotificationUtil.createNotificationChannel(this)

        setUI()
    }

    private fun setUI() {
        // connect controller with ui
        randomNumberTv = findViewById(R.id.main_random_number)
        randomizeBtn = findViewById(R.id.main_btn_randomize)
        trueBtn = findViewById(R.id.main_btn_true)
        falseBtn = findViewById(R.id.main_btn_false)

        // set number to number from intent, if no intent (f.ex. at start of app) set number to 0
        randomNumberTv.text = intent.getIntExtra(RANDOM_NUMBER, 0).toString()
        // update ALL widgets with the intent number
        updateWidgets(intent.getIntExtra(RANDOM_NUMBER, 0))

        // set true button listener
        trueBtn.setOnClickListener {
            val number = randomNumberTv.text.toString().toInt()
            val isPrime = CalcUtil.checkIfPrime(number)

            if (isPrime) {
                val intent = Intent(this, TrueActivity::class.java)
                intent.putExtra(getString(R.string.is_prime_question), getString(R.string.is_text))

            } else {
                val intent = Intent(this, FalseActivity::class.java)
                intent.putExtra(
                    getString(R.string.is_prime_question), getString(R.string.is_not_text)
                )
            }
            intent.putExtra(RANDOM_NUMBER, number)
            startActivity(intent)
        }

        // set false button listener
        falseBtn.setOnClickListener {
            val number = randomNumberTv.text.toString().toInt()
            val isPrime = CalcUtil.checkIfPrime(number)

            if (!isPrime) {
                val intent = Intent(this, TrueActivity::class.java)
                intent.putExtra(
                    getString(R.string.is_prime_question), getString(R.string.is_not_text)
                )
            } else {
                val intent = Intent(this, FalseActivity::class.java)
                intent.putExtra(getString(R.string.is_prime_question), getString(R.string.is_text))
            }
            intent.putExtra(RANDOM_NUMBER, number)
            startActivity(intent)
        }

        randomizeBtn.setOnClickListener {
            updateRandomNumber()
        }
    }

    /**
     * calculates a new random number and sets text on mainActivity and updates all widgets to that number
     */
    private fun updateRandomNumber() {
        val randomNumber = CalcUtil.rng()
        randomNumberTv.text = randomNumber.toString()
        updateWidgets(randomNumber)
    }

    /**
     * updates all widget with the input number
     * @param randomNumber number that should be shown on all widgets
     */
    private fun updateWidgets(randomNumber: Int) {
        // gets all active widget ids and loops over them
        for (id in AppWidgetManager.getInstance(this).getAppWidgetIds(
            ComponentName(this, MyAppWidget::class.java)
        )) {
            // intent for widget with widget id and random number
            val intent = Intent(this, MyAppWidget::class.java)
            intent.putExtra(APP_WIDGET_ID, id)
            intent.putExtra(RANDOM_NUMBER, randomNumber)
            // sync action to update the widget
            intent.action = WidgetActionEnum.SYNC.toString()
            // send to the widget
            sendBroadcast(intent)
        }
    }


}