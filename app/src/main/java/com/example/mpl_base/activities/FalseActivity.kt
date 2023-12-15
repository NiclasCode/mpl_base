package com.example.mpl_base.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import com.example.mpl_base.R

class FalseActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_false)
        setUI()
    }

    private fun setUI(){
        val button: Button = findViewById(R.id.false_btn_back)
        val text: TextView = findViewById(R.id.false_text)
        val number: Int = intent.getIntExtra(getString(R.string.number), 0)
        val isPrime: String? = intent.getStringExtra(getString(R.string.is_prime_question))

        if(isPrime != null){
            text.text = String.format(getString(R.string.answer_text), number, isPrime)
        }

        button.setOnClickListener {
            val backIntent = Intent(this, MainActivity::class.java)
            startActivity(backIntent)
        }
    }
}