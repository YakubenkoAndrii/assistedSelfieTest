package com.example.assistedselfietest

import android.os.Bundle
import android.widget.Button
import android.widget.Toast

class MainActivity : BaseActivitySelfie() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val button = findViewById<Button>(R.id.start)
        button.setOnClickListener {
            Toast.makeText(this, "Start clicked", Toast.LENGTH_SHORT).show()
            startActivityWithThreeRandomActions()
        }
    }
}
