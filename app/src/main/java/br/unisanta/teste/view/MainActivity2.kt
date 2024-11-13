package br.unisanta.teste.view

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import br.unisanta.teste.R
import br.unisanta.teste.databinding.ActivityMain2Binding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity2 : AppCompatActivity() {
    private lateinit var binding: ActivityMain2Binding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMain2Binding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnCadastrar.setOnClickListener {
            if (binding.edtEmailCadastro.text.toString().length == 0 || binding.edtPasswordCadastro.text.toString().length == 0 || binding.edtNameCadastro.text.toString().length == 0) {
                Toast.makeText(this, "Preencha os campos obrigatóriamente!", Toast.LENGTH_SHORT).show()
            }else{
                if(binding.edtEmailCadastro.text.toString().contains("@")){
                    registerUser(binding.edtNameCadastro.text.toString(), binding.edtEmailCadastro.text.toString(), binding.edtPasswordCadastro.text.toString(), "cliente")
                }else{
                    Toast.makeText(this, "Email inválido!", Toast.LENGTH_SHORT).show()
                }
            }
        }

        binding.btnCancelaCadastro.setOnClickListener {
            finish()
        }
    }

    fun registerUser(name : String, email: String, password: String, role: String) {
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val userId = FirebaseAuth.getInstance().currentUser?.uid
                    userId?.let {
                        saveUserDataToFirestore(userId, name, email, role)
                    }
                } else {
                    Toast.makeText(this, "Usuário já cadastrado!", Toast.LENGTH_SHORT).show()
                }
            }
    }

    fun saveUserDataToFirestore(userId: String, name: String, email: String, role: String) {
        val db = FirebaseFirestore.getInstance()
        val userData = hashMapOf(
            "email" to email,
            "nome" to name,
            "role" to role
        )

        db.collection("Users").document(userId).set(userData)
            .addOnSuccessListener {
                Toast.makeText(this, "Usuário cadastrado!", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Falha ao cadastrar usuário!", Toast.LENGTH_SHORT).show()
            }
    }
}