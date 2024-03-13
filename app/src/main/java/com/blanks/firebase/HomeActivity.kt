package com.blanks.firebase

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import com.blanks.firebase.ui.theme.Firebase_projectTheme
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

class HomeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Firebase_projectTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val user = Firebase.auth.currentUser

                    Log.i("USER : ", user?.email!!)

                    Greeting2(user.email!!)
                }
            }
        }
    }
}

@Composable
fun Greeting2(name: String, modifier: Modifier = Modifier) {
    val context = LocalContext.current

    Column {
        Text(
            text = "Hello $name!",
            modifier = modifier
        )
        Button(onClick = {
            Firebase.auth.signOut()
            context.startActivity(Intent(context,MainActivity::class.java))
        }) {
            Text(text = "Logout")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview2() {
    Firebase_projectTheme {
        Greeting2("Android")
    }
}