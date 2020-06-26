package com.freezer.mathsolver

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.freezer.mathkeyboard.MathKeyboard
import io.github.kexanie.library.MathView

class TypeFragment : Fragment() {
    val TAG = "TypeFragment"

    lateinit var mathViewFormula : MathView
    lateinit var mathKeyboard: MathKeyboard

    companion object {
        private val mInstance = TypeFragment()

        @Synchronized
        fun getInstance(): TypeFragment {
            return mInstance
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_type , container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mathViewFormula = view.findViewById(R.id.mv_formula)
        mathKeyboard = view.findViewById(R.id.math_keyboard)

        mathKeyboard.setView(mathViewFormula)
    }
}
