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
<<<<<<< HEAD
=======
import com.example.english_app.ui.LeaderboardScreen
>>>>>>> 73d420b5c198105f2a9f3f976511c9aad67dfa69
import com.example.english_app.ui.QuizHubScreen
import com.example.english_app.ui.QuizScreen
import com.example.english_app.ui.PilotTestScreen
import com.example.english_app.ui.AdminPanelScreen
<<<<<<< HEAD
=======
import com.example.english_app.ui.OnboardingScreen
>>>>>>> 73d420b5c198105f2a9f3f976511c9aad67dfa69
import com.google.firebase.auth.FirebaseAuth
import androidx.compose.ui.graphics.Color
import androidx.compose.runtime.mutableFloatStateOf
import com.example.english_app.data.UserProgressRepository
import com.example.english_app.data.UserProfile

<<<<<<< HEAD
=======
@androidx.compose.material3.ExperimentalMaterial3Api
>>>>>>> 73d420b5c198105f2a9f3f976511c9aad67dfa69
class MainActivity : ComponentActivity() {
    private lateinit var googleSignInLauncher: androidx.activity.result.ActivityResultLauncher<Intent>
    private lateinit var googleSignInClient: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

<<<<<<< HEAD
        val webClientId = "331963078820-iiji6rt1dhi8d2i84komd88483g3cqqh.apps.googleusercontent.com"
=======
        val webClientId = "601582889258-bugbjcask3vh9igir6k089jcl1cg2v4s.apps.googleusercontent.com"
>>>>>>> 73d420b5c198105f2a9f3f976511c9aad67dfa69

        // Google Sign-In setup — request idToken so we can sign into Firebase Auth
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(webClientId)
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
                val accountEmail = account.email ?: ""

                // Domain restriction check
                if (!accountEmail.lowercase().endsWith(ALLOWED_EMAIL_DOMAIN)) {
                    // Sign out from Google so user can pick a different account next time
                    googleSignInClient.signOut()
                    Toast.makeText(
                        this@MainActivity,
                        "Only $ALLOWED_EMAIL_DOMAIN emails are allowed. Please use your college email.",
                        Toast.LENGTH_LONG
                    ).show()
                    return@registerForActivityResult
                }

