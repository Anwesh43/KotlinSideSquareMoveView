package com.example.anweshmishra.kotlinsquaresidemoveview

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.example.squaresidemoveview.SquareSideMoveView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        SquareSideMoveView.create(this)
    }
}
