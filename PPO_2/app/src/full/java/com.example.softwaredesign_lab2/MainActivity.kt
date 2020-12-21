package com.example.softwaredesign_lab2

import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import kotlinx.android.synthetic.main.fragment_basic.*
import kotlinx.android.synthetic.main.fragment_text_views.*
import net.objecthunter.exp4j.ExpressionBuilder
import net.objecthunter.exp4j.function.Function
import net.objecthunter.exp4j.operator.Operator
import kotlin.random.Random

class MainActivity : AppCompatActivity() {

    private var isAnswer = true
    private var functionButtons = mapOf(
        R.id.buttonSin to "sin(",
        R.id.buttonCos to "cos(",
        R.id.buttonTan to "tan(",
        R.id.buttonLog2 to "log2(",
        R.id.buttonLog10 to "log10(",
        R.id.buttonFloor to "floor(",
        R.id.buttonLn to "ln(",
        R.id.buttonMod to "%",
        R.id.buttonPi to "pi",
        R.id.buttonSqrt to "sqrt(",
        R.id.buttonCbrt to "cbrt(",
        R.id.buttonRand to "rand("
    )
    private var rand0 = object : Function("rand0", 0) {
        override fun apply(vararg args: Double) = Random.nextDouble()
    }
    private var rand1 = object : Function("rand", 1) {
        override fun apply(vararg args: Double) = Random.nextDouble(args[0])
    }
    private var factorial =
        object : Operator("!", 1, true, PRECEDENCE_POWER + 1) {
            override fun apply(vararg args: Double): Double {
                val n = args[0].toInt()
                require(n.toDouble() == args[0]) { "Operand for factorial has to be an integer" }
                require(n >= 0) { "The operand of the factorial can not be less than zero" }
                return (1..n).takeIf { !it.isEmpty() }?.reduce { fact, i -> fact * i }?.toDouble() ?: 1.0
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        expressionTextView.showSoftInputOnFocus = false
        expressionTextView.doAfterTextChanged {
            answerTextView.setText(calculate() ?: "")
            isAnswer = false
        }
        buttonBackspace.setOnLongClickListener(::onBackspaceLong)
    }

    private fun calculate(): String? {
        val txt = expressionTextView.text.toString()
            .replace('×', '*')
            .replace('÷', '/')
            .replace("ln", "log")
            .replace("rand()", "rand0()")
        return try {
            val result = ExpressionBuilder(txt).functions(rand0, rand1).operator(factorial).build()
                .evaluate()
            "%.7f".format(result).replace(',', '.').trimEnd('0').trimEnd('.')
        } catch (ex: Exception) {
            null
        }
    }

    fun onNumber(view: View) {
        if (isAnswer) {
            expressionTextView.setText((view as Button).text)
            expressionTextView.setSelection(expressionTextView.text.length)
        } else {
            val start = expressionTextView.selectionStart
            val end = expressionTextView.selectionEnd
            expressionTextView.text.replace(start, end, (view as Button).text)
        }
    }

    fun onPoint(view: View) {
        val cur = expressionTextView.selectionStart
        if (cur == 0 || expressionTextView.text[cur - 1] != '.') {
            expressionTextView.text.insert(cur, ".")
        }
    }

    fun onOperator(view: View) {
        var cur = expressionTextView.selectionStart

        val arr = when ((view as Button).text) {
            "-" -> arrayOf('+', '-')
            else -> arrayOf('+', '×', '÷', '-')
        }
        if (cur > 0 && expressionTextView.text[cur - 1] in arr) {
            onBackspace(view)
            --cur
            if (cur > 0 && expressionTextView.text[cur - 1] in arr) {  // for '×-' and '÷-' situation
                onBackspace(view)
                --cur
            }
        }
        if (cur > 0 || view.text == "-") {
            expressionTextView.text.insert(cur, view.text)
        }
    }

    fun onEqual(view: View) {
        if (isAnswer) return
        if (answerTextView.text.toString() != "" && answerTextView.text.toString() != getString(R.string.error)) {
            expressionTextView.text = answerTextView.text
            expressionTextView.setSelection(expressionTextView.text.length)
            answerTextView.setText("")
            isAnswer = true
        } else answerTextView.setText(getString(R.string.error))
    }

    fun onBracket(view: View) {
        if (expressionTextView.hasSelection()) {
            val start = expressionTextView.selectionStart
            val end = expressionTextView.selectionEnd
            expressionTextView.text.insert(end, ")")
            expressionTextView.text.insert(start, "(")
            expressionTextView.setSelection(start, end + 2)
        } else {
            expressionTextView.text.insert(expressionTextView.selectionStart, (view as Button).text)
        }
    }

    fun onFunction(view: View) {
        if (isAnswer && view.id !in arrayOf(R.id.buttonFactorial, R.id.buttonMod, R.id.buttonPower)) {
            expressionTextView.text.clear()
        }
        expressionTextView.text.insert(
            expressionTextView.selectionStart,
            functionButtons.getOrElse(view.id) { (view as Button).text }
        )
    }

    private fun onBackspaceLong(view: View): Boolean {
        expressionTextView.text.clear()
        return true
    }

    fun onBackspace(view: View) {
        if (expressionTextView.hasSelection()) {
            expressionTextView.text.delete(
                expressionTextView.selectionStart,
                expressionTextView.selectionEnd
            )
        } else if (expressionTextView.selectionStart != 0) {
            val prefix = expressionTextView.text.substring(0, expressionTextView.selectionStart)
            functionButtons.values.find { prefix.endsWith(it) }?.let {
                expressionTextView.text.delete(expressionTextView.selectionStart - it.length, expressionTextView.selectionStart)
            } ?: expressionTextView.text.delete(expressionTextView.selectionStart - 1, expressionTextView.selectionStart)
        }
    }
}
