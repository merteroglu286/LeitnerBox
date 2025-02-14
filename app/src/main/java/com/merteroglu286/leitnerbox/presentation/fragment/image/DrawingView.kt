package com.merteroglu286.leitnerbox.presentation.fragment.image

import android.content.Context
import android.graphics.*
import android.media.ExifInterface
import android.net.Uri
import android.os.Environment
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import java.io.File
import java.io.FileOutputStream

class DrawingView(context: Context, attrs: AttributeSet) : View(context, attrs) {

    var backgroundBitmap: Bitmap? = null
    private var drawingBitmap: Bitmap? = null
    private var drawingCanvas: Canvas? = null
    private val paths = mutableListOf<Path>()
    private val undonePaths = mutableListOf<Path>()
    private val pathPaints = mutableMapOf<Path, Paint>()
    private var currentPath: Path? = null

    private var currentPaint = Paint().apply {
        isAntiAlias = true
        color = Color.BLACK
        style = Paint.Style.STROKE
        strokeWidth = 10f
    }

    /*private var eraserPaint = Paint().apply {
        isAntiAlias = true
        style = Paint.Style.STROKE
        strokeWidth = 10f
        xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
    }*/

    private var isEraser = false
    private var pendingUri: Uri? = null

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        drawingBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        drawingCanvas = Canvas(drawingBitmap!!)

