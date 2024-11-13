package br.unisanta.teste.view

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import br.unisanta.teste.R
import br.unisanta.teste.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnLogin.setOnClickListener {
            if (binding.edtEmailLogin.text.toString().length == 0 || binding.edtPasswordLogin.text.toString().length == 0) {
                Toast.makeText(this, "Preencha os campos obrigatóriamente!", Toast.LENGTH_SHORT).show()
            }else{
                if(binding.edtEmailLogin.text.toString().contains("@")){
                    loginUser(binding.edtEmailLogin.text.toString(), binding.edtPasswordLogin.text.toString())
                }else{
                    Toast.makeText(this, "Email inválido!", Toast.LENGTH_SHORT).show()
                }
            }
        }

        binding.btnCadastra.setOnClickListener {
            val intent = Intent(this, MainActivity2::class.java)
            startActivity(intent)
        }
    }

    fun loginUser(email: String, password: String) {
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val userId = task.result?.user?.uid
                    userId?.let { fetchUserRole(it) }
                    binding.edtEmailLogin.text.clear()
                    binding.edtPasswordLogin.text.clear()
                } else {
                    Toast.makeText(this, "Email e/ou senha incorretos!", Toast.LENGTH_SHORT).show()
                }
            }
    }

    fun fetchUserRole(userId: String) {
        val db = FirebaseFirestore.getInstance()
        db.collection("Users").document(userId).get()
            .addOnSuccessListener { document ->
                val role = document.getString("role")
                val intent = if (role == "restaurante") {
                    Intent(this, MainActivity3::class.java)
                } else {
                    Intent(this, MainActivity4::class.java)
                }
                intent.putExtra("USER_ID", userId)
                startActivity(intent)
            }
            .addOnFailureListener {
                Toast.makeText(this, "Usuário não encontrado!", Toast.LENGTH_SHORT).show()
            }
    }
}