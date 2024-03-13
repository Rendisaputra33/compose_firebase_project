package com.blanks.firebase

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.blanks.firebase.data.LoadingState
import com.blanks.firebase.ui.theme.Firebase_projectTheme
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.Firebase
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        setContent {
            Firebase_projectTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Greeting("Android", onClick = {})
                }
            }
        }
    }
}


@Composable
fun Greeting(name: String, onClick:  () -> Unit, modifier: Modifier = Modifier) {
    var user by remember { mutableStateOf(Firebase.auth.currentUser) }
    var loadingState by remember {
        mutableStateOf(LoadingState(isLoading = false))
    }

    val context = LocalContext.current
    val token = stringResource(R.string.web_client_id)

    LaunchedEffect(key1 = user) {
        if (user != null) {
            context.startActivity(Intent(context, HomeActivity::class.java))
        }
    }

    val launcher = rememberFirebaseAuthLauncher(
        onAuthComplete = {
            context.startActivity(Intent(context, HomeActivity::class.java))
    }, onAuthError = {
        Toast.makeText(context, "Something when wrong!", Toast.LENGTH_LONG).show()
        user = null
    })

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.firebase),
            contentDescription = "",
            modifier = modifier
                .padding(bottom = 20.dp)
                .fillMaxWidth()
        )

        Button(
            onClick = {
                loadingState = LoadingState(isLoading = true)
            val client = getGoogleLoginAuth(ctx = context, token)
            launcher.launch(client.signInIntent) },
            modifier = modifier
                .fillMaxWidth()
                .padding(15.dp),

            ) {
            if (loadingState.isLoading) {
                CircularProgressIndicator(color = Color.White, strokeWidth = 6.dp)
            } else {
                Text(text = "Login with google")
            }
        }
    }
}

 fun getGoogleLoginAuth(ctx: Context, token: String): GoogleSignInClient {
    val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestEmail()
        .requestIdToken(token)
        .requestId()
        .requestProfile()
        .build()
    return GoogleSignIn.getClient(ctx, gso)
}


@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    Firebase_projectTheme {
        Greeting(name = "Login gengan google", onClick = { /*TODO*/ })
    }
}

@Composable
fun rememberFirebaseAuthLauncher(
    onAuthComplete: (AuthResult) -> Unit,
    onAuthError: (ApiException) -> Unit
): ManagedActivityResultLauncher<Intent, ActivityResult> {
    val scope = rememberCoroutineScope()
    return rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.getResult(ApiException::class.java)!!
            val credential = GoogleAuthProvider.getCredential(account.idToken!!, null)
            scope.launch {
                val authResult = Firebase.auth.signInWithCredential(credential).await()
                onAuthComplete(authResult)
            }
        } catch (e: ApiException) {
            onAuthError(e)
        }
    }
}
