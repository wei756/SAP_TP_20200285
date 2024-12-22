package kr.ac.kumoh.ce.s20200285.sap_tp_20200285.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import kr.ac.kumoh.ce.s20200285.sap_tp_20200285.entities.Artist
import kr.ac.kumoh.ce.s20200285.sap_tp_20200285.viewmodel.ConcertViewModel

@Composable
fun ArtistScreen(viewModel: ConcertViewModel) {
    LaunchedEffect(Unit) {
        viewModel.loadFavorite()
    }
    ArtistList(viewModel)
}

@Composable
fun ArtistList(viewModel: ConcertViewModel) {
    val artistList by viewModel.artistList.observeAsState(emptyList())
    val favoriteList by viewModel.favoriteList.observeAsState(emptyList())

    LazyVerticalGrid(
        columns = GridCells.Fixed(2), // 2단 열
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp),
        contentPadding = PaddingValues(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(artistList) { artist ->
            ArtistItem(artist, isChecked = favoriteList.contains(artist.name), onChangeFavorite = {
                if (it) {
                    viewModel.addFavorite(artist.name)
                } else {
                    viewModel.removeFavorite(artist.name)
                }
            })
        }
    }
}

@Composable
fun ArtistItem(
    artist: Artist, isChecked: Boolean, onChangeFavorite: (Boolean) -> Unit
) {
    Card(
        elevation = CardDefaults.cardElevation(8.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min)
                .padding(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1F)
            ) {
                AsyncImage(
                    model = artist.profile_image_url,
                    contentDescription = "가수 이미지 ${artist.name}",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(corner = CornerSize(8.dp))),
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(artist.name_kr)
            FavoriteCheckBox(
                modifier = Modifier.align(Alignment.End),
                checked = isChecked,
                onCheckedChange = onChangeFavorite
            )
        }
    }
}

@Composable
fun FavoriteCheckBox(
    modifier: Modifier,
    checked: Boolean,
    onCheckedChange: ((Boolean) -> Unit),
) {
    if (checked) {
        Icon(Icons.Default.Favorite, contentDescription = "즐겨찾기 제거", modifier = modifier.clickable {
            onCheckedChange(false)
        })
    } else {
        Icon(Icons.Default.FavoriteBorder, contentDescription = "즐겨찾기 추가", modifier = modifier.clickable {
            onCheckedChange(true)
        })
    }
}