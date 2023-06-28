package com.example.mysolelife.fragments

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.example.mysolelife.*
import com.example.mysolelife.R
import com.example.mysolelife.databinding.FragmentStoreBinding
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.*
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.Overlay
import com.naver.maps.map.util.FusedLocationSource
import com.naver.maps.map.util.MarkerIcons
import com.naver.maps.map.widget.LocationButtonView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class StoreFragment : Fragment(), OnMapReadyCallback, Overlay.OnClickListener {

    private lateinit var binding: FragmentStoreBinding

    private lateinit var naverMap: NaverMap
    private lateinit var mapView: MapView
    private lateinit var locationSource : FusedLocationSource

    private lateinit var viewPager: ViewPager2

    private lateinit var recyclerView : RecyclerView
    private lateinit var currentLocationButton : LocationButtonView

    private val viewPagerAdapter = HouseViewPagerAdapter()
    private val recyclerAdapter = HouseListAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_store, container, false)

        binding.homeTap.setOnClickListener {

            it.findNavController().navigate(R.id.action_storeFragment_to_homeFragment)
        }

        binding.tipTap.setOnClickListener {

            it.findNavController().navigate(R.id.action_storeFragment_to_tipFragment)
        }

        binding.talkTap.setOnClickListener {

            it.findNavController().navigate(R.id.action_storeFragment_to_talkFragment)
        }

        binding.bookmarkTap.setOnClickListener {

            it.findNavController().navigate(R.id.action_storeFragment_to_bookFragment)
        }

        // mapView 연결해주기
        mapView = binding.mapView
        mapView.onCreate(savedInstanceState)

        mapView.getMapAsync(this)

        // viewPager 연결해주기
        viewPager = binding.houseViewPager
        viewPager.adapter = viewPagerAdapter

        // recyclerView 연결해주기
        recyclerView = binding.user.findViewById(R.id.recyclerView)
        recyclerView.adapter = recyclerAdapter
        recyclerView.layoutManager = LinearLayoutManager(context)

        // currentLocationButton연결해주기
        currentLocationButton = binding.currentLocationButton

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {

            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)

                val selectedHouseModel = viewPagerAdapter.currentList[position]
                val cameraUpdate = CameraUpdate.scrollTo(LatLng(selectedHouseModel.lat, selectedHouseModel.lng))
                    .animate(CameraAnimation.Easing)

                naverMap.moveCamera(cameraUpdate)
            }

        })

        return binding.root

    }

    override fun onMapReady(map: NaverMap) {
        naverMap = map

        // 줌범위 설정
        naverMap.maxZoom = 20.0
        naverMap.minZoom = 10.0

        // 네이버 지도 켜줬을 때 초기위치 설정(안할시 서웉시청으로 설정됨)
        val cameraUpdate = CameraUpdate.scrollTo(LatLng(37.498051, 127.027562))
        naverMap.moveCamera(cameraUpdate)

        // 현위치 버튼 설정
        // - 위치정보는 민감해서 사용자로부터 권한을 동의 받아야함(매니패스트에 권한 추가해줘야함)
        // - 1.0이후로 부터 팝업을 통해서 권한을 확인받아야함 (build.gradle app에 권한 추가해줘야함)
        val uiSetting = naverMap.uiSettings
        uiSetting.isLocationButtonEnabled = false

        currentLocationButton.map = naverMap

        // locationSource 등록해주기
        locationSource = FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE)
        naverMap.locationSource = locationSource

        getHouseListFromAPI()
    }

    private fun getHouseListFromAPI() {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://run.mocky.io")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        retrofit.create(HouseService::class.java).also {
            it.getHouseList()
                .enqueue(object: Callback<HouseDto> {
                    override fun onResponse(call: Call<HouseDto>, response: Response<HouseDto>) {
                        if (response.isSuccessful.not()) {

                            return
                        }

                        response.body()?.let { dto ->
                            //Log.d("Retrofit", dto.toString())
                            updateMarker(dto.items)
                            viewPagerAdapter.submitList(dto.items)
                            recyclerAdapter.submitList(dto.items)
                        }
                    }

                    override fun onFailure(call: Call<HouseDto>, t: Throwable) {

                    }

                })
        }
    }

    private fun updateMarker(houses: List<HouseModel>) {
        houses.forEach { house ->
            val marker = Marker()
            marker.position = LatLng(house.lat, house.lng)

            marker.onClickListener = this

            marker.map = naverMap
            marker.tag = house.id
            marker.icon = MarkerIcons.BLACK
            marker.iconTintColor = Color.RED
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode != LOCATION_PERMISSION_REQUEST_CODE) {
            return
        }

        if (locationSource.onRequestPermissionsResult(requestCode, permissions,grantResults)) {
            if (!locationSource.isActivated) {
                naverMap.locationTrackingMode = LocationTrackingMode.None
            }
            return
        }

    }

    // 생명주기 연결해주기
    override fun onStart() {
        super.onStart()
        mapView.onStart()
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapView.onSaveInstanceState(outState)
    }

    override fun onStop() {
        super.onStop()
        mapView.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1000
    }

    override fun onClick(overly: Overlay): Boolean {
        val selectedModel = viewPagerAdapter.currentList.firstOrNull {
            it.id == overly.tag
        }

        selectedModel?.let {
            val position = viewPagerAdapter.currentList.indexOf(it)
            viewPager.currentItem = position
        }

        return true

    }



}