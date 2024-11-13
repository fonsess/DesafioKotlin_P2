package br.unisanta.teste.view

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import br.unisanta.teste.adapter.PedidoAdapter
import br.unisanta.teste.adapter.StatusAdapter
import br.unisanta.teste.databinding.ActivityMain3Binding
import br.unisanta.teste.model.Pedido
import br.unisanta.teste.model.pedidoAdmin
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity3 : AppCompatActivity() {
    private lateinit var binding: ActivityMain3Binding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMain3Binding.inflate(layoutInflater)
        setContentView(binding.root)
        val userId = intent.getStringExtra("USER_ID")
        getUserName(userId.toString()) { patientName ->
            binding.txtDr.text = "Olá, $patientName"
        }

        binding.recyclerViewAgendamentos.layoutManager = LinearLayoutManager(this)

        binding.btVoltaDr.setOnClickListener {
            finish()
        }

        loadPedidos()
    }

    private fun loadPedidos() {
        val db = FirebaseFirestore.getInstance()
        db.collection("Restaurante")
            .get()
            .addOnSuccessListener { documents ->
                val pedidos = mutableListOf<pedidoAdmin>()

                for (document in documents) {
                    val comida = document.getString("comida") ?: ""
                    val bebida = document.getString("bebida") ?: ""
                    val status = document.getString("status") ?: ""

                    val codigo = document.id

                    val pedido = pedidoAdmin(
                        comida = comida,
                        bebida = bebida,
                        status = status,
                        codigo = codigo
                    )

                    pedidos.add(pedido)

                    if (pedidos.size == documents.size()) {
                        val adapter = StatusAdapter(pedidos)
                        binding.recyclerViewAgendamentos.adapter = adapter
                    }
                }
            }
            .addOnFailureListener { exception ->
                Log.e("Firestore", "Erro ao buscar agendamentos: ${exception.message}")
            }
    }

    fun getUserName(userId: String, onResult: (String?) -> Unit) {
        val db = FirebaseFirestore.getInstance()
        val userRef = db.collection("Users").document(userId)

        userRef.get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val userName = document.getString("nome")
                    onResult(userName)
                } else {
                    onResult(null)
                }
            }
            .addOnFailureListener { exception ->
                Log.e("Firestore", "Erro ao buscar nome do usuário: ${exception.message}")
                onResult(null)
            }
    }

}