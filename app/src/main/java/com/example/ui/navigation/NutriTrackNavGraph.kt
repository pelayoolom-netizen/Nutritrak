package com.example.ui.navigation

import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material.icons.outlined.Book
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.core.content.FileProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.data.local.entity.FavoriteFoodEntity
import com.example.data.model.FoodItem
import com.example.data.model.MealType
import com.example.ui.components.AddFoodBottomSheet
import com.example.ui.components.EditFoodItemDialog
import com.example.ui.components.LiquidGlassBottomBar
import com.example.ui.components.ManualFoodEntryDialog
import com.example.ui.screens.camera.AiFoodAnalysisScreen
import com.example.ui.screens.diary.DiaryScreen
import com.example.ui.screens.home.HomeScreen
import com.example.ui.screens.onboarding.OnboardingScreen
import com.example.ui.screens.profile.ProfileScreen
import com.example.ui.screens.progress.ProgressScreen
import com.example.ui.viewmodel.AiAnalysisUiState
import com.example.ui.viewmodel.NutriTrackViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

sealed class Screen(val route: String, val title: String, val selectedIcon: ImageVector, val unselectedIcon: ImageVector) {
    object Home : Screen("home", "Inicio", Icons.Filled.Home, Icons.Outlined.Home)
    object Diary : Screen("diary", "Diario", Icons.Filled.Book, Icons.Outlined.Book)
    object Progress : Screen("progress", "Progreso", Icons.Filled.BarChart, Icons.Outlined.BarChart)
    object Profile : Screen("profile", "Perfil", Icons.Filled.Person, Icons.Outlined.Person)
}

