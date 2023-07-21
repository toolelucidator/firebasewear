/* While this template provides a good starting point for using Wear Compose, you can always
 * take a look at https://github.com/android/wear-os-samples/tree/main/ComposeStarter and
 * https://github.com/android/wear-os-samples/tree/main/ComposeAdvanced to find the most up to date
 * changes to the libraries and their usages.
 */

package com.example.firebasewear.presentation

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.ViewModel
import androidx.wear.compose.material.*
import com.example.firebasewear.R
import com.example.firebasewear.presentation.theme.FirebasewearTheme
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WearApp()
        }
    }
}

data class workoutData(val name: String = "", val reps: Int = 0)

class workOutviewmodel : ViewModel() {
    private val database = Firebase.database("https://fir-iot-488f2-default-rtdb.firebaseio.com")
    private var _workoutData = mutableStateOf<List<workoutData>>(emptyList())
    val workoutData: State<List<workoutData>> = _workoutData

    fun getData() {
        database.getReference("workout").addValueEventListener(
            object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    _workoutData.value = snapshot.getValue<List<workoutData>>()!!
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.w(TAG, "Error reading value")
                }

            }
        )
    }

    fun writeToDB(workout: workoutData, index: Int) {
        val database = Firebase.database("https://fir-iot-488f2-default-rtdb.firebaseio.com")
        val myRef = database.getReference("workout")
        listOf(workout).forEach() {
            myRef.child(index.toString()).setValue(it)
        }

    }
}

@Composable
fun workOutScreen(viewModel: workOutviewmodel) {
    viewModel.getData()
    val index = viewModel.workoutData.value.size
    ScalingLazyColumn() {
        items(viewModel.workoutData.value) { workout ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = workout.name,
                    //modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    color = Color.White
                )
                Text(
                    text = workout.reps.toString(),
                    //modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    color = Color.White
                )
            }

        }
        item {
            Button(onClick = { viewModel.writeToDB(workoutData("Trote ", 40), index) }) {
                Text("Add to Firebase")
            }
        }
    }
}


@Composable
fun WearApp() {
    FirebasewearTheme {
        workOutScreen(workOutviewmodel())
    }
}

