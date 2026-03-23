package com.example.myapitest.view

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.myapitest.R
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import java.util.concurrent.TimeUnit

class LoginActivity: AppCompatActivity() {

    private lateinit var etTelefone: EditText
    private lateinit var btnEnviarCodigo: Button
    private lateinit var  etCodigo: EditText
    private lateinit var btnVerificaCodigo: Button

    private lateinit var auth: FirebaseAuth
    private var storedVerificationId: String? = null
    private lateinit var resendToken: PhoneAuthProvider.ForceResendingToken

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth = FirebaseAuth.getInstance()

        if(auth.currentUser != null) {
            //usuário está logado
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        iniciandoViews()

        configurandoListeners()

        val btnSair = findViewById<Button>(R.id.btnSair)

        btnSair.setOnClickListener {
            finishAffinity()
        }

    }

    private fun iniciandoViews() {
        etTelefone = findViewById(R.id.etTelefone)
        btnEnviarCodigo = findViewById(R.id.btnEnviarCodigo)
        etCodigo = findViewById(R.id.etCodigo)
        btnVerificaCodigo = findViewById(R.id.btnVerifaCodigo)

        //Para testes 
        etTelefone.setText("+5511912345678")
        etCodigo.setText("123456")
    }

    private fun configurandoListeners() {
        btnEnviarCodigo.setOnClickListener {
            val codigoTelefone = etTelefone.text.toString()
            if(codigoTelefone.isNotEmpty()) {
                enviarCodigo(codigoTelefone)
            } else {
                Toast.makeText(this, "Digite o telefone", Toast.LENGTH_SHORT).show()
            }

        }

        btnVerificaCodigo.setOnClickListener {
            val codigo = etCodigo.text.toString()
            if (codigo.isNotEmpty()) {
                verificarCodigo(codigo)
            } else {
                Toast.makeText(this, "Digite o código", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun enviarCodigo(telefone: String) {
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(telefone)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(this)
            .setCallbacks(callbacks)
            .build()

        PhoneAuthProvider.verifyPhoneNumber(options)


        Toast.makeText(this, "Enviando seu código", Toast.LENGTH_SHORT).show()
        btnEnviarCodigo.isEnabled = false
    }

    private val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        override fun onVerificationCompleted(credential: PhoneAuthCredential) {
            signInWithPhoneAuthCredential(credential)
        }

        override fun onVerificationFailed(p0: FirebaseException) {
            btnEnviarCodigo.isEnabled = true
            Toast.makeText(this@LoginActivity, "Erro na autenticação", Toast.LENGTH_SHORT).show()
        }

        override fun onCodeSent(verificationId: String, token: PhoneAuthProvider.ForceResendingToken) {
            storedVerificationId = verificationId
            resendToken = token

            etCodigo.visibility = android.view.View.VISIBLE
            btnVerificaCodigo.visibility = android.view.View.VISIBLE
            btnEnviarCodigo.isEnabled = true

            Toast.makeText(this@LoginActivity, "Código enviado", Toast.LENGTH_SHORT).show()
        }

    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Autenticação bem sucedida", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                } else {
                    Toast.makeText(this, "Falha na autenticação", Toast.LENGTH_SHORT).show()
                    btnVerificaCodigo.isEnabled = true
                }
            }
    }

    private fun verificarCodigo(codigo: String) {
        val credential = PhoneAuthProvider.getCredential(storedVerificationId!!, codigo)
        signInWithPhoneAuthCredential(credential)
    }
}

