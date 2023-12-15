package com.example.mpl_base.activities

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.RemoteViews
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.mpl_base.R
import com.example.mpl_base.util.CalcUtil
import com.example.mpl_base.util.MyAppWidget
import com.example.mpl_base.util.NotificationUtil

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
        randomNumberTv = findViewById(R.id.main_random_number)
        randomizeBtn = findViewById(R.id.main_btn_randomize)
        trueBtn = findViewById(R.id.main_btn_true)
        falseBtn = findViewById(R.id.main_btn_false)

        updateRandomNumber()

        trueBtn.setOnClickListener {
            val number = randomNumberTv.text.toString().toInt()
            val isPrime = CalcUtil.checkIfPrime(number)

            if(isPrime){
                val intent = Intent(this, TrueActivity::class.java)
                intent.putExtra(getString(R.string.number), number)
                intent.putExtra(getString(R.string.is_prime_question), getString(R.string.is_text))
                startActivity(intent)
            } else {
                val intent = Intent(this, FalseActivity::class.java)
                intent.putExtra(getString(R.string.is_prime_question), getString(R.string.is_not_text))
                intent.putExtra(getString(R.string.number), number)
                startActivity(intent)
            }
        }

        falseBtn.setOnClickListener {
            val number = randomNumberTv.text.toString().toInt()
            val isPrime = CalcUtil.checkIfPrime(number)

            if(!isPrime){
                val intent = Intent(this, TrueActivity::class.java)
                intent.putExtra(getString(R.string.is_prime_question), getString(R.string.is_not_text))
                intent.putExtra(getString(R.string.number), number)
                startActivity(intent)
            } else {
                val intent = Intent(this, FalseActivity::class.java)
                intent.putExtra(getString(R.string.is_prime_question), getString(R.string.is_text))
                intent.putExtra(getString(R.string.number), number)
                startActivity(intent)
            }
        }


        randomizeBtn.setOnClickListener {
            updateRandomNumber()
        }

        /*
        notifyBtn.setOnClickListener {
            val number = randomNumberTv.text.toString().toInt()
            val isPrime = CalcUtil.checkIfPrime(number)

            val title: String
            val text: String
            val icon: Int

            if(isPrime){
                title = getString(R.string.yay)
                text = String.format(getString(R.string.answer_text), number, getString(R.string.is_text))
                icon = R.drawable.icon_true
            } else {
                title = getString(R.string.nay)
                text = String.format(getString(R.string.answer_text), number, getString(R.string.is_not_text))
                icon = R.drawable.icon_false
            }


        }

         */
    }

    private fun updateRandomNumber() {
        val randomNumber = CalcUtil.rng()
        randomNumberTv.text = randomNumber.toString()
        val appWidgetManager = AppWidgetManager.getInstance(this)
        val remoteViews = RemoteViews(this.packageName, R.layout.my_app_widget)
        val thisWidget = ComponentName(this, MyAppWidget::class.java)
        remoteViews.setTextViewText(R.id.appwidget_text, randomNumber.toString())
        appWidgetManager.updateAppWidget(thisWidget, remoteViews)
    }

}