package com.example.myapitest.view

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.myapitest.databinding.ActivityMainBinding
import com.example.myapitest.service.RetrofitClient
import kotlinx.coroutines.launch
import android.util.Log
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapitest.adapter.CarAdapter
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: CarAdapter
    private var carsList = mutableListOf<com.example.myapitest.model.Car>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        adapter = CarAdapter(carsList) { car ->
            val intent = Intent(this, CarMapaActivity::class.java).apply {
                putExtra("lat", car.place.lat)
                putExtra("long", car.place.long)
            }
            startActivity(intent)
        }

        setupView()
    }

    override fun onResume() {
        super.onResume()
        fetchItems()
    }

    private fun setupView() {

        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter


        binding.swipeRefreshLayout.setOnRefreshListener {
            fetchItems()
        }


        binding.btVoltar.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Sair")
                .setMessage("Deseja deslogar do aplicativo?")
                .setPositiveButton("Sim") { _, _ ->
                    FirebaseAuth.getInstance().signOut()
                    startActivity(Intent(this, LoginActivity::class.java))
                    finish()
                }
                .setNegativeButton("Não", null)
                .show()
        }
    }

    private fun fetchItems() {

        binding.swipeRefreshLayout.isRefreshing = true

        lifecycleScope.launch {
            try {
                val cars = RetrofitClient.apiService.getCars()

                carsList.clear()
                carsList.addAll(cars)
                adapter.notifyDataSetChanged()

                binding.recyclerView.visibility = if (cars.isEmpty()) View.GONE else View.VISIBLE
            } catch (e: Exception) {
                Log.e("API_ERROR", "Erro: ${e.message}")
            } finally {
                binding.swipeRefreshLayout.isRefreshing = false
            }
        }
    }
}