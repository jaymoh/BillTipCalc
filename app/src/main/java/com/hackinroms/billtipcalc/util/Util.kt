package com.hackinroms.billtipcalc.util

fun calculateTotalTip(totalBill: Double, tipPercentage: Int): Double {
  return if (totalBill > 1 && totalBill.toString().isNotEmpty())
    (totalBill * tipPercentage) / 100
  else 0.0
}

fun calculateTotalPerPerson(totalBill: Double, tipPercentage: Int, numberOfPersons: Int): Double {
  return if (totalBill > 1 && totalBill.toString().isNotEmpty())
    (totalBill + calculateTotalTip(totalBill, tipPercentage)) / numberOfPersons
  else 0.0
}