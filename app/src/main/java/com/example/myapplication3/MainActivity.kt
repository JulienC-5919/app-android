package com.example.myapplication3

import android.R.attr.top
import android.graphics.Color as AndroidColor
import android.icu.text.DecimalFormat
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.ui.Alignment
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.myapplication3.ui.theme.MyApplication3Theme
import java.text.NumberFormat
import java.util.Locale
import kotlin.math.round
import kotlin.math.roundToInt
import kotlin.text.toFloatOrNull


const val taxes = 0.14975f
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(AndroidColor.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(AndroidColor.TRANSPARENT)
        )
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable

fun Greeting(name: String, modifier: Modifier = Modifier) {
    var amountStr by remember { mutableStateOf("") }
    var amount by remember {mutableFloatStateOf(0.0f)}
    var applyTaxes by remember { mutableStateOf(true) }
    var tipPercentage by remember { mutableFloatStateOf(0.1f) }
    var tipAmount by remember {mutableFloatStateOf(0.0f)}

    var people by remember { mutableIntStateOf(1) }

    val locale = Locale.getDefault()
    val currencyFormat = NumberFormat.getCurrencyInstance(locale)
    val currencySymbol = currencyFormat.currency?.getSymbol(locale) ?: ""

    // Détermine si le symbole doit être devant ou derrière selon la locale
    val isSymbolPrefix = currencyFormat.format(0.0).trim().startsWith(currencySymbol)

    val percentage = DecimalFormat("#%")

    val decimalSeparator = java.text.DecimalFormatSymbols.getInstance(locale).decimalSeparator

    var showMenu by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.app_title)) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF4B0082),
                    titleContentColor = Color.White,
                    actionIconContentColor = Color.White,
                    navigationIconContentColor = Color.White
                ),
                actions = {
                    Box {
                        IconButton(onClick = { showMenu = true }) {
                            Icon(Icons.Default.Menu, contentDescription = "Menu")
                        }
                        DropdownMenu(
                            expanded = showMenu,
                            onDismissRequest = { showMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Option 1") },
                                onClick = { showMenu = false }
                            )
                            DropdownMenuItem(
                                text = { Text("Option 2") },
                                onClick = { showMenu = false }
                            )
                        }
                    }
                }
            )
        },
        bottomBar = {
            val totalWithTaxes = (amount + tipAmount) * ( 1f + if (applyTaxes) taxes else 0f)
            BottomAppBar(
                containerColor = Color(0xFF4B0082),
                contentColor = Color.White
            ) {

                Column(Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(stringResource(R.string.total))
                        Text(currencyFormat.format(totalWithTaxes))
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(stringResource(R.string.total_per_person))
                        Text(currencyFormat.format(totalWithTaxes / people))
                    }
                }
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    amountStr = ""
                    amount = 0f
                    applyTaxes = true
                    tipPercentage = 0.1f
                    tipAmount = 0f
                },
                containerColor = Color(0xFF4B0082),
                contentColor = Color.White
            ) { Text(stringResource(R.string.clear)) }
        }
    ) {

        innerPadding -> Column(modifier = modifier
        .padding(innerPadding)
        .padding(16.dp)) {
        Text(stringResource(R.string.bill_amount))
        OutlinedTextField(
            value = amountStr,
            onValueChange = {

                amountStr = cleanNumber(it, decimalSeparator)
                
                val conv = toFloat(amountStr)

                if (conv != null) {
                    amount = conv
                    tipAmount = round((conv * tipPercentage) * 100) / 100
                }

            },
            label = { Text(stringResource(R.string.amount)) },
            prefix = if (isSymbolPrefix) { { Text(currencySymbol) } } else null,
            suffix = if (!isSymbolPrefix) { { Text(currencySymbol) } } else null,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            modifier = Modifier.fillMaxWidth()
        )

        HorizontalDivider( modifier = Modifier.padding(top = 10.dp, bottom = 20.dp) )

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.apply_taxes),
                modifier = Modifier.weight(1f)
            )
            Switch(
                checked = applyTaxes,
                onCheckedChange = { applyTaxes = it }
            )
        }

        HorizontalDivider( modifier = Modifier.padding(top = 10.dp, bottom = 20.dp) )

        Text(
            text= stringResource(R.string.tip) + percentage.format(tipPercentage)
        )
        Slider(
            value = tipPercentage,
            onValueChange = {
                tipPercentage = round(it*100)/100

                tipAmount = round(amount * tipPercentage*100)/100

                            },
            valueRange = 0.05f..0.2f
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(text = "5 %", modifier = Modifier.weight(1f))
            Text(text = "10 %", modifier = Modifier.weight(1f))
            Text(text = "15 %", modifier = Modifier.weight(1f))
            Text(text = "20 %")
        }

        Spacer(
            modifier = Modifier.height(15.dp)
        )

        Text(stringResource(R.string.tip_amount))
        OutlinedTextField(
            value = currencyFormat.format(tipAmount),
            readOnly = true,
            onValueChange = {},
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            modifier = Modifier.fillMaxWidth()
        )

        HorizontalDivider( modifier = Modifier.padding(top = 10.dp, bottom = 20.dp) )

        Text(stringResource(R.string.number_of_people) + people)
        Slider(
            value = people.toFloat(),
            onValueChange = {
                people = it.roundToInt()
            },
            valueRange = 1f..10f
        )
        Row(
            modifier = Modifier.fillMaxWidth()

        ) {
            Text(text = "1", modifier = Modifier.weight(4f))
            Text(text = "5", modifier = Modifier.weight(5f))
            Text(text = "10")
        }
    }
}}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    MyApplication3Theme {
        Greeting("Android")
    }
}

fun cleanNumber(num : String, decimalSeparator: Char): String {

    val otherSeparator = if (decimalSeparator == '.') ',' else '.'

    // Nettoyer l'entrée : garder chiffres et séparateurs, supprimer le signe moins
    var cleaned = num.filter { it.isDigit() || it == '.' || it == ',' }

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
    return cleaned
}

fun toFloat(num: String): Float? {
    return num
        .replace(',', '.')
        .toFloatOrNull()
}