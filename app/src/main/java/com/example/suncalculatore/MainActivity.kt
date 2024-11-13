package com.example.suncalculatore
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlin.math.PI
import kotlin.math.exp
import kotlin.math.pow
import kotlin.math.sqrt

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val avgPowerInput = findViewById<EditText>(R.id.avg_power)
        val sigma1Input = findViewById<EditText>(R.id.sigma1)
        val sigma2Input = findViewById<EditText>(R.id.sigma2)
        val electricityPriceInput = findViewById<EditText>(R.id.electricity_price)
        val calculateButton = findViewById<Button>(R.id.calculate_button)
        val resultText = findViewById<TextView>(R.id.result_text)

        calculateButton.setOnClickListener {
            val avgPower = avgPowerInput.text.toString().toDoubleOrNull() ?: 0.0
            val sigma1 = sigma1Input.text.toString().toDoubleOrNull() ?: 0.0
            val sigma2 = sigma2Input.text.toString().toDoubleOrNull() ?: 0.0
            val electricityPrice = electricityPriceInput.text.toString().toDoubleOrNull() ?: 0.0

            // Виклик функції для розрахунку прибутку
            val profit = calculateProfit(avgPower, sigma1, sigma2, electricityPrice)
            resultText.text = "Прибуток: ${String.format("%.1f", profit)} тис. грн"
        }
    }

    private fun calculateProfit(avgPower: Double, sigma1: Double, sigma2: Double, electricityPrice: Double): Double {
        val Pc = avgPower
        val B = electricityPrice

        val deltaW1 = integrate(
            { x, pc, sigma1 -> calculatePdW(x, pc, sigma1) },
            4.75, 5.25, 1000, Pc, sigma1
        )

        val deltaW2 = integrate(
            { x, pc, sigma2 -> calculatePdW(x, pc, sigma2) },
            4.75, 5.25, 1000, Pc, sigma2
        )

        val W3 = Pc * 24 * deltaW2
        val P2 = W3 * B
        val W4 = Pc * 24 * (1 - deltaW2)
        val Sh2 = W4 * B
        val totalProfit = P2 - Sh2

        return totalProfit
    }

    // Функція інтегрування
    private fun integrate(
        func: (Double, Double, Double) -> Double,
        start: Double,
        end: Double,
        steps: Int,
        Pc: Double,
        sigma: Double
    ): Double {
        val step = (end - start) / steps
        var sum = 0.5 * (func(start, Pc, sigma) + func(end, Pc, sigma))

        var x = start + step
        while (x < end) {
            sum += func(x, Pc, sigma)
            x += step
        }

        return sum * step
    }

    // Функція для розрахунку нормального розподілу
    private fun calculatePdW(p: Double, Pc: Double, sigma: Double): Double {
        return (1 / (sigma * sqrt(2 * PI))) * exp(-(p - Pc).pow(2) / (2 * sigma.pow(2)))
    }
}

