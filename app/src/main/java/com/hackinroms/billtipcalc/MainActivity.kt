package com.hackinroms.billtipcalc

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Slider
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.text.isDigitsOnly
import com.hackinroms.billtipcalc.components.InputField
import com.hackinroms.billtipcalc.ui.theme.BillTipCalcTheme
import com.hackinroms.billtipcalc.util.calculateTotalPerPerson
import com.hackinroms.billtipcalc.util.calculateTotalTip
import com.hackinroms.billtipcalc.widgets.RoundIconButton

val TAG: String = MainActivity::class.java.simpleName

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContent {
      MyApp {
        MainContent()
      }
    }
  }
}

@Composable
fun MyApp(content: @Composable () -> Unit) {
  BillTipCalcTheme {
    // A surface container using the 'background' color from the theme
    Surface(
      modifier = Modifier.fillMaxSize(), color = MaterialTheme.colors.background
    ) {
      content()
    }
  }
}

@Composable
fun TopHeader(totalPerPerson: Double = 120.0) {
  Surface(
    modifier = Modifier
      .padding(10.dp)
      .fillMaxWidth()
      .height(150.dp)
      //.clip(shape = CircleShape.copy(all = CornerSize(12.dp)))
      .clip(shape = RoundedCornerShape(corner = CornerSize(8.dp))), color = Color(0x209C27B0)
  ) {
    Column(
      modifier = Modifier.padding(15.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center
    ) {
      val total = "%.2f".format(totalPerPerson)

      Text(
        text = "Total Per Person", style = MaterialTheme.typography.h5
      )

      Text(
        text = "$$total", style = MaterialTheme.typography.h4, fontWeight = FontWeight.ExtraBold
      )
    }
  }
}

@Preview(showBackground = true)
@Composable
fun MainContent() {
  val numberOfPersons = remember {
    mutableStateOf(1)
  }

  val totalPerPerson = remember {
    mutableStateOf(0.0)
  }

  val tipAmount = remember {
    mutableStateOf(0.0)
  }
  Column(modifier = Modifier.padding(all = 12.dp)) {
    BillForm(
      modifier = Modifier,
      numberOfPersons = numberOfPersons,
      totalPerPerson = totalPerPerson,
      tipAmount = tipAmount
    ) { billAmt ->
      Log.d(TAG, "Bill Amount: $billAmt")
    }
  }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun BillForm(
  modifier: Modifier = Modifier,
  numberOfPersons: MutableState<Int>,
  tipAmount: MutableState<Double>,
  totalPerPerson: MutableState<Double>,
  onValChange: (String) -> Unit = {}
) {

  val totalBillState = remember {
    mutableStateOf("")
  }

  val validState = remember(totalBillState.value) {
    totalBillState.value.trim().isNotEmpty() && totalBillState.value.isDigitsOnly()
  }

  val keyboardController = LocalSoftwareKeyboardController.current

  val sliderPosition = remember {
    mutableStateOf(0f)
  }
  val tipPercentage = (sliderPosition.value * 100).toInt()

  TopHeader(totalPerPerson = totalPerPerson.value)

  Surface(
    modifier = modifier
      .padding(2.dp)
      .fillMaxWidth(),
    shape = RoundedCornerShape(corner = CornerSize(8.dp)),
    border = BorderStroke(width = 1.dp, color = Color(0xFFE0E0E0))
  ) {
    Column(
      modifier = modifier.padding(6.dp),
      verticalArrangement = Arrangement.Top,
      horizontalAlignment = Alignment.Start
    ) {
      InputField(
        valueState = totalBillState,
        labelId = "Enter Bill",
        enabled = true,
        isSingleLine = true,
        onAction = KeyboardActions {
          if (!validState) return@KeyboardActions

          onValChange(totalBillState.value.trim())

          totalPerPerson.value = calculateTotalPerPerson(
            totalBill = totalBillState.value.toDouble(),
            tipPercentage = tipPercentage,
            numberOfPersons = numberOfPersons.value
          )

          tipAmount.value = calculateTotalTip(
            totalBill = totalBillState.value.toDouble(),
            tipPercentage = tipPercentage
          )

          keyboardController?.hide()
        })

      if (validState) {
      // Split Row
      Row(
        modifier = modifier
          .padding(3.dp)
          .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        Text(
          text = "Split", modifier = modifier.align(alignment = Alignment.CenterVertically)
        )

        Spacer(modifier = modifier.width(120.dp))

        Row(
          modifier = modifier.padding(horizontal = 3.dp), horizontalArrangement = Arrangement.End
        ) {
          RoundIconButton(imageVector = Icons.Default.Remove, onClick = {
            if (numberOfPersons.value > 1) {
              numberOfPersons.value--

              totalPerPerson.value = calculateTotalPerPerson(
                totalBill = totalBillState.value.toDouble(),
                tipPercentage = tipPercentage,
                numberOfPersons = numberOfPersons.value
              )
            }
          })

          Text(
            text = "${numberOfPersons.value}",
            modifier = modifier
              .align(alignment = Alignment.CenterVertically)
              .padding(start = 9.dp, end = 9.dp)
          )

          RoundIconButton(imageVector = Icons.Default.Add, onClick = {
            if (numberOfPersons.value < 50) {
              numberOfPersons.value++

              totalPerPerson.value = calculateTotalPerPerson(
                totalBill = totalBillState.value.toDouble(),
                tipPercentage = tipPercentage,
                numberOfPersons = numberOfPersons.value
              )
            }
          })
        }
      }

      // Tip Row
      Row(
        modifier = modifier
          .padding(horizontal = 3.dp, vertical = 12.dp)
          .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        Text(
          text = "Tip", modifier = modifier.align(alignment = Alignment.CenterVertically)
        )
        Spacer(modifier = modifier.width(200.dp))

        Text(
          text = "$ ${tipAmount.value}",
          modifier = modifier
            .align(alignment = Alignment.CenterVertically)
            .padding(end = 20.dp)
        )
      }

      // Percentage Column
      Column(
        verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally
      ) {

        Text(
          text = "$tipPercentage%",
        )

        Spacer(modifier = modifier.height(14.dp))

        Slider(
          value = sliderPosition.value, onValueChange = { newVal ->
            sliderPosition.value = newVal
            tipAmount.value = calculateTotalTip(
              totalBill = totalBillState.value.toDouble(),
              tipPercentage = tipPercentage
            )
            totalPerPerson.value = calculateTotalPerPerson(
              totalBill = totalBillState.value.toDouble(),
              tipPercentage = tipPercentage,
              numberOfPersons = numberOfPersons.value
            )
          },
          modifier = modifier.padding(start = 16.dp, end = 16.dp)
        )
      }
      } else {
        Box() {}
      }

    }
  }

}


@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
  MyApp {
    TopHeader()
    //MainContent()
  }
}