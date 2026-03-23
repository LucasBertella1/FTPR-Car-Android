package com.example.myapitest.view

import com.example.myapitest.R
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class CarMapaActivity : AppCompatActivity(), OnMapReadyCallback {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_car_map)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment

        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {

        val lat = intent.getDoubleExtra("lat", 0.0)
        val long = intent.getDoubleExtra("long", 0.0)

        val location = LatLng(lat, long)

        googleMap.addMarker(
            MarkerOptions().position(location).title("Local do carro")
        )

        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 15f))
    }
}