package com.blanks.firebase

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
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
import androidx.compose.runtime.LaunchedEffect
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

class EditScreen : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Firebase_projectTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Greeting4()
                }
            }
        }
    }
}

@Composable
fun Greeting4(modifier: Modifier = Modifier) {
    val store = Firebase.firestore
    val context = LocalContext.current

    var nameInput by remember { mutableStateOf("") }
    var adresss by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var documentID by remember {
        mutableStateOf("")
    }
    var loadingState by remember {
        mutableStateOf(LoadingState(isLoading = false))
    }

    LaunchedEffect(key1 = Unit) {
        val activity = context.findActivity()
        val intent = activity?.intent

        val id = intent?.getStringExtra("doc_id")

        if (id != null) {
            documentID = id
            store.collection("public_users")
                .document(id)
                .get()
                .addOnSuccessListener {
                    nameInput = it.get("name").toString()
                    adresss = it.get("address").toString()
                    phoneNumber = it.get("phoneNumber").toString()
                }
        }
    }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Edit Data",
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

                store.collection("public_users").document(documentID)
                    .set(data)
                    .addOnSuccessListener {
                        loadingState = LoadingState(isLoading = false)
                        nameInput = ""
                        adresss = ""
                        phoneNumber = ""
                        Toast.makeText(context, "Berhasil mengubah data!", Toast.LENGTH_LONG).show()
                        context.startActivity(Intent(context, ShowActivity::class.java))
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
                context.startActivity(Intent(context,HomeActivity::class.java))
            }, modifier = modifier
                .fillMaxWidth()
                .padding(3.dp)) {
                Text(text = "Kembali")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview4() {
    Firebase_projectTheme {
        Greeting4()
    }
}

fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}