        pendingUri?.let { uri ->
            setImageUri(uri)
            pendingUri = null
        }
    }

    fun setImageUri(uri: Uri) {
        if (width == 0 || height == 0) {
            pendingUri = uri
            return
        }

        try {
            val inputStream = context.contentResolver.openInputStream(uri)
            val originalBitmap = BitmapFactory.decodeStream(inputStream)
            inputStream?.close()

            originalBitmap?.let {
                // EXIF verisini oku
                val exif = ExifInterface(context.contentResolver.openInputStream(uri)!!)
                val orientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL
                )

                // Oryantasyona göre döndürme açısını belirle
                val matrix = Matrix()
                when (orientation) {
                    ExifInterface.ORIENTATION_ROTATE_90 -> matrix.postRotate(90f)
                    ExifInterface.ORIENTATION_ROTATE_180 -> matrix.postRotate(180f)
                    ExifInterface.ORIENTATION_ROTATE_270 -> matrix.postRotate(270f)
                }

                // Sadece gerekiyorsa döndürme işlemi yap
                val finalBitmap = if (orientation != ExifInterface.ORIENTATION_NORMAL) {
                    Bitmap.createBitmap(it, 0, 0, it.width, it.height, matrix, true)
                } else {
                    it
                }

                backgroundBitmap = Bitmap.createScaledBitmap(finalBitmap, width, height, true)
                invalidate()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /*override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        backgroundBitmap?.let {
            canvas.drawBitmap(it, 0f, 0f, null)
        }

        drawingBitmap?.let {
            canvas.drawBitmap(it, 0f, 0f, null)
        }

        currentPath?.let { path ->
            canvas.drawPath(path, if (isEraser) eraserPaint else currentPaint)
        }
    }*/

    private var eraserPaint = Paint().apply {
        isAntiAlias = true
        style = Paint.Style.STROKE
        strokeWidth = 10f
        color = Color.TRANSPARENT  // Rengi transparan yapıyoruz
        xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
    }

    /*override fun onTouchEvent(event: MotionEvent): Boolean {
        val x = event.x
        val y = event.y

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                currentPath = Path().apply {
                    moveTo(x, y)
                }
                undonePaths.clear()
                // Silgi modundaysa direkt çizmeye başla
                if (isEraser) {
                    drawingCanvas?.drawPath(currentPath!!, eraserPaint)
                }
            }
            MotionEvent.ACTION_MOVE -> {
                currentPath?.lineTo(x, y)
                // Silgi modundaysa her harekette direkt sil
                if (isEraser) {
                    drawingCanvas?.drawPath(currentPath!!, eraserPaint)
                    // Yeni path oluştur
                    currentPath = Path().apply {
                        moveTo(x, y)
                    }
                }
            }
            MotionEvent.ACTION_UP -> {
                currentPath?.let { path ->
                    if (!isEraser) {
                        // Normal çizim modunda path'i kaydet
                        drawingCanvas?.drawPath(path, currentPaint)
                        paths.add(path)
                        pathPaints[path] = Paint(currentPaint)
                    }
                }
                currentPath = null
            }
        }
        invalidate()
        return true
    }*/

    fun setPenColor(color: Int) {
        isEraser = false
        currentPaint = Paint().apply {
            isAntiAlias = true
            style = Paint.Style.STROKE
            strokeWidth = currentPaint.strokeWidth
            this.color = color
        }
    }

    fun setEraser() {
        isEraser = true
    }

    fun setPen() {
        isEraser = false
    }

    /*fun setStrokeWidth(width: Float) {
        currentPaint.strokeWidth = width
        eraserPaint.strokeWidth = width
    }*/

    private var cursorPaint = Paint().apply {
        isAntiAlias = true
        style = Paint.Style.STROKE
        color = Color.GRAY
        strokeWidth = 2f
    }

    private var lastTouchX = 0f
    private var lastTouchY = 0f
    private var showCursor = false

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        backgroundBitmap?.let {
            canvas.drawBitmap(it, 0f, 0f, null)
        }

        drawingBitmap?.let {
            canvas.drawBitmap(it, 0f, 0f, null)
        }

        currentPath?.let { path ->
            canvas.drawPath(path, if (isEraser) eraserPaint else currentPaint)
        }

        // İmleç çizimi
        if (isEraser && showCursor) {
            canvas.drawCircle(lastTouchX, lastTouchY, eraserPaint.strokeWidth / 2, cursorPaint)
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        lastTouchX = event.x
        lastTouchY = event.y

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                showCursor = true
                currentPath = Path().apply {
                    moveTo(lastTouchX, lastTouchY)
                }
                undonePaths.clear()
                if (isEraser) {
                    drawingCanvas?.drawPath(currentPath!!, eraserPaint)
                }
            }

            MotionEvent.ACTION_MOVE -> {
                currentPath?.lineTo(lastTouchX, lastTouchY)
                if (isEraser) {
                    drawingCanvas?.drawPath(currentPath!!, eraserPaint)
                    currentPath = Path().apply {
                        moveTo(lastTouchX, lastTouchY)
                    }
                }
            }

            MotionEvent.ACTION_UP -> {
                showCursor = false
                currentPath?.let { path ->
                    if (!isEraser) {
                        drawingCanvas?.drawPath(path, currentPaint)
                        paths.add(path)
                        pathPaints[path] = Paint(currentPaint)
                    }
                }
                currentPath = null
            }
        }
        invalidate()
        return true
    }

    // İmleci göstermek/gizlemek için yeni metodlar
    fun showEraser() {
        isEraser = true
        showCursor = true
        invalidate()
    }

    fun hideCursor() {
        showCursor = false
        invalidate()
    }

    fun setStrokeWidth(width: Float) {
        currentPaint.strokeWidth = width
        eraserPaint.strokeWidth = width
        invalidate() // İmleç boyutunu güncellemek için
    }

    // Parmağınızı kaldırdığınızda imleci gizlemek için
    override fun onWindowVisibilityChanged(visibility: Int) {
        super.onWindowVisibilityChanged(visibility)
        if (visibility != VISIBLE) {
            hideCursor()
        }
    }

    fun clear() {
        paths.clear()
        undonePaths.clear()
        pathPaints.clear()
        currentPath = null
        drawingCanvas?.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR)
        invalidate()
    }

    fun undo() {
        if (paths.isNotEmpty()) {
            val lastPath = paths.removeAt(paths.lastIndex)
            undonePaths.add(lastPath)
            redrawPaths()
        }
    }

    fun redo() {
        if (undonePaths.isNotEmpty()) {
            val lastUndonePath = undonePaths.removeAt(undonePaths.lastIndex)
            paths.add(lastUndonePath)
            redrawPaths()
        }
    }

    private fun redrawPaths() {
        drawingCanvas?.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR)
        for (path in paths) {
            pathPaints[path]?.let { paint ->
                drawingCanvas?.drawPath(path, paint)
            }
        }
        invalidate()
    }

    fun canUndo(): Boolean = paths.isNotEmpty()
    fun canRedo(): Boolean = undonePaths.isNotEmpty()

    fun saveDrawingToUri(): Uri? {
        if (width == 0 || height == 0) return null

        val combinedBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(combinedBitmap)

        backgroundBitmap?.let { canvas.drawBitmap(it, 0f, 0f, null) }
        drawingBitmap?.let { canvas.drawBitmap(it, 0f, 0f, null) }

        val storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val fileName = "drawing_${System.currentTimeMillis()}.png"
        val file = File(storageDir, fileName)

        return try {
            FileOutputStream(file).use { fos ->
                combinedBitmap.compress(Bitmap.CompressFormat.PNG, 100, fos)
            }
            Uri.fromFile(file)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
