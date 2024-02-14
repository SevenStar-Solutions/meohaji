package com.example.meohaji

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.meohaji.databinding.ActivityNetworkCheckBinding
import com.example.meohaji.main.MainActivity

class NetworkCheckActivity : AppCompatActivity() {

    private val binding: ActivityNetworkCheckBinding by lazy {
        ActivityNetworkCheckBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        if (NetworkStatus.getConnectivityStatus(this) != NetworkStatus.TYPE_NOT_CONNECTED) {
            startActivity(Intent(this@NetworkCheckActivity, MainActivity::class.java))
            finish()
        }

        binding.btnNetworkCheck.setOnClickListener {
            if (NetworkStatus.getConnectivityStatus(this) != NetworkStatus.TYPE_NOT_CONNECTED) {
                startActivity(Intent(this@NetworkCheckActivity, MainActivity::class.java))
                finish()
            } else {
                Toast.makeText(this@NetworkCheckActivity, "네트워크 연결을 다시 확인해주세요", Toast.LENGTH_SHORT).show()
            }
        }
    }
}