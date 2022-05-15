package com.example.eagle_eye_v2.screens.data_collection_screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController

@Composable
fun DataCollectionScreen(nc:NavHostController) {
  Column{
    Text(text = " Data Collection Screen ")
    Spacer(modifier = Modifier.height(20.dp))
    Button(onClick = { onGoNextClick(nc) })
    {
      Text(text = " go Back ")
    }
  }
}

private fun onGoNextClick(nc:NavHostController) {
  nc.navigateUp()
}
