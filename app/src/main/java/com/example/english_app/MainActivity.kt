package com.example.english_app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.english_app.ui.theme.ENGLISH_APPTheme
import com.example.english_app.ui.LoginScreen
import com.example.english_app.ui.SignUpScreen
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.english_app.ui.HomeScreen
import android.widget.Toast
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import androidx.navigation.NavType
import androidx.navigation.navArgument
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.english_app.ui.CarouselScreen
import com.example.english_app.ui.categories
import com.example.english_app.ui.DashboardScreen
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import com.example.english_app.ui.AnimationSettings
import android.content.Context
import com.example.english_app.ui.SettingsScreen
import com.example.english_app.ui.ContactScreen
import com.google.firebase.auth.FirebaseAuth

class MainActivity : ComponentActivity() {
    private lateinit var googleSignInLauncher: androidx.activity.result.ActivityResultLauncher<Intent>
    private lateinit var googleSignInClient: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Google Sign-In setup
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)

        // Register for activity result at the top level of onCreate
        googleSignInLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            val data: Intent? = result.data
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account: GoogleSignInAccount = task.getResult(ApiException::class.java)
                // Store the account info for navigation
                googleAccountInfoState.value = account
                Toast.makeText(this@MainActivity, "Google Sign-In successful! Welcome ${account.displayName}", Toast.LENGTH_LONG).show()
            } catch (e: ApiException) {
                Toast.makeText(this@MainActivity, "Google Sign-In failed: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }

        setContent {
            var darkTheme by remember { mutableStateOf(false) }
            var animationSettings by remember {
                mutableStateOf(AnimationSettings())
            }

            // Load animation settings from SharedPreferences
            LaunchedEffect(Unit) {
                val prefs = getSharedPreferences("animation_settings", Context.MODE_PRIVATE)
                animationSettings = AnimationSettings(
                    useRandomDirections = prefs.getBoolean("use_random_directions", true),
                    animationStyle = prefs.getInt("animation_style", 0)
                )
            }

            ENGLISH_APPTheme(darkTheme = darkTheme) {
                val navController = rememberNavController()
                val googleAccountInfo by googleAccountInfoState
                val firebaseUser = FirebaseAuth.getInstance().currentUser
                val isLoggedIn = googleAccountInfo != null || firebaseUser != null

                // Handle Google Sign-In result
                LaunchedEffect(googleAccountInfo) {
                    googleAccountInfo?.let { _ ->
                        navController.navigate("home") {
                            popUpTo("login") { inclusive = true }
                        }
                        googleAccountInfoState.value = null // Reset
                    }
                }

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = "login",
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable(
                            route = "login",
                            enterTransition = { slideInHorizontally { it } },
                            exitTransition = { slideOutHorizontally { -it } },
                            popEnterTransition = { slideInHorizontally { -it } },
                            popExitTransition = { slideOutHorizontally { it } }
                        ) {
                            LoginScreen(
                                onLoginClick = { _, _, _ ->
                                    navController.navigate("home") {
                                        popUpTo("login") { inclusive = true }
                                    }
                                },
                                onForgotPassword = {
                                    Toast.makeText(this@MainActivity, "Forgot Password clicked", Toast.LENGTH_SHORT).show()
                                },
                                onSignUp = {
                                    navController.navigate("signup")
                                },
                                onGoogleLogin = {
                                    try {
                                        val signInIntent = googleSignInClient.signInIntent
                                        googleSignInLauncher.launch(signInIntent)
                                    } catch (e: Exception) {
                                        Toast.makeText(this@MainActivity, "Error launching Google Sign-In: ${e.message}", Toast.LENGTH_LONG).show()
                                    }
                                },
                                onFacebookLogin = {
                                    // TODO: Implement Facebook Login
                                    navController.navigate("home") {
                                        popUpTo("login") { inclusive = true }
                                    }
                                }
                            )
                        }
                        composable(
                            route = "signup",
                            enterTransition = { slideInHorizontally { it } },
                            exitTransition = { slideOutHorizontally { -it } },
                            popEnterTransition = { slideInHorizontally { -it } },
                            popExitTransition = { slideOutHorizontally { it } }
                        ) {
                            SignUpScreen(
                                onSignUpClick = { _, _, _, _ ->
                                    navController.navigate("home") {
                                        popUpTo("login") { inclusive = true }
                                    }
                                },
                                onBackToLogin = {
                                    navController.navigateUp()
                                }
                            )
                        }
                        composable(
                            route = "carousel/{categoryIndex}",
                            arguments = listOf(
                                navArgument("categoryIndex") { type = NavType.IntType }
                            ),
                            enterTransition = { slideInHorizontally { it } },
                            exitTransition = { slideOutHorizontally { -it } },
                            popEnterTransition = { slideInHorizontally { -it } },
                            popExitTransition = { slideOutHorizontally { it } }
                        ) { backStackEntry ->
                            val categoryIndex = backStackEntry.arguments?.getInt("categoryIndex") ?: 0
                            CarouselScreen(
                                category = categories[categoryIndex],
                                onBack = { navController.popBackStack() },
                                onLogout = {
                                    navController.navigate("login") {
                                        popUpTo("login") { inclusive = true }
                                    }
                                },
                                animationSettings = animationSettings
                            )
                        }
                        composable(
                            route = "home",
                            enterTransition = { slideInHorizontally { it } },
                            exitTransition = { slideOutHorizontally { -it } },
                            popEnterTransition = { slideInHorizontally { -it } },
                            popExitTransition = { slideOutHorizontally { it } }
                        ) {
                            HomeScreen(
                                onCategorySelected = { category ->
                                    val idx = categories.indexOf(category)
                                    navController.navigate("carousel/$idx")
                                },
                                onDashboard = { navController.navigate("dashboard") },
                                onSettings = { navController.navigate("settings") },
                                onContact = { navController.navigate("contact") },
                                onHome = { navController.navigate("home") },
                                onLogin = { navController.navigate("login") },
                                onLogout = {
                                    navController.navigate("login") {
                                        popUpTo("home") { inclusive = true }
                                    }
                                },
                                darkTheme = darkTheme,
                                onToggleTheme = { darkTheme = !darkTheme },
                                userName = googleAccountInfo?.displayName ?: firebaseUser?.displayName ?: firebaseUser?.email?.substringBefore("@") ?: "User",
                                userPhotoUrl = googleAccountInfo?.photoUrl?.toString() ?: firebaseUser?.photoUrl?.toString(),
                                isLoggedIn = isLoggedIn
                            )
                        }
                        composable(
                            route = "dashboard",
                            enterTransition = { slideInHorizontally { it } },
                            exitTransition = { slideOutHorizontally { -it } },
                            popEnterTransition = { slideInHorizontally { -it } },
                            popExitTransition = { slideOutHorizontally { it } }
                        ) {
                            DashboardScreen(
                                userName = googleAccountInfo?.displayName ?: firebaseUser?.displayName ?: firebaseUser?.email?.substringBefore("@") ?: "User",
                                userEmail = googleAccountInfo?.email ?: firebaseUser?.email ?: "user@example.com",
                                userPhotoUrl = googleAccountInfo?.photoUrl?.toString() ?: firebaseUser?.photoUrl?.toString(),
                                onNavigateToHome = {
                                    navController.navigate("home")
                                },
                                onLogout = {
                                    navController.navigate("login") {
                                        popUpTo("login") { inclusive = true }
                                    }
                                },
                                onBack = { navController.popBackStack() },
                                onSettings = { navController.navigate("settings") },
                                onContact = { navController.navigate("contact") }
                            )
                        }
                        composable(
                            route = "settings",
                            enterTransition = { slideInHorizontally { it } },
                            exitTransition = { slideOutHorizontally { -it } },
                            popEnterTransition = { slideInHorizontally { -it } },
                            popExitTransition = { slideOutHorizontally { it } }
                        ) {
                            SettingsScreen(
                                onBack = { navController.popBackStack() },
                                darkTheme = darkTheme,
                                onToggleTheme = { darkTheme = !darkTheme },
                                animationSettings = animationSettings,
                                onAnimationSettingsChange = { newSettings ->
                                    animationSettings = newSettings
                                }
                            )
                        }
                        composable(
                            route = "contact",
                            enterTransition = { slideInHorizontally { it } },
                            exitTransition = { slideOutHorizontally { -it } },
                            popEnterTransition = { slideInHorizontally { -it } },
                            popExitTransition = { slideOutHorizontally { it } }
                        ) {
                            ContactScreen(onBack = { navController.popBackStack() })
                        }
                    }
                }
            }
        }
    }

    companion object {
        var googleAccountInfoState = mutableStateOf<GoogleSignInAccount?>(null)
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    ENGLISH_APPTheme {
        Greeting("Android")
    }
}