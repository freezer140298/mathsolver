package com.freezer.mathkeyboard

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.util.SparseArray
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import io.github.kexanie.library.MathView

class MathKeyboard(context: Context, atrs: AttributeSet?) : LinearLayout(context, atrs, 0), View.OnClickListener {
    private val TAG = "MathKeyboard"
    private var mvFormula: MathView? = null

    // Lists to store symbols left and right of the cursor
    private var left = ArrayList<String>()
    private var right = ArrayList<String>()

    // matches corresponding ID - Key Symbol pairs
    private val keyValues = SparseArray<String>()

    // Grouping of symbols with similar insertion rules
    var symRoundBracket = arrayOf("sin(", "cos(", "ln(", "\\sqrt(", "(", "log_{")
    var symCurlyBracket = arrayOf("\\sqrt{", "^{", "\\int_{", "\\sum_{", "\\prod_{", "\\sqrt[")
    var symClosing = arrayOf(")", "}", "}{", "}(", "}^{")
    var symOpening = arrayOf("sin(", "cos(", "ln(", "\\sqrt(", "(", "log_{", "\\sqrt{", "}{", "}(", "^{", "", "\\int_{", "\\sum_{", "\\prod_{", "\\sqrt[", "}^{") // all symbols that open a new expression
    var symNumbers = arrayOf("0", "1", "2", "3", "4", "5", "6", "7", "8", "9")
    var symArithmeticWoMinus = arrayOf("+", "*", "/",  "^{")
    var symSigns = arrayOf("+", "-", "*", "/")

    fun setView(view_display: MathView?) {
        // The display view is used to show the formula
        mvFormula = view_display
        // Show initial text ( \\, as white space in latex)
        mvFormula!!.text = "$\$Enter\\,a\\,formula$$"
    }

    private fun initializeKeyboard(context: Context) {
        // In-app keyboard is connected to values
        LayoutInflater.from(context).inflate(R.layout.keyboard, this, true)

        // IDs and corresponding key values in the order of appearance in the app
        val ids = intArrayOf(
            R.id.bt_plus, R.id.bt1, R.id.bt2, R.id.bt3, R.id.bt_klL, R.id.bt_klR, R.id.bt_ac,
            R.id.bt_minus, R.id.bt4, R.id.bt5, R.id.bt6, R.id.bt_ln, R.id.bt_log, R.id.bt_del,
            R.id.bt_mult, R.id.bt7, R.id.bt8, R.id.bt9, R.id.bt_root, R.id.bt_pot, R.id.bt_sin,
            R.id.bt_div, R.id.bt_dec, R.id.bt0, R.id.btX, R.id.bt_down, R.id.bte, R.id.bt_cos,
            R.id.bt_equal, R.id.bt_less, R.id.bt_more, R.id.bt_y, R.id.bt_percent, R.id.bt_abs, R.id.bt_integral,
            R.id.bt_not_equal, R.id.bt_pi, R.id.bt_inf, R.id.bt_z, R.id.bt_sum, R.id.bt_product, R.id.bt_nth_root
        )
        val values = arrayOf(
            "+", "1", "2", "3", "(", ")", "",
            "-", "4", "5", "6", "ln(", "log_{", "",
            "*", "7", "8", "9", "\\sqrt{", "^{", "sin(",
            "/", ".", "0", "x", "}", "e", "cos(",
            "=", "<", ">" , "y", "\\%", "\\mid", "\\int_{",
            "\\neq", "\\pi", "\\infty", "z", "\\sum_{", "\\prod_{", "\\sqrt["
        )
        for (i in ids.indices) {
            initializeButton(ids[i], values[i])
        }
    }

    private fun initializeButton(id: Int, value: String) {
        // Makes individual button clickable and connected to its value
        val btn = findViewById<Button>(id)
        btn.setOnClickListener(this)
        keyValues.put(id, value)
    }

    private fun resetText() {
        // Delete all written input
        left = ArrayList()
        right = ArrayList()
    }

    private fun check() {
        // Fill in here what to do, if the check button is clicked
        // e.g. Calculate the result or reset the formula
        resetText()
    }

    private fun delete() {
        // Deletes the last symbol from the LaTex text if it is possible.
        if (left.size == 0) {
            return
        }
        val lastItem = left[left.size - 1]
        left.removeAt(left.size - 1)
        val lastChar = lastItem[lastItem.length - 1]

        // closing brackets and brackets in logarithms or fractions are not deleted
        // instead the cursor is shifted to the left
        if (lastItem == "}{" || lastItem == "}(" || lastChar == '}' || lastChar == ')') {
            right.add(lastItem)
        } else if (lastItem == "log_{" || lastItem == "\\int_{" || lastItem == "\\sum_{" || lastItem == "\\prod_{") {
            right.removeAt(right.size - 1)
            if(right.size != 0)
                right.removeAt(right.size - 1)
        } else if (lastChar == '{' || lastChar == '(') {
            right.removeAt(right.size - 1)
        }
    }

