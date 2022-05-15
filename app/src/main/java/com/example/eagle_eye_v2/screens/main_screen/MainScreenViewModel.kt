package com.example.eagle_eye_v2.screens.main_screen

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel

class MainScreenViewModel():ViewModel(){

  init {
    println("re Creating ")
  }
  var cnt = 0
  fun getName():Int{
    return cnt
  }
}