package com.example.lunchtray

import androidx.annotation.StringRes
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.lunchtray.ui.OrderViewModel

//app screens
enum class LunchTrayAppScreen(@StringRes val title: Int) {
    START(title = R.string.start_order),
    ENTREE(title = R.string.choose_entree),
    SIDE_DISH(title = R.string.choose_side_dish),
    ACCOMPANIMENT(title = R.string.choose_accompaniment),
    CHECKOUT(title = R.string.order_checkout)
}

// TODO: AppBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LunchTrayApp() {
    // TODO: Create Controller and initialization

    // Create ViewModel
    val viewModel: OrderViewModel = viewModel()

    Scaffold(
        topBar = {
            // TODO: AppBar
        }
    ) { innerPadding ->
        val uiState by viewModel.uiState.collectAsState()

        // TODO: Navigation host
    }
}
