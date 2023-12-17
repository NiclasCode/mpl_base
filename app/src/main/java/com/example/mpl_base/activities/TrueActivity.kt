package com.example.mpl_base.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.mpl_base.R
import com.example.mpl_base.util.CalcUtil
import com.example.mpl_base.util.RANDOM_NUMBER

class TrueActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_true)
        setUI()
    }

    private fun setUI() {
        val button: Button = findViewById(R.id.true_btn_back)
        val text: TextView = findViewById(R.id.true_text)
        val number: Int = intent.getIntExtra(RANDOM_NUMBER, 0)
        val isPrime: Boolean = CalcUtil.checkIfPrime(number)
        val primetext = if(isPrime) getString(R.string.is_text) else getString(R.string.is_not_text)

        text.text = String.format(getString(R.string.answer_text), number, primetext)

        // set back button to launch mainActivity again
        button.setOnClickListener {
            val backIntent = Intent(this, MainActivity::class.java)
            backIntent.putExtra(RANDOM_NUMBER, CalcUtil.rng())
            startActivity(backIntent)
        }
    }
}