                // Sign into Firebase Auth with the Google credential
                val credential = com.google.firebase.auth.GoogleAuthProvider.getCredential(account.idToken, null)
                FirebaseAuth.getInstance().signInWithCredential(credential)
                    .addOnSuccessListener {
                        googleAccountInfoState.value = account
                        Toast.makeText(this@MainActivity, "Google Sign-In successful! Welcome ${account.displayName}", Toast.LENGTH_LONG).show()
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this@MainActivity, "Firebase auth failed: ${e.message}", Toast.LENGTH_LONG).show()
                    }
            } catch (e: ApiException) {
                Toast.makeText(this@MainActivity, "Google Sign-In failed: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }

        setContent {
            var darkTheme by remember { mutableStateOf(false) }
            var animationSettings by remember {
                mutableStateOf(AnimationSettings())
            }
            var fontSizeScale by remember { mutableFloatStateOf(1.0f) }
            var userProfile by remember { mutableStateOf(UserProfile()) }

            // Load animation settings from SharedPreferences
            LaunchedEffect(Unit) {
                val prefs = getSharedPreferences("animation_settings", Context.MODE_PRIVATE)
                animationSettings = AnimationSettings(
                    useRandomDirections = prefs.getBoolean("use_random_directions", true),
                    animationStyle = prefs.getInt("animation_style", 0)
                )
                fontSizeScale = prefs.getFloat("font_size_scale", 1.0f)
            }

            ENGLISH_APPTheme(
                darkTheme = darkTheme,
                fontScale = fontSizeScale
            ) {
                val navController = rememberNavController()
                val googleAccountInfo by googleAccountInfoState
                var firebaseUser by remember { mutableStateOf(FirebaseAuth.getInstance().currentUser) }
                
                LaunchedEffect(Unit) {
                    FirebaseAuth.getInstance().addAuthStateListener { auth ->
                        firebaseUser = auth.currentUser
                    }
                }
                
                val isLoggedIn = googleAccountInfo != null || firebaseUser != null

<<<<<<< HEAD
                // Load user profile when logged in
                LaunchedEffect(isLoggedIn) {
                    if (isLoggedIn) {
                        UserProgressRepository.loadUserProfile { profile ->
                            userProfile = profile
                        }
=======
                val prefsApp = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
                var hasSeenOnboarding by remember { mutableStateOf(prefsApp.getBoolean("has_seen_onboarding", false)) }
                val startRoute = if (hasSeenOnboarding) "login" else "onboarding"

                // Load user profile when logged in or clear it when logged out
                LaunchedEffect(firebaseUser?.uid) {
                    if (firebaseUser != null) {
                        UserProgressRepository.loadUserProfile { profile ->
                            userProfile = profile
                        }
                    } else {
                        userProfile = UserProfile()
                        googleAccountInfoState.value = null
>>>>>>> 73d420b5c198105f2a9f3f976511c9aad67dfa69
                    }
                }

                // Handle Google Sign-In result
                LaunchedEffect(googleAccountInfo) {
                    googleAccountInfo?.let { _ ->
                        navController.navigate("home") {
                            popUpTo("login") { inclusive = true }
<<<<<<< HEAD
=======
                            popUpTo("onboarding") { inclusive = true }
>>>>>>> 73d420b5c198105f2a9f3f976511c9aad67dfa69
                        }
                        googleAccountInfoState.value = null // Reset
                    }
                }

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    containerColor = Color.Transparent
                ) { innerPadding ->
                    NavHost(
                        navController = navController,
<<<<<<< HEAD
                        startDestination = "login",
                        modifier = Modifier
                    ) {
                        composable(
=======
                        startDestination = startRoute,
                        modifier = Modifier
                    ) {
                        composable(
                            route = "onboarding",
                            enterTransition = { slideInHorizontally { it } },
                            exitTransition = { slideOutHorizontally { -it } },
                            popEnterTransition = { slideInHorizontally { -it } },
                            popExitTransition = { slideOutHorizontally { it } }
                        ) {
                            OnboardingScreen(
                                onFinish = {
                                    prefsApp.edit().putBoolean("has_seen_onboarding", true).apply()
                                    hasSeenOnboarding = true
                                    navController.navigate("login") {
                                        popUpTo("onboarding") { inclusive = true }
                                    }
                                }
                            )
                        }
                        composable(
>>>>>>> 73d420b5c198105f2a9f3f976511c9aad67dfa69
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
                                        // Sign out first so user can pick account
                                        googleSignInClient.signOut().addOnCompleteListener {
                                            val signInIntent = googleSignInClient.signInIntent
                                            googleSignInLauncher.launch(signInIntent)
                                        }
                                    } catch (e: Exception) {
                                        Toast.makeText(this@MainActivity, "Error launching Google Sign-In: ${e.message}", Toast.LENGTH_LONG).show()
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
                                },
                                onGoogleSignUp = {
                                    try {
                                        // Sign out first so user can pick account
                                        googleSignInClient.signOut().addOnCompleteListener {
                                            val signInIntent = googleSignInClient.signInIntent
                                            googleSignInLauncher.launch(signInIntent)
                                        }
                                    } catch (e: Exception) {
                                        Toast.makeText(this@MainActivity, "Error launching Google Sign-In: ${e.message}", Toast.LENGTH_LONG).show()
                                    }
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
<<<<<<< HEAD
=======
                                    FirebaseAuth.getInstance().signOut()
                                    googleSignInClient.signOut()
>>>>>>> 73d420b5c198105f2a9f3f976511c9aad67dfa69
                                    navController.navigate("login") {
                                        popUpTo("login") { inclusive = true }
                                    }
                                },
                                animationSettings = animationSettings,
                                onStartQuiz = { categoryId ->
                                    navController.navigate("quiz/$categoryId")
                                }
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
                                onQuiz = { navController.navigate("quizHub") },
                                onLogout = {
                                    FirebaseAuth.getInstance().signOut()
                                    googleSignInClient.signOut()
                                    googleAccountInfoState.value = null
                                    userProfile = UserProfile()
                                    navController.navigate("login") {
                                        popUpTo("home") { inclusive = true }
                                    }
                                },
                                onAdminPanel = { navController.navigate("adminPanel") },
                                darkTheme = darkTheme,
                                onToggleTheme = { darkTheme = !darkTheme },
                                userName = googleAccountInfo?.displayName ?: firebaseUser?.displayName ?: firebaseUser?.email?.substringBefore("@") ?: "User",
                                userPhotoUrl = googleAccountInfo?.photoUrl?.toString() ?: firebaseUser?.photoUrl?.toString(),
                                isLoggedIn = isLoggedIn,
<<<<<<< HEAD
                                isAdmin = userProfile.role == "admin"
=======
                                isAdmin = userProfile.role == "admin" || userProfile.role == "teacher"
>>>>>>> 73d420b5c198105f2a9f3f976511c9aad67dfa69
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
                                    FirebaseAuth.getInstance().signOut()
                                    googleSignInClient.signOut()
                                    navController.navigate("login") {
                                        popUpTo("login") { inclusive = true }
                                    }
                                },
                                onBack = { navController.popBackStack() },
                                onSettings = { navController.navigate("settings") },
<<<<<<< HEAD
                                onContact = { navController.navigate("contact") }
=======
                                onContact = { navController.navigate("contact") },
                                onLeaderboard = { navController.navigate("leaderboard") }
>>>>>>> 73d420b5c198105f2a9f3f976511c9aad67dfa69
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
                                },
                                userName = googleAccountInfo?.displayName ?: firebaseUser?.displayName ?: firebaseUser?.email?.substringBefore("@") ?: "User",
                                userEmail = googleAccountInfo?.email ?: firebaseUser?.email ?: "",
                                userPhotoUrl = googleAccountInfo?.photoUrl?.toString() ?: firebaseUser?.photoUrl?.toString(),
                                onSignOut = {
                                    FirebaseAuth.getInstance().signOut()
                                    googleSignInClient.signOut()
                                    googleAccountInfoState.value = null
                                    userProfile = UserProfile()
                                    navController.navigate("login") {
                                        popUpTo("home") { inclusive = true }
                                    }
                                },
                                fontSizeScale = fontSizeScale,
                                onFontSizeChange = { newScale ->
                                    fontSizeScale = newScale
                                    val prefs = getSharedPreferences("animation_settings", Context.MODE_PRIVATE)
                                    prefs.edit().putFloat("font_size_scale", newScale).apply()
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
                        composable(
                            route = "quizHub",
                            enterTransition = { slideInHorizontally { it } },
                            exitTransition = { slideOutHorizontally { -it } },
                            popEnterTransition = { slideInHorizontally { -it } },
                            popExitTransition = { slideOutHorizontally { it } }
                        ) {
                            QuizHubScreen(
                                onBack = { navController.popBackStack() },
                                onCategorySelected = { category ->
                                    navController.navigate("quiz/${category.id}")
                                },
                                onPilotTest = {
                                    navController.navigate("pilotTest")
                                }
                            )
                        }
                        composable(
                            route = "pilotTest",
                            enterTransition = { slideInHorizontally { it } },
                            exitTransition = { slideOutHorizontally { -it } },
                            popEnterTransition = { slideInHorizontally { -it } },
                            popExitTransition = { slideOutHorizontally { it } }
                        ) {
                            PilotTestScreen(onBack = { navController.popBackStack() })
                        }
                        composable(
                            route = "quiz/{categoryId}",
                            arguments = listOf(
                                navArgument("categoryId") { type = NavType.StringType }
                            ),
                            enterTransition = { slideInHorizontally { it } },
                            exitTransition = { slideOutHorizontally { -it } },
                            popEnterTransition = { slideInHorizontally { -it } },
                            popExitTransition = { slideOutHorizontally { it } }
                        ) { backStackEntry ->
                            val categoryId = backStackEntry.arguments?.getString("categoryId") ?: ""
                            val quizCategory = categories.find { it.id == categoryId } ?: categories.first()
                            QuizScreen(
                                category = quizCategory,
                                onBack = { navController.popBackStack() }
                            )
                        }
                        composable(
                            route = "adminPanel",
                            enterTransition = { slideInHorizontally { it } },
                            exitTransition = { slideOutHorizontally { -it } },
                            popEnterTransition = { slideInHorizontally { -it } },
                            popExitTransition = { slideOutHorizontally { it } }
                        ) {
                            AdminPanelScreen(
                                onBack = { navController.popBackStack() },
<<<<<<< HEAD
                                teacherDepartment = userProfile.department
=======
                                teacherDepartment = userProfile.department,
                                isAdmin = userProfile.role == "admin" || userProfile.role == "teacher"
                            )
                        }
                        composable(
                            route = "leaderboard",
                            enterTransition = { slideInHorizontally { it } },
                            exitTransition = { slideOutHorizontally { -it } },
                            popEnterTransition = { slideInHorizontally { -it } },
                            popExitTransition = { slideOutHorizontally { it } }
                        ) {
                            LeaderboardScreen(
                                onBack = { navController.popBackStack() },
                                userDepartment = userProfile.department
>>>>>>> 73d420b5c198105f2a9f3f976511c9aad67dfa69
                            )
                        }
                    }
                }
            }
        }
    }

    companion object {
        var googleAccountInfoState = mutableStateOf<GoogleSignInAccount?>(null)
        private const val ALLOWED_EMAIL_DOMAIN = "@srcas.ac.in"
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