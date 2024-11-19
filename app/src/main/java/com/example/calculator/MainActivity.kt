package com.example.calculator

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.calculator.ui.theme.CalculatorTheme
import net.objecthunter.exp4j.ExpressionBuilder

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CalculatorTheme {
                // A surface container using the 'background' color from the theme
//                val systemUiController = rememberSystemUiController()
//                systemUiController.setStatusBarColor(
//                    color = Color(0xFFFF9500)
//                )
                Surface(
                    modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
                ) {
                    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                        CalculatorApp()
                    }
                }
            }
        }
    }
}


@Composable
fun CalculatorApp() {
    var displayText by remember { mutableStateOf("0") }
    var resultText by remember { mutableStateOf("") }

//    val backgroundColor = MaterialTheme.colorScheme.background
    val backgroundColor = Color.White


    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(backgroundColor)
            .padding(bottom = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Bottom
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1F),
            verticalArrangement = Arrangement.Bottom,
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(3f), horizontalAlignment = Alignment.Start
                ) {
                    Text(
                        text = displayText.replace("*", "×").replace("/", "÷"),
                        color = Color.Black,
                        fontSize = 40.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp,
                        textAlign = TextAlign.Start,
                        lineHeight = 40.sp,
                        maxLines = 5
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = resultText,
                            color = Color.Gray,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Normal,
                            letterSpacing = 1.sp,
                            textAlign = TextAlign.Start,
                            lineHeight = 28.sp,
                            maxLines = 1
                        )
                        Icon(painter = painterResource(id = R.drawable.icon_clear),
                            contentDescription = null,
                            tint = Color.Gray,
                            modifier = Modifier
                                .size(32.dp)
                                .clickable {
                                    displayText =
                                        if (displayText.length > 1) displayText.dropLast(1) else "0"
                                }
                        )
                    }

                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // هنا بنستدعي دالة CalculatorLayout ونعرف الدوال اللي بتشتغل لما المستخدم يضغط على الأزرار
        CalculatorLayout(
            //  onDigitClick: لو النص الحالي "0"، بيبدله بالرقم المدخل، لو لأ بيضيف الرقم للنص
            onDigitClick = { number ->
                if (displayText == "0") {
                    displayText = number
                } else {
                    displayText += number
                }
            },
            // onOperatorClick: لو آخر حرف رقم أو نقطة أو قوس، بيضيف العملية للنص، لو لأ بيبدل العملية القديمة بالجديدة
            onOperatorClick = { operator ->
                if (displayText.last()
                        .isDigit() || displayText.last() == '.' || displayText.last() == ')'
                ) {
                    displayText += operator
                } else {
                    displayText = displayText.dropLast(1) + operator
                }
            },
            onClearClick = {
                displayText = "0"
                resultText = ""
            },
            // onEqualClick: resultText ويعرضها في calculateResult بيحسب النتيجة باستخدام دالة
            onEqualClick = {
                resultText = try {
                    calculateResult(displayText).toString()
                } catch (e: Exception) {
                    "Error"
                }
            },
            // بيضيف نقطة للنص لو مفيش نقطة قبل كده
            onDotClick = {
                if (!displayText.contains(".")) {
                    displayText += "."
                }
            },
            onLeftParenthesisClick = {
                if (displayText == "0") {
                    displayText = "("
                } else {
                    displayText += "("
                }
            },
            onRightParenthesisClick = {
                if (displayText != "0") {
                    displayText += ")"
                }
            }
        )
    }
}

@Composable
fun CalculatorLayout(
    onDigitClick: (String) -> Unit,
    onOperatorClick: (String) -> Unit,
    onClearClick: () -> Unit,
    onEqualClick: () -> Unit,
    onDotClick: () -> Unit,
    onLeftParenthesisClick: () -> Unit,
    onRightParenthesisClick: () -> Unit,
) {
    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            CalculatorButton(
                text = "C",
                onClick = onClearClick,
                color = Color(0xFFFF9500)
            )
            CalculatorButton(
                text = "(",
                onClick = onLeftParenthesisClick,
                color = Color(0xFFFF9500)
            )
            CalculatorButton(
                text = ")",
                onClick = onRightParenthesisClick,
                color = Color(0xFFFF9500)
            )
            CalculatorButton(
                text = "÷",
                onClick = { onOperatorClick("/") },
                color = Color(0xFFFF9500)
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            CalculatorButton(text = "7", onClick = { onDigitClick("7") }, color = Color.LightGray)
            CalculatorButton(text = "8", onClick = { onDigitClick("8") }, color = Color.LightGray)
            CalculatorButton(text = "9", onClick = { onDigitClick("9") }, color = Color.LightGray)
            CalculatorButton(text = "×", onClick = { onOperatorClick("*") }, Color(0xFFFF9500))
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            CalculatorButton(text = "4", onClick = { onDigitClick("4") }, color = Color.LightGray)
            CalculatorButton(text = "5", onClick = { onDigitClick("5") }, color = Color.LightGray)
            CalculatorButton(text = "6", onClick = { onDigitClick("6") }, color = Color.LightGray)
            CalculatorButton(text = "-", onClick = { onOperatorClick("-") }, Color(0xFFFF9500))
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            CalculatorButton(text = "1", onClick = { onDigitClick("1") }, color = Color.LightGray)
            CalculatorButton(text = "2", onClick = { onDigitClick("2") }, color = Color.LightGray)
            CalculatorButton(text = "3", onClick = { onDigitClick("3") }, color = Color.LightGray)
            CalculatorButton(text = "+", onClick = { onOperatorClick("+") }, Color(0xFFFF9500))
        }

        Row(
            modifier = Modifier.fillMaxWidth(),


            ) {
            CalculatorButton(
                text = "0",
                onClick = { onDigitClick("0") },
                color = Color.LightGray,
                modifier = Modifier.weight(2f)
            )
            CalculatorButton(
                text = ".",
                onClick = onDotClick,
                color = Color.LightGray
            )
            CalculatorButton(
                text = "=",
                onClick = onEqualClick,
                Color(0xFFFF9500)
            )
        }
    }
}

// دي دالة بتحدد شكل الزر وإيه اللي يحصل لما نضغط عليه
@Composable
fun CalculatorButton(
    text: String,
    onClick: () -> Unit,
    color: Color,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .clickable { onClick() }
            .padding(8.dp)
            .size(65.dp)
            .background(color, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = Color.White,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
        )
    }
}

fun calculateResult(expression: String): Double {
    return try {
        val result = ExpressionBuilder(expression).build().evaluate()
        result
    } catch (e: Exception) {
        Double.NaN
    }
}

@Preview
@Composable
fun CalculatorAppPreview() {
    CalculatorTheme {
        CalculatorApp()
    }

}
