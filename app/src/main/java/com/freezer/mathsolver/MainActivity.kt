package com.freezer.mathsolver

import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.freezer.mathsolver.detailsolutionactivity.DetailSolutionActivity
import com.freezer.mathsolver.history.HistoryFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.myscript.iink.uireferenceimplementation.InputController
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MainActivity: AppCompatActivity(), BottomNavigationView.OnNavigationItemSelectedListener {
    companion object {
        val TAG = "MainActivity"
    }

    lateinit var iInkFragment: IInkFragment
    lateinit var typeFragment: TypeFragment
    lateinit var historyFragment: HistoryFragment
    lateinit var curFragment : Fragment

    lateinit var convertMenuItem: MenuItem
    lateinit var queryMenuItem: MenuItem

    var curInputMode = 0 // PEN = 0 , TOUCH = 1, TYPE = 3

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        iInkFragment = IInkFragment.getInstance()
        typeFragment = TypeFragment.getInstance()
        historyFragment = HistoryFragment.getInstance()

        curFragment = iInkFragment

        val fragmentPopulateScope = CoroutineScope(Dispatchers.Default)
        fragmentPopulateScope.launch {
            // Pre populate fragment
            supportFragmentManager.beginTransaction()
                .add(R.id.fragment_editor, iInkFragment, "IInkFragment")
                .commit()
            supportFragmentManager.beginTransaction()
                .add(R.id.fragment_editor, typeFragment, "TypeFragment")
                .hide(typeFragment)
                .commit()
            supportFragmentManager.beginTransaction()
                .add(R.id.fragment_editor, historyFragment, "HistoryFragment")
                .hide(historyFragment)
                .commit()
        }

        bottom_navigation.setOnNavigationItemSelectedListener(this)

//        button_input_mode_forcePen.setOnClickListener(this)
//        button_input_mode_forceTouch.setOnClickListener(this)
//        button_input_mode_forceType.setOnClickListener(this)

        // Disable active button linked to input mode
//        button_input_mode_forcePen.isEnabled = false
//        button_input_mode_forceTouch.isEnabled = true
//        button_input_mode_forceType.isEnabled = true
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.activity_main, menu)

//        val convertMenuItem = menu?.findItem(R.id.menu_convert)
//        convertMenuItem?.isEnabled = true

        convertMenuItem = menu?.findItem(R.id.menu_convert)!!
        queryMenuItem = menu.findItem(R.id.menu_query)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_convert -> {
                iInkFragment.convert()
            }
            R.id.menu_query -> {
                var queryInputStr = when(curFragment) {
                    is IInkFragment -> {
                        // IInk mode
                        iInkFragment.getLatexText()
                    }
                    else -> {
                        // Type mode
                        typeFragment.mathKeyboard.getLatexText()
                    }
                }
                GlobalScope.launch {
                    queryInputStr = LatexProcessor.getCalculableLatexText(queryInputStr)
                    runOnUiThread{
                        val intent = Intent(this@MainActivity, DetailSolutionActivity::class.java)
                        intent.putExtra("queryInputStr", queryInputStr)
                        intent.putExtra("isAdding", true)
                        startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this@MainActivity).toBundle())
                    }
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.pen_page -> {
                showIInkFragment()
            }
            R.id.type_page -> {
                showTypeFragment()
            }
            R.id.history_page -> {
                showHistoryFragment()
            }
            else ->
                Log.e(TAG, "Failed to handle event")
        }
        return true
    }

//    override fun onClick(v: View?) {
//        when(v?.id) {
//            R.id.button_input_mode_forcePen -> {
//                if(curInputMode != 0) {
//                    showIInkFragment()
//                }
//                iInkFragment.setInputMode(InputController.INPUT_MODE_FORCE_PEN)
//                curInputMode = 0
//            }
//            R.id.button_input_mode_forceTouch -> {
//                if(curInputMode != 1) {
//                    showIInkFragment()
//                }
//                iInkFragment.setInputMode(InputController.INPUT_MODE_FORCE_TOUCH)
//                curInputMode = 1
//            }
//            R.id.button_input_mode_forceType -> {
//                if(curInputMode != 3)
//                    showTypeFragment()
//                curInputMode = 3
//            }
//            else ->
//                Log.e(TAG, "Failed to handle event")
//        }
//        invalidateInputModeOptions()
//    }

//    private fun invalidateInputModeOptions() {
//        button_input_mode_forcePen.isEnabled = curInputMode != InputController.INPUT_MODE_FORCE_PEN
//        button_input_mode_forceTouch.isEnabled = curInputMode != InputController.INPUT_MODE_FORCE_TOUCH
//        button_input_mode_forceType.isEnabled = curInputMode != 3
//    }

    private fun showIInkFragment() {
        supportFragmentManager.beginTransaction().hide(curFragment).commit()
        supportFragmentManager.beginTransaction().show(iInkFragment).commit()
        curFragment = iInkFragment

        iink_toolbar_button_layout.visibility = View.VISIBLE
        convertMenuItem.isVisible = true
        queryMenuItem.isVisible = true
    }

    private fun showTypeFragment() {
        supportFragmentManager.beginTransaction().hide(curFragment).commit()
        supportFragmentManager.beginTransaction().show(typeFragment).commit()
        curFragment = typeFragment

        iink_toolbar_button_layout.visibility = View.GONE
        convertMenuItem.isVisible = false
        queryMenuItem.isVisible = true
    }

    private fun showHistoryFragment() {
        supportFragmentManager.beginTransaction().hide(curFragment).commit()
        supportFragmentManager.beginTransaction().show(historyFragment).commit()
        curFragment = historyFragment

        iink_toolbar_button_layout.visibility = View.GONE
        iink_toolbar_button_layout.visibility = View.GONE
        convertMenuItem.isVisible = false
        queryMenuItem.isVisible = false
    }
}

