package kr.ac.kumoh.ce.s20200285.sap_tp_20200285

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.launch
import kr.ac.kumoh.ce.s20200285.sap_tp_20200285.ui.ArtistScreen
import kr.ac.kumoh.ce.s20200285.sap_tp_20200285.ui.ScheduleScreen
import kr.ac.kumoh.ce.s20200285.sap_tp_20200285.ui.Screen
import kr.ac.kumoh.ce.s20200285.sap_tp_20200285.ui.theme.SAP_TP_20200285Theme
import kr.ac.kumoh.ce.s20200285.sap_tp_20200285.viewmodel.ConcertViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SAP_TP_20200285Theme {
                MainScreen()
            }
        }
    }
}

@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val viewModel: ConcertViewModel = viewModel()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            DrawerSheet(drawerState) {
                navController.navigate(it) {
                    launchSingleTop = true
                    popUpTo(it) { inclusive = true }
                }
            }
        },
        gesturesEnabled = true,
    ) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = { TopBar(drawerState, navController) },
            bottomBar = {
                BottomNavigationBar {
                    navController.navigate(it) {
                        launchSingleTop = true
                        popUpTo(it) { inclusive = true }
                    }
                }
            },
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = Screen.Schedule.name,
                modifier = Modifier.padding(innerPadding)
            ) {
                composable(route = Screen.Schedule.name) {
                    ScheduleScreen(viewModel, isFavorite = false)
                }
                composable(route = Screen.Favorite.name) {
                    ScheduleScreen(viewModel, isFavorite = true)
                }
                composable(route = Screen.Artist.name) {
                    ArtistScreen(viewModel)
                }
            }
        }
    }
}

@Composable
fun DrawerSheet(
    drawerState: DrawerState,
    onNavigate: (String) -> Unit,
) {
    val scope = rememberCoroutineScope()

    ModalDrawerSheet {
        NavigationDrawerItem(label = { Text("공연 리스트") }, selected = false, onClick = {
            onNavigate(Screen.Schedule.name)
            scope.launch {
                drawerState.close()
            }
        }, icon = {
            Icon(
                Icons.Filled.DateRange, contentDescription = "공연 리스트 아이콘"
            )
        })
        NavigationDrawerItem(label = { Text("아티스트 리스트") }, selected = false, onClick = {
            onNavigate(Screen.Artist.name)
            scope.launch {
                drawerState.close()
            }
        }, icon = {
            Icon(
                Icons.Filled.AccountCircle, contentDescription = "아티스트 리스트 아이콘"
            )
        })
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(drawerState: DrawerState, navController: NavController) {
    val scope = rememberCoroutineScope()

    CenterAlignedTopAppBar(
        title = {
            Text(
                when (navController.currentBackStackEntryAsState().value?.destination?.route) {
                    Screen.Schedule.name -> "공연"
                    Screen.Artist.name -> "아티스트"
                    Screen.Favorite.name -> "즐겨찾기"
                    else -> ""
                }
            )
        },
        navigationIcon = {
            IconButton(onClick = {
                scope.launch {
                    drawerState.open()
                }
            }) {
                Icon(
                    imageVector = Icons.Default.Menu, contentDescription = "메뉴 아이콘"
                )
            }
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = MaterialTheme.colorScheme.primary,
        ),
    )
}

@Composable
fun BottomNavigationBar(onNavigate: (String) -> Unit) {
    NavigationBar {
        NavigationBarItem(label = {
            Text("공연")
        }, icon = {
            Icon(
                Icons.Filled.DateRange, contentDescription = "공연 리스트 아이콘"
            )
        }, selected = false, onClick = {
            onNavigate(Screen.Schedule.name)
        })
        NavigationBarItem(label = {
            Text("아티스트")
        }, icon = {
            Icon(
                Icons.Filled.AccountCircle, contentDescription = "아티스트 리스트 아이콘"
            )
        }, selected = false, onClick = {
            onNavigate(Screen.Artist.name)
        })
        NavigationBarItem(label = {
            Text("즐겨찾기")
        }, icon = {
            Icon(
                Icons.Filled.Favorite, contentDescription = "즐겨찾기 아이콘"
            )
        }, selected = false, onClick = {
            onNavigate(Screen.Favorite.name)
        })
    }
}

