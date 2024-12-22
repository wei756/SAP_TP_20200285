package kr.ac.kumoh.ce.s20200285.sap_tp_20200285.viewmodel

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kr.ac.kumoh.ce.s20200285.sap_tp_20200285.api.ConcertApi
import kr.ac.kumoh.ce.s20200285.sap_tp_20200285.api.ConcertApiConfig
import kr.ac.kumoh.ce.s20200285.sap_tp_20200285.entities.Artist
import kr.ac.kumoh.ce.s20200285.sap_tp_20200285.entities.ScheduleWithArtist
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ConcertViewModel(application: Application) : AndroidViewModel(application) {
    private val concertApi: ConcertApi
    private val _artistList = MutableLiveData<List<Artist>>()
    val artistList: LiveData<List<Artist>>
        get() = _artistList
    private val _scheduleList = MutableLiveData<List<ScheduleWithArtist>>()
    val scheduleList: LiveData<List<ScheduleWithArtist>>
        get() = _scheduleList
    private val _favoriteList = MutableLiveData<List<String>>(emptyList())
    val favoriteList: LiveData<List<String>>
        get() = _favoriteList

    init {
        val retrofit = Retrofit.Builder().baseUrl(ConcertApiConfig.SERVER_URL)
            .addConverterFactory(GsonConverterFactory.create()).build()

        concertApi = retrofit.create(ConcertApi::class.java)
        loadFavorite()
        fetchArtistData()
    }

    fun fetchScheduleData(isOnlyFavorite: Boolean) {
        // Coroutine 사용
        viewModelScope.launch {
            try {
                // 아티스트 정보 미리 가져오기
                fetchArtistDataSync()

                val schedules: MutableList<ScheduleWithArtist> = mutableListOf()
                val resSchedule = if (isOnlyFavorite) concertApi.getSchedulesByArtists(
                    or = "(artist.ilike.*${
                        favoriteList.value!!.joinToString(
                            "*,artist.ilike.*"
                        )
                    }*)"
                )
                else concertApi.getSchedules(limit = "300")
                resSchedule.forEach { schedule ->
//                    val resArtist = concertApi.getArtistsByName(name = "eq.${schedule.artist}")
                    val artist = (_artistList.value
                        ?: emptyList<Artist>()).find { it.name == schedule.artist }
                    if (artist != null) {
                        // 아티스트 정보 join 후 리스트에 추가
                        schedules.add(
                            ScheduleWithArtist(
                                schedule.id,
                                schedule.key,
                                schedule.title,
                                artist,
                                schedule.startDate,
                                schedule.endDate,
                                schedule.place,
                                schedule.site
                            )
                        )
                    }
                }
                Log.e("viewModel", schedules.toString())
                _scheduleList.value = schedules.toList()
            } catch (e: Exception) {
                Log.e("fetchData()", e.toString())
            }
        }
    }

    // 아티스트 정보를 데이터베이스에서 가져옴
    private fun fetchArtistData() {
        // Coroutine 사용
        viewModelScope.launch {
            fetchArtistDataSync()
        }
    }

    // 다른 suspend 함수에서 호출할 수 있는
    private suspend fun fetchArtistDataSync() {
        try {
            val resSchedule = concertApi.getArtists(limit = "300")
            _artistList.value = resSchedule
        } catch (e: Exception) {
            Log.e("fetchData()", e.toString())
        }
    }

    fun loadFavorite() {
        viewModelScope.launch(Dispatchers.IO) {
            val context = getApplication<Application>()

            // 최초 실행시 초기화
            if (!context.fileList().contains("favorites")) {
                Log.i("ConcertViewModel", "최초 실행으로 인한 초기화")
                viewModelScope.launch {
                    _favoriteList.value = emptyList()
                }
                saveFavorite()
            } else {
                // 즐겨찾기 목록 파일에서 읽기
                context.openFileInput("favorites").bufferedReader().use {
                    val tmp = it.readText()
                    Log.i("ConcertViewModel", tmp)
                    viewModelScope.launch {
                        _favoriteList.value = tmp.split("|||||").filter { it.isNotEmpty() }
                        Log.e("aaaaa", _favoriteList.value.toString())
                    }
                }
            }
        }
    }

    private fun saveFavorite() {
        val fileContents = _favoriteList.value?.joinToString("|||||") ?: ""
        // 즐겨찾기 목록 파일로 저장
        viewModelScope.launch(Dispatchers.IO) {
            val context = getApplication<Application>()
            context.openFileOutput("favorites", Context.MODE_PRIVATE).use {
                it.write(fileContents.toByteArray())
            }
        }
    }

    fun addFavorite(artistName: String) {
        // 즐겨찾기 목록 추가 후 저장
        if (!_favoriteList.value!!.contains(artistName)) {
            _favoriteList.value = _favoriteList.value!! + artistName
            saveFavorite()
        }
    }

    fun removeFavorite(artistName: String) {
        // 즐겨찾기 목록 제거 후 저장
        if (_favoriteList.value!!.contains(artistName)) {
            _favoriteList.value = _favoriteList.value!! - artistName
            saveFavorite()
        }
    }

    fun clearScheduleList() {
        _scheduleList.value = emptyList()
    }
}