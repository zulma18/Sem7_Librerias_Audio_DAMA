package com.example.sem7_librerias_audio_dama

import android.content.Context
import android.media.MediaPlayer
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {

    private var reproductorMedia: MediaPlayer?= null
    private var fuenteAudioEstablecida = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // enableEdgeToEdge()

        setContentView(R.layout.activity_main)

        val btnReproducir = findViewById<ImageButton>(R.id.imgBoton)
        val btnDetener = findViewById<ImageButton>(R.id.imgBotonStop)
        val btnRemoto = findViewById<Button>(R.id.btnRemoto)
        val btnLocal = findViewById<Button>(R.id.btnLocal)

        reproductorMedia = MediaPlayer()

        btnLocal.setOnClickListener {
            try {
                if (reproductorMedia?.isPlaying == true) {
                    reproductorMedia?.stop()
                }
                reproductorMedia?.reset()
                try {
                    val idRecurso = R.raw.sonido_test
                    reproductorMedia?.setDataSource(this, android.net.Uri.parse("android.resource://$packageName/$idRecurso"))

                    reproductorMedia?.setOnPreparedListener {
                        fuenteAudioEstablecida = true
                        mostrarMensaje("Audio local listo para reproducir")
                    }
                    reproductorMedia?.setOnErrorListener { _, _, _ ->
                        fuenteAudioEstablecida = false
                        mostrarMensaje("Error al cargar el audio local")
                        false
                    }
                    mostrarMensaje("Cargando audio local...")
                    reproductorMedia?.prepare()
                } catch (e: Exception) {
                    mostrarMensaje("Error al acceder al archivo de audio local")
                }
            } catch (e: Exception) {
                mostrarMensaje("Error al cargar el audio local")
            }
        }

        btnRemoto.setOnClickListener {
            try {
                if (!hayConexionInternet()) {
                    mostrarMensaje("No hay conexion a internet")
                    return@setOnClickListener
                }
                if (reproductorMedia?.isPlaying == true) {
                    reproductorMedia?.stop()
                }
                reproductorMedia?.reset()
                val urlAudio = "https://tonosmovil.net/wp-content/uploads/tonosmovil.net_himno_champions_league.mp3"
                reproductorMedia?.setDataSource(urlAudio)
                reproductorMedia?.setOnPreparedListener {
                    fuenteAudioEstablecida = true
                    mostrarMensaje("Audio remoto listo para reproducir")
                }
                reproductorMedia?.setOnErrorListener { _, _, _ ->
                    fuenteAudioEstablecida = false
                    mostrarMensaje("Error al cargar el audio remoto")
                    false
                }
                mostrarMensaje("Cargando audio remoto...")
                reproductorMedia?.prepareAsync()
            } catch (e: Exception) {
                mostrarMensaje("Error al configurar el audio remoto")
            }

        }

        btnDetener.setOnClickListener {
            try {
                if (reproductorMedia?.isPlaying == true){
                    reproductorMedia?.stop()
                    reproductorMedia?.reset()
                    fuenteAudioEstablecida = false
                    mostrarMensaje("Reproduccion detenida")
                } else {
                    mostrarMensaje("No hay audio reproduciéndose")
                }
            } catch (e: Exception) {
                mostrarMensaje("Error al detener el audio")
            }
        }

        btnReproducir.setOnClickListener {
            try {
                if (reproductorMedia?.isPlaying == true) {
                    mostrarMensaje("El audio ya se esta reproduciendo")
                } else if (fuenteAudioEstablecida) {
                    reproductorMedia?.start()
                    mostrarMensaje("Reproduciendo audio")
                } else {
                    mostrarMensaje("Seleccione primero un audio(local o remoto)")
                }
            } catch (e: Exception) {
                mostrarMensaje("Error al reproducir el audio")
            }
        }

        /*
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        } */
    }

    private fun mostrarMensaje(mensaje: String) {
        Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show()
    }

    private fun hayConexionInternet(): Boolean {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false

        return when {
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
            else -> false
        }
    }
}