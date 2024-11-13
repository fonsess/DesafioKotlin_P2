package br.unisanta.teste.view

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import br.unisanta.teste.R
import br.unisanta.teste.adapter.PedidoAdapter
import br.unisanta.teste.databinding.ActivityMain4Binding
import br.unisanta.teste.model.Pedido
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity4 : AppCompatActivity() {
    private lateinit var binding: ActivityMain4Binding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMain4Binding.inflate(layoutInflater)
        setContentView(binding.root)
        val userId = intent.getStringExtra("USER_ID")

        getUserName(userId.toString()) { patientName ->
            binding.txtC.text = "Olá, $patientName"
        }

        binding.recyclerViewAgendamentosC.layoutManager = LinearLayoutManager(this)

        binding.btnVoltaC.setOnClickListener {
            finish()
        }

        binding.btnAgendar.setOnClickListener {
            showAppointmentDialog()
        }

        loadAgendamentos(userId)
    }

    private fun loadAgendamentos(userId: String?) {
        val db = FirebaseFirestore.getInstance()
        db.collection("Restaurante")
            .whereEqualTo("clienteId", userId)
            .get()
            .addOnSuccessListener { documents ->
                val pedidos = mutableListOf<Pedido>()

                for (document in documents) {
                    val comida = document.getString("comida") ?: ""
                    val bebida = document.getString("bebida") ?: ""
                    val status = document.getString("status") ?: ""
                    val pedido = Pedido(
                        comida = comida ,
                        bebida = bebida,
                        status = status
                    )
                    pedidos.add(pedido)
                    if (pedidos.size == documents.size()) {
                        val adapter = PedidoAdapter(pedidos)
                        binding.recyclerViewAgendamentosC.adapter = adapter
                    }
                }
            }
            .addOnFailureListener { exception ->
                Log.e("Firestore", "Erro ao buscar pedidos: ${exception.message}")
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

    private fun showAppointmentDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_appointment, null)

        val spinnerComida = dialogView.findViewById<Spinner>(R.id.spinnerComida)
        val spinnerBebida = dialogView.findViewById<Spinner>(R.id.spinnerBebida)

        val descriptionOptions = arrayOf("Hambúrguer $10,99", "Hot-dog $5,00", "Batata frita $3,50")
        spinnerComida.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, descriptionOptions)

        val bebidaOptions = arrayOf("Coca-cola $1,99", "Guaraná $2,00", "Cerveja $3,50")
        spinnerBebida.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, bebidaOptions)

        AlertDialog.Builder(this)
            .setTitle("Fazer pedido")
            .setView(dialogView)
            .setPositiveButton("Pedir") { _, _ ->
                val comida = spinnerComida.selectedItem.toString()
                val bebida = spinnerBebida.selectedItem.toString()

                saveAppointment(comida, bebida)
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun saveAppointment(comida: String, bebida: String) {
        val db = FirebaseFirestore.getInstance()
        val appointment = hashMapOf(
            "comida" to comida,
            "bebida" to bebida,
            "status" to "pendente",
            "clienteId" to FirebaseAuth.getInstance().currentUser?.uid
        )

        db.collection("Restaurante")
            .add(appointment)
            .addOnSuccessListener {
                Toast.makeText(this, "Pedido feito com sucesso!", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { exception ->
                Log.e("Firestore", "Erro ao fazer pedido: ${exception.message}")
                Toast.makeText(this, "Erro ao fazer pedido", Toast.LENGTH_SHORT).show()
            }
    }

}