package com.example.logonrm.myapplication

import android.Manifest
import android.content.DialogInterface
import android.location.Address
import android.location.Geocoder
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.AlertDialog
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.LocationServices

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.android.synthetic.main.activity_maps.*
import android.support.v4.app.ActivityCompat
import android.content.pm.PackageManager
import android.support.v4.content.ContextCompat




class MapsActivity : AppCompatActivity(), OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private lateinit var mMap: GoogleMap
    private lateinit var mGoogleApiClient: GoogleApiClient

    override fun onConnectionFailed(p0: ConnectionResult) {
    }


    override fun onConnected(p0: Bundle?) {
        checkPermission()

    }

    override fun onConnectionSuspended(p0: Int) {

    }


    private fun checkPermission() {
        val permission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)

        if (permission != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                val builder = AlertDialog.Builder(this)

                builder.setMessage("Necessária a permissao para GPS")
                        .setTitle("Permissao Requerida")

                builder.setPositiveButton("OK") { dialog, id ->
                    requestPermission()
                }

                val dialog = builder.create()
                dialog.show()

            } else {
                requestPermission()
            }
        }
    }

    protected fun requestPermission() {
        val REQUEST_GPS = 200
        ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_GPS)
    }
    

    fun callConnection() {
        mGoogleApiClient = GoogleApiClient.Builder(this).
                addOnConnectionFailedListener(this).
                addConnectionCallbacks(this).
                addApi(LocationServices.API).build()

        mGoogleApiClient.connect()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        btPesquisar.setOnClickListener {
            val geocoder = Geocoder(this)
            var address: List<Address>?

            address = geocoder.getFromLocationName(etEndereco.text.toString(),
                    1)

            if (!address.isEmpty()) {
                val location = address[0]
                adicionarMarcador(location.latitude, location.longitude, location.locality)
            } else {
                var alert = AlertDialog.Builder(this).create()
                alert.setTitle("Erro ao Localizar")
                alert.setMessage("Endereço Não localizado!")
                alert.setCancelable(false)
                alert.setButton(AlertDialog.BUTTON_POSITIVE, "OK", { dialog: DialogInterface?, which: Int ->
                    alert.dismiss()
                })
                alert.show()
            }
        }
    }

    fun adicionarMarcador(latitude: Double, longitude: Double, titulo: String) {
        mMap.clear()
        val sydney = LatLng(latitude, longitude)
        mMap.addMarker(MarkerOptions().position(sydney).title(titulo).icon(BitmapDescriptorFactory.
                fromResource(R.mipmap.ic_launcher_round)))

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 15f))

    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        callConnection()
    }
}
