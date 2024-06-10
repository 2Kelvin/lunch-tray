package com.example.lunchtray

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.lunchtray.datasource.DataSource
import com.example.lunchtray.ui.AccompanimentMenuScreen
import com.example.lunchtray.ui.CheckoutScreen
import com.example.lunchtray.ui.EntreeMenuScreen
import com.example.lunchtray.ui.OrderViewModel
import com.example.lunchtray.ui.SideDishMenuScreen
import com.example.lunchtray.ui.StartOrderScreen

//app screens
enum class LunchTrayScreens(@StringRes val title: Int) {
    START(title = R.string.start_order),
    ENTREE(title = R.string.choose_entree),
    SIDE_DISH(title = R.string.choose_side_dish),
    ACCOMPANIMENT(title = R.string.choose_accompaniment),
    CHECKOUT(title = R.string.order_checkout)
}

// CenterAlignedTopAppBar
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TheAppBar(
    currentScreen: LunchTrayScreens, // get the current screen & display its name
    canNavigateBack: Boolean, // on which screens to show the back button
    handleNavigateBack: () -> Unit, // backStackEntry screens functionality
    modifier: Modifier = Modifier
) {
    CenterAlignedTopAppBar(
        title = { Text(stringResource(currentScreen.title)) }, // show current screen title
        navigationIcon = {
            // showing the back button icon on all screens except the first one
            if (canNavigateBack) {
                IconButton(onClick = handleNavigateBack) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.back_button)
                    )
                }
            }
        },
        modifier = modifier
    )
}

@Composable
fun LunchTrayApp() {
    // navigation controller and initialization
    val navController: NavHostController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentScreen = LunchTrayScreens.valueOf(
        backStackEntry?.destination?.route ?: LunchTrayScreens.START.name
    )

    // the ViewModel
    val viewModel: OrderViewModel = viewModel()

    Scaffold(
        topBar = { // AppBar
            TheAppBar(
                currentScreen = currentScreen,
                // showing the back button on all screens except the start/first screen
                canNavigateBack = navController.previousBackStackEntry != null,
                handleNavigateBack = { navController.navigateUp() } // navigating back to previous screens
            )
        }
    ) { innerPadding ->
        val uiState by viewModel.uiState.collectAsState()

        // navigation host of all screens
        NavHost(
            navController = navController, // navigates through screens
            startDestination = LunchTrayScreens.START.name, // app's starting screen
        ) {

            // start screen
            composable(route = LunchTrayScreens.START.name) {
                // onClick should trigger navigation to entree page
                StartOrderScreen(
                    onStartOrderButtonClicked = { navController.navigate(LunchTrayScreens.ENTREE.name) },
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                )
            }

            // entree screen
            composable(route = LunchTrayScreens.ENTREE.name) {
                EntreeMenuScreen(
                    options = DataSource.entreeMenuItems,
                    onCancelButtonClicked = {
                        viewModel.resetOrder()
                        navController.popBackStack(LunchTrayScreens.START.name, inclusive = false)
                    },
                    onNextButtonClicked = { navController.navigate(LunchTrayScreens.SIDE_DISH.name) },
                    onSelectionChanged = { item ->
                        viewModel.updateEntree(item)
                    },
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                )
            }

            // side-dish screen
            composable(route = LunchTrayScreens.SIDE_DISH.name) {
                SideDishMenuScreen(
                    options = DataSource.sideDishMenuItems,
                    onCancelButtonClicked = {
                        viewModel.resetOrder()
                        navController.popBackStack(LunchTrayScreens.START.name, inclusive = false)
                    },
                    onNextButtonClicked = { navController.navigate(LunchTrayScreens.ACCOMPANIMENT.name) },
                    onSelectionChanged = { item ->
                        viewModel.updateSideDish(item)
                    },
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                )
            }

            // accompaniment screen
            composable(route = LunchTrayScreens.ACCOMPANIMENT.name) {
                AccompanimentMenuScreen(
                    options = DataSource.accompanimentMenuItems,
                    onCancelButtonClicked = {
                        viewModel.resetOrder()
                        navController.popBackStack(LunchTrayScreens.START.name, inclusive = false)
                    },
                    onNextButtonClicked = { navController.navigate(LunchTrayScreens.CHECKOUT.name) },
                    onSelectionChanged = { item ->
                        viewModel.updateAccompaniment(item)
                    },
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                )
            }

            // checkout screen
            composable(route = LunchTrayScreens.CHECKOUT.name) {
                CheckoutScreen(
                    orderUiState = uiState,
                    onCancelButtonClicked = {
                        viewModel.resetOrder()
                        navController.popBackStack(LunchTrayScreens.START.name, inclusive = false)
                    },
                    onNextButtonClicked = {
                        viewModel.resetOrder()
                        navController.popBackStack(LunchTrayScreens.START.name, inclusive = false)
                    },
                    modifier = Modifier
                        .verticalScroll(rememberScrollState())
                        .padding(
                            top = innerPadding.calculateTopPadding(),
                            bottom = innerPadding.calculateBottomPadding(),
                            start = dimensionResource(R.dimen.padding_medium),
                            end = dimensionResource(R.dimen.padding_medium),
                        )
                )
            }
        }
    }
}
