package com.storen.xidiandoc

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.storen.xidiandoc.bean.ChoiceQuestion
import com.storen.xidiandoc.ui.theme.XiDianDocTheme
import com.storen.xidiandoc.util.DocUtil

class MainActivity : ComponentActivity() {

    private val questionList by lazy { DocUtil.parseDoc1(DocUtil.readTxtFile(assets)) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            XiDianDocTheme {
                val systemUiController = rememberSystemUiController()
                LaunchedEffect(key1 = Unit, block = {
                    systemUiController.setStatusBarColor(Color.Transparent, true)
                })
                // A surface container using the 'background' color from the theme
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    MainScreen(questionList)
                }
            }
        }
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(questionList: List<ChoiceQuestion>) {
    ModalNavigationDrawer(
        modifier = Modifier.fillMaxSize(),
        gesturesEnabled = false,
        drawerContent = {
            ModalNavigationDrawerContent()
        }) {

        val rememberLazyListState = rememberLazyListState()
        var searchText by remember { mutableStateOf("") }
        val questions by remember(searchText) {
            mutableStateOf(questionList.filter { item ->
                item.question.contains(searchText) or (item.itemList.find { it.contains(searchText) } != null)
            })
        }

        Scaffold(
            modifier = Modifier.fillMaxSize(),
            floatingActionButtonPosition = FabPosition.Center,
            floatingActionButton = {
                var fabExpanded by remember {
                    /*derivedStateOf {
                        rememberLazyListState.firstVisibleItemScrollOffset == 0
                    }*/
                    mutableStateOf(false)
                }
                ExtendedFloatingActionButton(text = {
                    BasicTextField(
                        value = searchText,
                        onValueChange = {
                            searchText = it
                        },
                        modifier = Modifier.height(35.dp),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                        keyboardActions = KeyboardActions(onSearch = {
                        }),
                        decorationBox = { innerTextField ->
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    border = BorderStroke(1.dp, Color.Gray),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth(.5f)
                                                .padding(start = 4.dp, end = 4.dp)
                                        ) {
                                            if (searchText.isEmpty()) Text(text = "搜索", color = Color(0x88000000))
                                            innerTextField()
                                        }
                                        Icon(
                                            imageVector = Icons.Outlined.Close,
                                            contentDescription = null,
                                            tint = Color(0x88000000),
                                            modifier = Modifier.clickable {
                                                searchText = ""
                                            })
                                    }
                                }
//                                IconButton(onClick = { /*TODO*/ }) {
//                                    Icon(imageVector = Icons.Outlined.KeyboardArrowUp, contentDescription = "上一个")
//                                }
//                                IconButton(onClick = { /*TODO*/ }) {
//                                    Icon(imageVector = Icons.Outlined.KeyboardArrowDown, contentDescription = "下一个")
//                                }
                                Spacer(
                                    modifier = Modifier
                                        .width(10.dp)
                                        .fillMaxHeight()
                                )
                                Text(text = "${questions.size}/${questionList.size}")
                            }
                        }
                    )
                }, icon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "搜索"
                    )
                }, onClick = {
                    fabExpanded = !fabExpanded
                }, expanded = fabExpanded)
            },
            content = {
                Box(modifier = Modifier.padding(it)) {
                    DocViewer(
                        questions,
                        searchText,
                        listState = rememberLazyListState,
                    )
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModalNavigationDrawerContent(onDrawerClicked: () -> Unit = {}) {
    ModalDrawerSheet {
        NavigationDrawerItem(label = {
            Text(text = "选择题", modifier = Modifier.padding(horizontal = 16.dp))
        }, icon = {
            Icon(imageVector = Icons.Default.Check, contentDescription = "选择题")
        }, selected = true, onClick = { /*TODO*/ })
        NavigationDrawerItem(label = {
            Text(text = "判断题", modifier = Modifier.padding(horizontal = 16.dp))
        }, icon = {
            Icon(imageVector = Icons.Default.Check, contentDescription = "选择题")
        }, selected = false, onClick = { /*TODO*/ })
        NavigationDrawerItem(label = {
            Text(text = "选择题", modifier = Modifier.padding(horizontal = 16.dp))
        }, icon = {
            Icon(imageVector = Icons.Default.Check, contentDescription = "选择题")
        }, selected = false, onClick = { /*TODO*/ })
    }
}

@Composable
fun DocViewer(
    questions: List<ChoiceQuestion>,
    searchText: String,
    modifier: Modifier = Modifier,
    listState: LazyListState = rememberLazyListState()
) {
//    val statusBarHeightDp = LocalDensity.current.run {
//        WindowInsets.statusBars.getTop(this).toDp()
//    }
    LazyColumn(modifier = modifier, state = listState) {
        itemsIndexed(questions) { index, item ->
            QuestionItem(question = item, searchText = searchText)
        }
        item {
            Box(modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)) {}
        }
    }
}

@Composable
fun QuestionItem(question: ChoiceQuestion, searchText: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OutlinedCard(
            modifier = Modifier
                .fillMaxWidth(1f)
                .wrapContentHeight()
                .padding(horizontal = 5.dp, vertical = 2.5.dp)
        ) {
            Text(
                modifier = Modifier.padding(5.dp), text = markSearchText(question.question, searchText = searchText), fontWeight = FontWeight.W900
            )
            question.itemList.forEachIndexed { index, item ->
                Text(
                    modifier = Modifier.padding(horizontal = 5.dp),
                    text = markSearchText(item, searchText = searchText),
                    color = if (index in question.answerIndexes) Color.Blue else Color.Red,
                    fontWeight = FontWeight.W600
                )
            }
            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(5.dp)
            )
        }
    }
}


@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    XiDianDocTheme {
        QuestionItem(ChoiceQuestion(question = "Aha Aha Aha Aha Aha Aha Aha Aha Aha Aha"), "Aha")
    }
}

private fun markSearchText(str: String, searchText: String): AnnotatedString {
    return if (searchText.isEmpty() || !str.contains(searchText)) {
        AnnotatedString(str)
    } else {
        buildAnnotatedString {
            val strings = str.split(searchText)
            strings.forEachIndexed { index, s ->
                append(s)
                if (index != strings.size - 1 || str.endsWith(searchText)) {
                    withStyle(SpanStyle(color = Color.Green)) {
                        append(searchText)
                    }
                }
            }
        }
    }
}