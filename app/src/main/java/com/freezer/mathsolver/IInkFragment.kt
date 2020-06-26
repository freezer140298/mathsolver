package com.freezer.mathsolver

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Button
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import com.myscript.iink.*
import com.myscript.iink.uireferenceimplementation.EditorView
import com.myscript.iink.uireferenceimplementation.InputController
import com.myscript.iink.uireferenceimplementation.Path
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import java.io.IOException

class IInkFragment : Fragment(), View.OnClickListener {
    val TAG = "IInkFragment"

    lateinit var engine : Engine
    lateinit var contentPackage: ContentPackage
    lateinit var contentPart: ContentPart
    lateinit var editorView: EditorView


    lateinit var undoButton : ImageButton
    lateinit var redoButton : ImageButton
    lateinit var clearButton : ImageButton

    var packageCodePath : String?  = null
    var filesDir : File? = null


    companion object {
        private val mInstance = IInkFragment()

        @Synchronized
        fun getInstance(): IInkFragment {
            return mInstance
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        engine = IInkEngine.getEngine()

        packageCodePath = activity?.packageCodePath
        filesDir = activity?.filesDir

        val config = engine.configuration
        val configDir = "zip://$packageCodePath!/assets/conf"
        config.setStringArray("configuration-manager.search-path", arrayOf(configDir))
        val tempDir = filesDir?.path.toString() + File.separator + "tmp"
        config.setString("content-package.temp-folder", tempDir)
        config.setBoolean("gesture.enable", true)

        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment


        return inflater.inflate(R.layout.fragment_i_ink, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        editorView = view.findViewById(R.id.editor_view)

        editorView.setEngine(engine)

        val editor = editorView.editor

        editor?.addListener(object : IEditorListener {
            override fun partChanging(editor: Editor?, oldPart: ContentPart?, newPart: ContentPart?) {
                // Unnecessary
            }

            override fun contentChanged(editor: Editor?, blockIds: Array<String>?) {
                invalidateIconButtons()
                activity?.invalidateOptionsMenu()
            }

            override fun partChanged(editor: Editor?) {
                invalidateIconButtons()
                activity?.invalidateOptionsMenu()
            }

            override fun onError(editor: Editor?, blockId: String?, message: String?) {
                Log.e(TAG, "Failed to edit block '${blockId}' $message")
            }
        })

        setInputMode(InputController.INPUT_MODE_FORCE_PEN)

        val packageName = "File1.iink"
        val file = File(filesDir, packageName)

        try {
            contentPackage = engine.createPackage(file)
            contentPart = contentPackage.createPart("Math")
        }
        catch (e : IOException) {
            Log.e(TAG, "Failed to open package $packageName")
        }
        catch (e : IllegalArgumentException) {
            Log.e(TAG, "Failed to open package $packageName")
        }

        editorView.post {
            editorView.renderer?.setViewOffset(0F, 0F)
            editorView.renderer?.viewScale = 1F
            editorView.visibility = View.VISIBLE
            editor?.part = contentPart
        }

        undoButton = activity?.findViewById(R.id.button_undo)!!
        redoButton = activity?.findViewById(R.id.button_redo)!!
        clearButton = activity?.findViewById(R.id.button_clear)!!

        undoButton.setOnClickListener(this)
        redoButton.setOnClickListener(this)
        clearButton.setOnClickListener(this)

        invalidateIconButtons()
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onDestroy() {
        editorView.setOnTouchListener(null)
        editorView.close()
        contentPart.close()
        contentPackage.close()

        engine.close()
        super.onDestroy()
    }


    fun setInputMode(inputMode : Int) {
        editorView.inputMode = inputMode
    }

    fun getInputMode(): Int {
        return editorView.inputMode
    }

    private fun invalidateIconButtons() {
        val editor = editorView.editor
        val canUndo = editor?.canUndo()
        val canRedo = editor?.canRedo()

        activity?.runOnUiThread {
            undoButton.isEnabled = canUndo!!
            redoButton.isEnabled = canRedo!!
            clearButton.isEnabled = contentPart != null
        }
    }


    override fun onClick(v: View?) {
        when(v?.id) {
            R.id.button_undo ->
                editorView.editor?.undo()
            R.id.button_redo ->
                editorView.editor?.redo()
            R.id.button_clear ->
                editorView.editor?.clear()
            else ->
                Log.e(TAG, "Failed to handle event")
        }
    }


    // Method of IIinkFragment
    fun convert() {
        val editor = editorView.editor
        val supportedStates: Array<ConversionState> =
            editor!!.getSupportedTargetConversionStates(null)
        if (supportedStates.size > 0) {
            editor.convert(null, supportedStates[0])
        }
    }

    fun getLatexText() : String {
        val editor = editorView.editor
        val result = editor?.export_(editor.rootBlock, MimeType.LATEX)

        Log.i(TAG, "Exported LaTEX : $result")
        return result!!
    }


}

