package com.kaushalvasava.apps.instagram.ui.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kaushalvasava.apps.instagram.models.ApiFailure
import com.kaushalvasava.apps.instagram.models.BaseState
import com.kaushalvasava.apps.instagram.models.User
import com.kaushalvasava.apps.instagram.ui.components.CenterCircularProgressBar
import com.kaushalvasava.apps.instagram.ui.components.CenterErrorText
import com.kaushalvasava.apps.instagram.ui.components.CircularImage
import com.kaushalvasava.apps.instagram.ui.navigation.NavigationItem
import com.kaushalvasava.apps.instagram.util.AppConstants.MY_USER_ID
import com.kaushalvasava.apps.instagram.viewmodel.HomeViewModel
import moe.tlaster.precompose.navigation.Navigator

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatListScreen(
    homeViewModel: HomeViewModel,
    navController: Navigator,
) {
    val usersState by homeViewModel.users.collectAsState()
    Column(Modifier.fillMaxSize()) {
        var searchData by rememberSaveable {
            mutableStateOf(emptyList<User>())
        }
        var query by rememberSaveable {
            mutableStateOf("")
        }
        TopAppBar(
            title = {
                Text(text = "Chats", fontSize = 18.sp)
            },
            actions = {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = "menu",
                )
            }, navigationIcon = {
                IconButton(onClick = {
                    navController.popBackStack()
                }) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "back",
                    )
                }
            }
        )
        SearchBar(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            query = query,
            leadingIcon = {
                Icon(Icons.Default.Search, contentDescription = "Search")
            },
            trailingIcon = {
                Icon(
                    Icons.Default.Close, contentDescription = "Clear",
                    modifier = Modifier.clickable {
                        query = ""
                    }
                )
            },
            onQueryChange = { q ->
                val d = searchData.filter {
                    it.name.lowercase().contains(q.lowercase())
                }
                searchData = d
                query = q
            },
            placeholder = {
                Text(
                    "Search name",
                    fontSize = 16.sp,
                    color = Color.Gray
                )
            },
            onSearch = {},
            active = false,
            onActiveChange = {
            }
        ) {}
        Spacer(modifier = Modifier.height(8.dp))

        when (val state = usersState) {
            is BaseState.Failed -> {
                when (state.error) {
                    is ApiFailure.Unknown -> {
                        CenterErrorText(msg = state.error.error)
                    }
                }
            }

            BaseState.Loading -> {
                CenterCircularProgressBar()
            }

            is BaseState.Success -> {
                LazyColumn(Modifier.padding(horizontal = 16.dp)) {
                    if (searchData.isEmpty() || query.isEmpty()) {
                        searchData = state.data
                    } else {
                        searchData.map { it.name.lowercase() }.contains(query.lowercase())
                    }
                    items(searchData.filterNot {
                        it.id == MY_USER_ID
                    }) {
                        UserChatItem(
                            userName = it.name,
                            imageUrl = it.profileImage,
                            lastMsg = "Hello, How r Khana khake jana",
                            modifier = Modifier.clickable {
                                navController.navigate(
                                    "${NavigationItem.Chat.route}/${it.id}"
                                )
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun UserChatItem(
    userName: String,
    imageUrl: String,
    lastMsg: String,
    modifier: Modifier = Modifier,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .padding(vertical = 4.dp)
            .fillMaxWidth()
    ) {
        CircularImage(imageUrl = imageUrl, imageSize = 55.dp)
        Spacer(modifier = Modifier.width(8.dp))
        Column(
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Center
        ) {
            Text(userName)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                lastMsg,
                color = Color.Gray,
                maxLines = 1,
                fontSize = 12.sp,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}