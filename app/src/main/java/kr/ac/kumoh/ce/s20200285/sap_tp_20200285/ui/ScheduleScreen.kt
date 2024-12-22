package kr.ac.kumoh.ce.s20200285.sap_tp_20200285.ui

import android.content.Intent
import android.net.Uri
import android.icu.text.SimpleDateFormat
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import kr.ac.kumoh.ce.s20200285.sap_tp_20200285.entities.ScheduleWithArtist
import kr.ac.kumoh.ce.s20200285.sap_tp_20200285.viewmodel.ConcertViewModel

@Composable
fun ScheduleScreen(viewModel: ConcertViewModel, isFavorite: Boolean) {
    LaunchedEffect(Unit) {
        viewModel.loadFavorite()
        viewModel.clearScheduleList()
    }
    LaunchedEffect(viewModel.favoriteList) {
        viewModel.fetchScheduleData(isFavorite)
    }
    ScheduleList(viewModel)
}

@Composable
fun ScheduleList(viewModel: ConcertViewModel) {
    val scheduleList by viewModel.scheduleList.observeAsState(emptyList())
    val favoriteList by viewModel.favoriteList.observeAsState(emptyList())

    LazyColumn {
        items(scheduleList) { schedule ->
            ScheduleItem(schedule,
                isChecked = favoriteList.contains(schedule.artist.name),
                onChangeFavorite = {
                    if (it) {
                        viewModel.addFavorite(schedule.artist.name)
                    } else {
                        viewModel.removeFavorite(schedule.artist.name)
                    }
                })
        }
    }
}

@Composable
fun ScheduleItem(
    schedule: ScheduleWithArtist, isChecked: Boolean, onChangeFavorite: (Boolean) -> Unit
) {
    val (expanded, setExpanded) = remember { mutableStateOf(false) }
    val dateFormat = SimpleDateFormat("yyyy-MM-dd")
    val context = LocalContext.current

    Card(
        modifier = Modifier.clickable {
            setExpanded(!expanded)
        },
        elevation = CardDefaults.cardElevation(8.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min)
                .padding(8.dp)
        ) {
            AsyncImage(
                model = schedule.artist.profile_image_url,
                contentDescription = "가수 이미지 ${schedule.artist.name}",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(100.dp)
                    //.clip(CircleShape),
                    .clip(RoundedCornerShape(corner = CornerSize(8.dp))),
            )
            Spacer(modifier = Modifier.width(10.dp))
            Column(
                modifier = Modifier.weight(1f), verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column(
                ) {
                    Text(schedule.title)
                }
                Text(dateFormat.format(schedule.startDate))
            }
            Spacer(Modifier.width(8.dp))
            FavoriteCheckBox(
                checked = isChecked,
                onCheckedChange = onChangeFavorite,
                modifier = Modifier.align(Alignment.CenterVertically)
            )
        }
        AnimatedVisibility(
            visible = expanded,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(schedule.artist.name_kr)
                    Text(schedule.place)
                    Text(when(schedule.site) {
                        "lawson" -> "로손티켓"
                        "eplus" -> "이플러스"
                        "pia" -> "티켓피아"
                        else -> "알 수 없음"
                    })
                }
                TextButton(
                    onClick = {
                        val uri = Uri.parse(when(schedule.site) {
                            "lawson" -> "https://l-tike.com/search/?keyword=${schedule.title}"
                            "eplus" -> "https://eplus.jp/sf/search?block=true&keyword=${schedule.title}"
                            "pia" -> "https://t.pia.jp/pia/search_all.do?kw=${schedule.title}"
                            else -> "https://www.google.com/search?q=${schedule.title}"// 알 수 없는 사이트일 때 구글 검색
                        })
                        context.startActivity(Intent(Intent.ACTION_VIEW, uri))
                    }
                ) { Text("티켓팅 링크") }
            }
        }
    }
}