package com.blanks.firebase

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.blanks.firebase.data.LoadingState
import com.blanks.firebase.data.PublicUser
import com.blanks.firebase.ui.theme.Firebase_projectTheme
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore

class ShowActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Firebase_projectTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Greeting3("Android")
                }
            }
        }
    }
}

@Composable
fun Greeting3(name: String, modifier: Modifier = Modifier) {
    val store = Firebase.firestore

    var loadingState by remember {
        mutableStateOf(LoadingState(isLoading = true))
    }

    var publicUsers by remember {
        mutableStateOf<List<PublicUser>>(listOf())
    }

    LaunchedEffect(key1 = Unit) {
        store.collection("public_users")
            .get()
            .addOnSuccessListener {
                  publicUsers = it.map {
                           PublicUser(
                               name = it.data.get("name").toString(),
                               address = it.data.get("address").toString(),
                               phoneNumber = it.data.get("phoneNumber").toString()
                           )
                   }
                    loadingState = LoadingState(isLoading = false)
            }
    }

    if (loadingState.isLoading) {
        Column(
            modifier = modifier.fillMaxWidth().fillMaxHeight(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {

        }
        CircularProgressIndicator(
            color = Color.Magenta,
            modifier = modifier.size(30.dp),
            strokeWidth = 5.dp
        )
    } else {
        LazyColumn {
            items(publicUsers) { item ->
                Card(
                    modifier = modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                ) {
                    Row(
                        modifier = modifier.fillMaxWidth()
                    ) {
                        Image(painter = painterResource(id = R.drawable.ic_launcher_background), contentDescription = "")
                        Column {
                            Text(text = "Name : ${item.name}", modifier = modifier.padding(10.dp))
                            Text(text = "Address : ${item.address}", modifier = modifier.padding(10.dp))
                            Text(text = "Phone : ${item.phoneNumber}", modifier = modifier.padding(10.dp))
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview3() {
    Firebase_projectTheme {
        Greeting3("Android")
    }
}