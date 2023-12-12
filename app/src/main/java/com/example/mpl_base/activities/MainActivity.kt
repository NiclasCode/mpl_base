package com.example.mpl_base.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import com.example.mpl_base.R
import com.example.mpl_base.util.CalcUtil
import com.example.mpl_base.util.IS_PRIME
import com.example.mpl_base.util.NotificationUtil
import com.example.mpl_base.util.RANDOM_NUMBER

class MainActivity : AppCompatActivity() {
    private lateinit var randomNumberTv: TextView
    private lateinit var randomizeBtn: Button
    private lateinit var notifyBtn: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        NotificationUtil.createNotificationChannel(this)

        setUI()
    }

    private fun setUI() {
        randomNumberTv = findViewById(R.id.main_random_number)
        randomizeBtn = findViewById(R.id.main_btn_randomize)
        notifyBtn = findViewById(R.id.main_btn_notify)

        updateRandomNumber()

        randomizeBtn.setOnClickListener {
            updateRandomNumber()
        }

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

            val notifyIntent = Intent(this, MainActivity::class.java)
            notifyIntent.putExtra(RANDOM_NUMBER, number)
            notifyIntent.putExtra(IS_PRIME, isPrime)

            NotificationUtil.sendNotification(this, title, text, icon, notifyIntent)
        }
    }

    private fun updateRandomNumber() {
        val randomNumber = CalcUtil.rng()
        randomNumberTv.text = randomNumber.toString()
    }

}