@Composable
fun NutriTrackNavGraph(
    viewModel: NutriTrackViewModel,
    modifier: Modifier = Modifier
) {
    val isOnboardingCompleted by viewModel.isOnboardingCompleted.collectAsStateWithLifecycle()
    val aiAnalysisState by viewModel.aiAnalysisState.collectAsStateWithLifecycle()
    val recentFoods by viewModel.recentFoods.collectAsStateWithLifecycle()
    val favoriteFoods by viewModel.favoriteFoods.collectAsStateWithLifecycle()

    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    // Modals & BottomSheets state
    var showAddFoodSheet by remember { mutableStateOf(false) }
    var targetMealTypeForAdd by remember { mutableStateOf(MealType.LUNCH) }
    var showManualEntryDialog by remember { mutableStateOf(false) }
    var editingFoodItem by remember { mutableStateOf<FoodItem?>(null) }
    var tempCameraImageUri by remember { mutableStateOf<Uri?>(null) }

    // Camera launcher
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success && tempCameraImageUri != null) {
            coroutineScope.launch(Dispatchers.IO) {
                try {
                    val uri = tempCameraImageUri ?: return@launch
                    val bitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                        ImageDecoder.decodeBitmap(ImageDecoder.createSource(context.contentResolver, uri))
                    } else {
                        @Suppress("DEPRECATION")
                        MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
                    }
                    withContext(Dispatchers.Main) {
                        viewModel.analyzePhoto(bitmap, uri.toString())
                    }
                } catch (e: Exception) {
                    // Fallback
                    withContext(Dispatchers.Main) {
                        viewModel.analyzePhoto(Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888))
                    }
                }
            }
        }
    }

    // Photo Picker launcher
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            coroutineScope.launch(Dispatchers.IO) {
                try {
                    val bitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                        ImageDecoder.decodeBitmap(ImageDecoder.createSource(context.contentResolver, uri))
                    } else {
                        @Suppress("DEPRECATION")
                        MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
                    }
                    withContext(Dispatchers.Main) {
                        viewModel.analyzePhoto(bitmap, uri.toString())
                    }
                } catch (e: Exception) {
                    // Fallback
                    withContext(Dispatchers.Main) {
                        viewModel.analyzePhoto(Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888))
                    }
                }
            }
        }
    }

    fun launchCamera() {
        try {
            val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val imageFile = File.createTempFile("MEAL_${timeStamp}_", ".jpg", context.cacheDir)
            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                imageFile
            )
            tempCameraImageUri = uri
            cameraLauncher.launch(uri)
        } catch (e: Exception) {
            // If camera cannot create file, trigger demo analyze
            viewModel.analyzePhoto(Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888))
        }
    }

    fun launchGallery() {
        photoPickerLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    if (!isOnboardingCompleted) {
        OnboardingScreen(
            onFinish = { goals, initialWeight ->
                viewModel.completeOnboarding(goals)
                if (initialWeight != null && initialWeight > 0) {
                    viewModel.logWeight(initialWeight, "Starting baseline")
                }
            }
        )
        return
    }

    // If AI Analysis is active (Loading, Review, Error), show full screen
    if (aiAnalysisState !is AiAnalysisUiState.Idle) {
        AiFoodAnalysisScreen(
            state = aiAnalysisState,
            initialMealType = targetMealTypeForAdd,
            onBack = { viewModel.clearAiAnalysis() },
            onSaveMeal = { mealType -> viewModel.saveAiMeal(mealType) },
            onUpdateItem = { index, food -> viewModel.updateAiFoodItem(index, food) },
            onDeleteItem = { index -> viewModel.removeAiFoodItem(index) },
            onDuplicateItem = { index -> viewModel.duplicateAiFoodItem(index) },
            onAddItem = { food -> viewModel.addAiFoodItem(food) },
            onRetry = {
                val current = aiAnalysisState as? AiAnalysisUiState.Review
                if (current?.photoBitmap != null) {
                    viewModel.analyzePhoto(current.photoBitmap, current.photoUri)
                } else {
                    viewModel.clearAiAnalysis()
                }
            },
            onAddManuallyInstead = {
                viewModel.clearAiAnalysis()
                showManualEntryDialog = true
            }
        )
        return
    }

    val bottomNavScreens = listOf(
        Screen.Home,
        Screen.Diary,
        Screen.Progress,
        Screen.Profile
    )

    Scaffold(
        bottomBar = {
            LiquidGlassBottomBar(
                screens = bottomNavScreens,
                currentRoute = currentRoute,
                onNavigate = { screen ->
                    if (currentRoute != screen.route) {
                        navController.navigate(screen.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                }
            )
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            composable(Screen.Home.route) {
                HomeScreen(
                    viewModel = viewModel,
                    onOpenAddFood = { mealType ->
                        targetMealTypeForAdd = mealType
                        showAddFoodSheet = true
                    },
                    onEditFoodItem = { foodItem ->
                        editingFoodItem = foodItem
                    },
                    onOpenAiCamera = {
                        targetMealTypeForAdd = MealType.LUNCH
                        launchCamera()
                    }
                )
            }

            composable(Screen.Diary.route) {
                DiaryScreen(
                    viewModel = viewModel,
                    onOpenAddFood = { mealType ->
                        targetMealTypeForAdd = mealType
                        showAddFoodSheet = true
                    },
                    onEditFoodItem = { foodItem ->
                        editingFoodItem = foodItem
                    }
                )
            }

            composable(Screen.Progress.route) {
                ProgressScreen(viewModel = viewModel)
            }

            composable(Screen.Profile.route) {
                ProfileScreen(viewModel = viewModel)
            }
        }
    }

    // Add Food Bottom Sheet
    if (showAddFoodSheet) {
        AddFoodBottomSheet(
            targetMealType = targetMealTypeForAdd,
            recentFoods = recentFoods,
            favoriteFoods = favoriteFoods,
            onDismiss = { showAddFoodSheet = false },
            onTakePhoto = { launchCamera() },
            onChoosePhoto = { launchGallery() },
            onManualEntry = { showManualEntryDialog = true },
            onQuickAddFood = { favFood ->
                val foodItem = FoodItem(
                    date = viewModel.selectedDate.value,
                    mealType = targetMealTypeForAdd,
                    name = favFood.name,
                    quantity = favFood.defaultQuantity,
                    unit = favFood.unit,
                    calories = favFood.calories,
                    protein = favFood.protein,
                    carbs = favFood.carbs,
                    fat = favFood.fat,
                    isAiEstimated = false
                )
                viewModel.logFoodItem(foodItem)
            },
            onToggleFavorite = { favFood ->
                viewModel.toggleFavorite(favFood)
            }
        )
    }

    // Manual Food Entry Dialog
    if (showManualEntryDialog) {
        ManualFoodEntryDialog(
            initialMealType = targetMealTypeForAdd,
            onDismiss = { showManualEntryDialog = false },
            onSave = { item, saveAsFavorite ->
                val withDate = item.copy(date = viewModel.selectedDate.value)
                viewModel.logFoodItem(withDate)
                if (saveAsFavorite) {
                    coroutineScope.launch {
                        viewModel.repository.addCustomFavorite(
                            FavoriteFoodEntity(
                                name = item.name,
                                defaultQuantity = item.quantity,
                                unit = item.unit,
                                calories = item.calories,
                                protein = item.protein,
                                carbs = item.carbs,
                                fat = item.fat,
                                isFavorite = true
                            )
                        )
                    }
                }
                showManualEntryDialog = false
            }
        )
    }

    // Edit Existing Food Item Dialog
    editingFoodItem?.let { item ->
        EditFoodItemDialog(
            initialItem = item,
            onDismiss = { editingFoodItem = null },
            onConfirm = { updated ->
                viewModel.updateFoodItem(updated)
                editingFoodItem = null
            }
        )
    }
}
