package com.vipulasri.kachetor.sample.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.vipulasri.kachetor.sample.shared.SpacexApi
import com.vipulasri.kachetor.sample.shared.model.Rocket
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private val spacexApi by lazy { SpacexApi() }

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ApplicationTheme {
                Scaffold(
                    topBar = {
                        TopAppBar(title = { Text(text = stringResource(id = R.string.app_name)) })
                    }
                ) { padding ->
                    Surface(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 20.dp)
                            .padding(padding),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        RocketsView(spacexApi)
                    }
                }
            }
        }
    }
}

@Composable
private fun RocketsView(spacexApi: SpacexApi) {
    val coroutineScope = rememberCoroutineScope()
    var rockets by remember { mutableStateOf<List<Rocket>?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    LaunchedEffect(key1 = "initial") {
        isLoading = true
        rockets = spacexApi.getRockets()
        isLoading = false
    }

    RocketList(
        isLoading = isLoading,
        rockets = rockets,
        onLoadClick = {
            coroutineScope.launch {
                isLoading = true
                rockets = spacexApi.getRockets()
                isLoading = false
            }
        }
    )
}

@Composable
private fun RocketList(
    isLoading: Boolean,
    rockets: List<Rocket>?,
    onLoadClick: () -> Unit = { }
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp)
    ) {
        when {
            isLoading -> CircularProgressIndicator(
                modifier = Modifier
                    .padding(20.dp)
                    .align(Alignment.CenterHorizontally),
            )

            rockets == null -> {
                Text(
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    text = "Error Fetching data!"
                )
                Spacer(modifier = Modifier.height(10.dp))
                ReloadButton(
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    onLoadClick = onLoadClick
                )
            }

            rockets.isEmpty() -> {
                Text(
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    text = "No rockets available!"
                )
                Spacer(modifier = Modifier.height(10.dp))
                ReloadButton(
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    onLoadClick = onLoadClick
                )
            }

            else -> {
                RowItem(
                    first = "Rocket Name",
                    second = "First Flight",
                    third = "Status",
                    isHeader = true
                )
                Spacer(modifier = Modifier.height(10.dp))
                rockets.forEach { rocket ->
                    RowItem(
                        first = rocket.name,
                        second = rocket.firstFlight,
                        third = if (rocket.active) "Active" else "Inactive"
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                }
                Spacer(modifier = Modifier.height(10.dp))
                ReloadButton(
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    onLoadClick = onLoadClick
                )
            }
        }
    }
}

@Composable
private fun RowItem(
    first: String,
    second: String,
    third: String,
    isHeader: Boolean = false
) {
    val textStyle =
        if (isHeader) MaterialTheme.typography.labelLarge else MaterialTheme.typography.bodySmall
    Row(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            modifier = Modifier.weight(1f),
            text = first,
            style = textStyle
        )
        Text(
            modifier = Modifier.weight(1f),
            text = second,
            style = textStyle.copy(
                textAlign = TextAlign.Center
            )
        )
        Text(
            modifier = Modifier.weight(1f),
            text = third,
            style = textStyle.copy(
                textAlign = TextAlign.End
            )
        )
    }
}

@Composable
private fun ReloadButton(
    modifier: Modifier = Modifier,
    onLoadClick: () -> Unit
) {
    Button(
        modifier = modifier,
        onClick = { onLoadClick.invoke() }
    ) {
        Text(text = "Reload")
    }
}

@Preview(showBackground = true)
@Composable
private fun RocketsListPreview() {
    ApplicationTheme {
        RocketList(
            isLoading = false, rockets = listOf(
                Rocket(
                    name = "Rocket 1",
                    active = false,
                    firstFlight = "2024-02-15"
                )
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun RocketsListErrorPreview() {
    ApplicationTheme {
        RocketList(isLoading = false, rockets = null)
    }
}

@Preview(showBackground = true)
@Composable
private fun RocketsListLoadingPreview() {
    ApplicationTheme {
        RocketList(isLoading = true, rockets = null)
    }
}