package com.example.softwaredesign_lab2

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import kotlinx.android.synthetic.main.fragment_text_views.*
import net.objecthunter.exp4j.ExpressionBuilder

class MainActivity : AppCompatActivity() {

    var isAnswer = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        expressionTextView.showSoftInputOnFocus = false
    }

    private fun calculate(): String? {
        val txt = expressionTextView.text.toString().replace('×', '*').replace('÷', '/')
        try {
            val result = ExpressionBuilder(txt).build().evaluate()
            return "%.7f".format(result).replace(',', '.').trimEnd('0').trimEnd('.')
        } catch (ex: Exception) {
            return null
        }
    }

    private fun setAnswer() {
        answerTextView.setText(calculate() ?: "")
        isAnswer = false
    }

    fun onNumber(view: View) {
        if (isAnswer) {
            expressionTextView.text.replace(0, expressionTextView.text.length, (view as Button).text)
            setAnswer()
            return
        }
        val start =
            if (expressionTextView.isCursorVisible) expressionTextView.selectionStart else expressionTextView.text.length
        val end = if (expressionTextView.isCursorVisible) expressionTextView.selectionEnd else start
        expressionTextView.text.replace(start, end, (view as Button).text)
        setAnswer()
    }

    fun onPoint(view: View) {
        val cur =
            if (expressionTextView.isCursorVisible) expressionTextView.selectionStart else expressionTextView.text.length
        if (cur == 0 || expressionTextView.text[cur - 1] != '.') {
            expressionTextView.text.insert(cur, ".")
        }
        setAnswer()
    }

    fun onOperator(view: View) {
        var cur =
            if (expressionTextView.isCursorVisible) expressionTextView.selectionStart else expressionTextView.text.length

        val arr = when ((view as Button).text) {
            "-" -> arrayOf('+', '-')
            else -> arrayOf('+', '×', '÷', '-')
        }
        if (cur > 0 && expressionTextView.text[cur - 1] in arr) {
            onBackspace(view)
            cur -= 1
            if (cur > 0 && expressionTextView.text[cur - 1] in arr) {  // for '×-' and '÷-' situation
                onBackspace(view)
                cur -= 1
            }
        }
        if (cur > 0) {
            expressionTextView.text.insert(cur, view.text)
        }
        setAnswer()
    }

    fun onEqual(view: View) {
        val txt = calculate()
        if (txt == null) {
            answerTextView.setText(getString(R.string.error))
        } else {
            expressionTextView.setText(txt)
            expressionTextView.setSelection(expressionTextView.text.length)
            answerTextView.setText("")
            isAnswer = true
        }
    }

    fun onBackspace(view: View) {
        if (expressionTextView.hasSelection()) {
            expressionTextView.text.delete(
                expressionTextView.selectionStart,
                expressionTextView.selectionEnd
            )
        } else {
            val cur =
                if (expressionTextView.isCursorVisible) expressionTextView.selectionStart else expressionTextView.text.length
            if (cur > 0) {
                expressionTextView.text.delete(cur - 1, cur)
            }
        }
        setAnswer()
    }

    fun onBracket(view: View) {
        if (expressionTextView.hasSelection()) {
            val start = expressionTextView.selectionStart
            val end = expressionTextView.selectionEnd
            expressionTextView.text.insert(end, ")")
            expressionTextView.text.insert(start, "(")
        } else {
            val cur =
                if (expressionTextView.isCursorVisible) expressionTextView.selectionStart else expressionTextView.text.length
            expressionTextView.text.insert(cur, (view as Button).text)
        }
        setAnswer()
    }
}