    private fun isInsertionViolations(symbol: String): Boolean {
        // checks for input symbols incompatible with the current formula
        var lastSymbol = ""
        var nextSymbol = ""
        if (left.size > 0) {
            lastSymbol = left[left.size - 1]
        }
        if (right.size > 0) {
            nextSymbol = right[right.size - 1]
        }
        if (isContained(lastSymbol, symOpening) && isContained(symbol, symArithmeticWoMinus)
        ) {
            return true // no arithmetic sign after opening an expression. e.g. (*
        }
        if (!isContained(lastSymbol, symNumbers) && symbol == ".") {
            return true // decimal point only after number
        }
        if (lastSymbol == "." && !isContained(symbol, symNumbers)) {
            return true // only number after decimal point
        }
        if (isContained(lastSymbol, symSigns) && isContained(symbol, symSigns)) {
            return true // avoid double signs e.g. *+, ++, ...
        }
        if (isContained(lastSymbol, symSigns) && symbol == "^{") {
            return true // no power after sign e.g. -^2
        }
        if (lastSymbol == "}" && symbol == "^{") {
            return true // no power after closing expression: debatable!!!
        }
        if (isContained(lastSymbol, symOpening) && isContained(symbol, symClosing)) {
            return true // no brackets without content. e.g. ()
        }
        if (symbol == ")" && nextSymbol != ")") {
            return true // don't close wrong pair of brackets in nested functions
        }
        if (symbol == "}" && ((countMatchingSting("\\int_{", getLatexText()) == 1)
                            || (countMatchingSting("\\sum_{", getLatexText()) == 1)
                            || (countMatchingSting("\\prod_{", getLatexText()) == 1))
                            and (countMatchingSting("}^{", getLatexText()) == 1) ) {
            return false
        }
        return symbol == "}" && !isContained(nextSymbol, symClosing)
    }

    private fun addSymbol(symbol: String) {
        // Add the pressed symbol and corresponding brackets if it is compliant with the rules.
        if (isInsertionViolations(symbol)) {
            return
        }
        if (symbol == "}") {
            // e.g. in fraction: symbol is not } but }{ to open the denominator
            left.add(right[right.size - 1])
        } else {
            left.add(symbol)
        }
        handleBrackets(symbol)
    }

    private fun handleBrackets(symbol: String) {
        // add or remove corresponding closing brackets to the right for inserted opening brackets

        // round brackets
        if (isContained(symbol, symRoundBracket)) {
            right.add(")")
            // add separator between basis and anti-log
            if (symbol == "log_{") {
                right.add("}(")
            }
        } else if (isContained(symbol, symCurlyBracket)) {
            right.add("}")
            // add separator between nominator and denominator
            if (symbol == "\\int_{" || symbol == "\\sum_{" || symbol == "\\prod_{") {
                right.add("}^{")
            } else if (symbol == "\\sqrt[") {
                right.add("]{")
            }
        } else if (symbol == ")" || symbol == "}") {
            right.removeAt(right.size - 1)
            if(("\\int_{" in getLatexText() || "\\sum_{" in getLatexText() || "\\prod_{" in getLatexText())
                        and ("}^{" in getLatexText())) {
                if(("{}" !in getLatexText()) and ("}\\lceil" !in getLatexText()))
                    right.add("}{")
            }
            if(getLatexText().contains("}{\\lceil}"))
                right.removeAt(right.size - 1)
        }
    }

    override fun onClick(v: View) {
        // Starts action depending on which button is clicked.

        // Invoke action of check button
        if (v.id == R.id.bt_ac) {
            check()
        } else if (v.id == R.id.bt_del) {
            delete()
        } else {
            try {
                addSymbol(keyValues[v.id])
            } catch (e : ArrayIndexOutOfBoundsException) {
                Log.e(TAG, e.toString())
            }
        }
        // display the text, $$ used to indicate a latex format
        mvFormula!!.text = "$$" + getLatexText() + "$$"
        Log.i(TAG, "Original LaTEX : " + getLatexText())
    }

    fun getLatexText(): String {
        //  Return the typed text in latex format.
        val text = StringBuilder()

        // add symbols in front of the cursor
        for (i in left.indices) {
            text.append(left[i])
        }

        // add cursor symbol: Here, the ceiling symbol is used
        text.append("\\lceil")

        // add symbols after the cursor
        for (i in right.indices.reversed()) {
            text.append(right[i])
        }
        return text.toString()
    }

    private fun isContained(value: String, searchList: Array<String>): Boolean {
        // Checks whether the value is a member of the list
        for (curValue in searchList) {
            if (curValue == value) {
                return true
            }
        }
        return false
    }

    private fun countMatchingSting(matchString: String, searchString: String) : Int {
        val counter = searchString
            .groupingBy { matchString }
            .eachCount()
        return counter.size
    }

    init {
        initializeKeyboard(context)
    }
}