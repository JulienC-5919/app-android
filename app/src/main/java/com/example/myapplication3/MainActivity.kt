package com.example.myapplication3

import android.icu.text.DecimalFormat
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.myapplication3.ui.theme.MyApplication3Theme
import java.text.NumberFormat
import java.util.Locale
import kotlin.math.round

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplication3Theme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        name = "Android",
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    var amountStr by remember { mutableStateOf("") }
    var amount by remember {mutableFloatStateOf(0.0f)}
    var applyTaxes by remember { mutableStateOf(true) }
    var tipPercentage by remember { mutableFloatStateOf(0.1f) }
    var tipAmount by remember {mutableFloatStateOf(0.0f)}
    var tipAmountStr by remember { mutableStateOf("") }

    val locale = Locale.getDefault()
    val currencyFormat = NumberFormat.getCurrencyInstance(locale)
    val currencySymbol = currencyFormat.currency?.getSymbol(locale) ?: ""

    // Détermine si le symbole doit être devant ou derrière selon la locale
    val isSymbolPrefix = currencyFormat.format(0.0).trim().startsWith(currencySymbol)

    val percentage: DecimalFormat = DecimalFormat("#%")

    Column(modifier = modifier.padding(16.dp)) {
        Text(
            text = "Montant de l'addition"
        )
        OutlinedTextField(
            value = amountStr,
            onValueChange = {

                val decimalSeparator = java.text.DecimalFormatSymbols.getInstance(locale).decimalSeparator

                amountStr = cleanNumber(amountStr, decimalSeparator)
                
                // Conversion en float (nécessite le point comme séparateur)
                val amountForConv = amountStr.replace(',', '.')
                val conv = amountForConv.toFloatOrNull()

                if (conv != null) {
                    amount = conv
                    tipAmount = round((conv * tipPercentage) * 100) / 100
                    tipAmountStr = tipAmount.toString().replace('.', decimalSeparator)
                }

            },
            label = { Text("Montant") },
            prefix = if (isSymbolPrefix) { { Text(currencySymbol) } } else null,
            suffix = if (!isSymbolPrefix) { { Text(currencySymbol) } } else null,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            modifier = Modifier.padding(top = 8.dp)
        )
        Row() {
            Text(
                text = "Ajouter les taxes (14.975%)"
            )
            Switch(
                checked = applyTaxes,
                onCheckedChange = { applyTaxes = it }
            )
        }
        Text(
            text="Pourboire : " + percentage.format(tipPercentage)
        )
        Slider(
            value = tipPercentage,
            onValueChange = {
                tipPercentage = it
                            },
            valueRange = 0.05f..0.2f
        )
        Row() {
            Text(text = "5 %")
            Text(text = "10 %")
            Text(text = "20 %")
        }
        Text(text = "Montant du pourboire")
        OutlinedTextField(
            value = tipAmountStr,
            onValueChange = { tipAmountStr = it },
            label = { Text("Montant") },
            prefix = if (isSymbolPrefix) { { Text(currencySymbol) } } else null,
            suffix = if (!isSymbolPrefix) { { Text(currencySymbol) } } else null,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            modifier = Modifier.padding(top = 8.dp)
        )

    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    MyApplication3Theme {
        Greeting("Android")
    }
}

fun cleanNumber(num : String, decimalSeparator: char): String {

    val otherSeparator = if (decimalSeparator == '.') ',' else '.'

    // Nettoyer l'entrée : garder chiffres et séparateurs, supprimer le signe moins
    var cleaned = it.filter { it.isDigit() || it == '.' || it == ',' }

    // Remplacer le séparateur incorrect par le bon selon la langue
    cleaned = cleaned.replace(otherSeparator, decimalSeparator)

    // Garder seulement le premier séparateur et 2 chiffres après
    val firstSeparatorIndex = cleaned.indexOf(decimalSeparator)
    if (firstSeparatorIndex != -1) {
        val before = cleaned.substring(0, firstSeparatorIndex + 1)
        val after = cleaned.substring(firstSeparatorIndex + 1).replace(decimalSeparator.toString(), "")
        val truncatedAfter = if (after.length > 2) after.substring(0, 2) else after
        return before + truncatedAfter
    }
}