package eu.petrfaruzel.example

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import eu.petrfaruzel.collapsibletopbar.CollapsibleScaffold
import eu.petrfaruzel.collapsibletopbar.CollapsibleTopBar
import eu.petrfaruzel.collapsibletopbar.R
import eu.petrfaruzel.example.ui.theme.CollapsibleTopBarTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CollapsibleTopBarTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    ExampleScreen()
                }
            }
        }
    }
}

@Composable
fun ExampleScreen() {
    val listState = rememberLazyListState()
    CollapsibleScaffold(
        state = listState,
        topBar = {
            CollapsibleTopBar(
                title = "This is my title that is really long and shall be long enough to cover at least three lines",
                label = "This is my label that is also way too long",
                actions = {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .fillMaxHeight()
                    ) {
                        IconButton(
                            onClick = {
                                Log.d(
                                    "TAG",
                                    "OnBackClick"
                                )
                            }) {
                            Icon(
                                Icons.Filled.Share,
                                stringResource(id = R.string.app_name)
                            )
                        }
                    }
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .fillMaxHeight()
                    ) {
                        IconButton(
                            onClick = {
                                // TODO
                            }) {
                            Icon(
                                Icons.Filled.Add,
                                stringResource(id = R.string.app_name)
                            )
                        }
                    }
                }
            )
        }
    ) { insets ->
        LazyColumn(
            state = listState,
            contentPadding = insets
        ) {
            items(100) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                        .height(80.dp)
                ) {
                    Text(
                        text = "Item ${it + 1}",
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxSize().wrapContentHeight(align = Alignment.CenterVertically)
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    CollapsibleTopBarTheme {
        ExampleScreen()
    }
}