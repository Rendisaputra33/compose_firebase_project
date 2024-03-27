package com.blanks.firebase

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.blanks.firebase.data.LoadingState
import com.blanks.firebase.data.PublicUser
import com.blanks.firebase.ui.theme.Firebase_projectTheme
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore

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
    val store = Firebase.firestore
    val context = LocalContext.current

    var nameInput by remember { mutableStateOf("") }
    var adresss by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var loadingState by remember {
        mutableStateOf(LoadingState(isLoading = false))
    }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Hello $name",
            modifier = modifier
        )

        OutlinedTextField(
            value = nameInput,
            onValueChange = { nameInput = it },
            label = {
                Text(text = "Nama")
            })

        Spacer(modifier = modifier.padding(6.dp))

        OutlinedTextField(
            value = adresss,
            onValueChange = { adresss = it },
            label = {
                Text(text = "Alamat")
            })

        Spacer(modifier = modifier.padding(6.dp))

        OutlinedTextField(
            value = phoneNumber,
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
            onValueChange = { phoneNumber = it },
            label = {
                Text(text = "Nomor HP")
            })

        Spacer(modifier = modifier.padding(6.dp))

        Row(
            modifier = modifier
                .fillMaxWidth()
                .padding(horizontal = 55.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            Button(onClick = {
                loadingState = LoadingState(isLoading = true)

                val data = PublicUser(
                    name = nameInput,
                    address = adresss,
                    phoneNumber = phoneNumber
                )

                store.collection("public_users").add(data)
                    .addOnSuccessListener {
                        Log.i("INFO CRUD : ", "Sukses!")
                        loadingState = LoadingState(isLoading = false)
                        nameInput = ""
                        adresss = ""
                        phoneNumber = ""
                        Toast.makeText(context, "Berhasil menambah data!", Toast.LENGTH_LONG).show()

                    }
                    .addOnCanceledListener {
                        Toast.makeText(context, "Gagal menambah data!", Toast.LENGTH_LONG).show()
                    }
            }, modifier = modifier
                .fillMaxWidth(0.5f)
                .padding(3.dp)) {
                if (loadingState.isLoading) {
                    CircularProgressIndicator(color = Color.White, strokeWidth = 6.dp)
                } else {
                    Text(text = "Simpan")
                }
            }

            Button(onClick = {
                             context.startActivity(Intent(context,ShowActivity::class.java))
            }, modifier = modifier
                .fillMaxWidth()
                .padding(3.dp)) {
                Text(text = "Lihat")
            }
        }

        Button(onClick = {
            Firebase.auth.signOut()
            Toast.makeText(context, "Logout berhasil", Toast.LENGTH_LONG).show()
            context.startActivity(Intent(context,MainActivity::class.java))
        }, modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 55.dp)) {
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