package com.example.eagle_eye_v2.screens.main_screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.eagle_eye_v2.NavRoutes

@Composable
fun MainScreen(nc:NavHostController,
   vm:MainScreenViewModel = viewModel()
  )
{
  Column{
    Text(text = vm.javaClass.simpleName)
    Spacer(modifier = Modifier.height(20.dp))
    Button(onClick = {
      vm.cnt++
      onGoNextClick(nc)
    })
    {
      Text(text = " ${vm.getName()} ")
    }
  }
}

private fun onGoNextClick(nc:NavHostController) {

  nc.navigate(NavRoutes.DataCollection.route)
}
