package e.roman.lab7

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import kotlin.math.abs

class MainActivity : AppCompatActivity() {

    private lateinit var weightTVs: MutableList<TextView>
    private lateinit var applyBtn: Button
    private lateinit var weigthET: EditText
    private lateinit var pointET: EditText

    private lateinit var imageView: ImageView
    private lateinit var bitmap: Bitmap
    private lateinit var canvas: Canvas
    private var x = 0f
    private var y = 0f
    private val points = mutableListOf<Point>()
    private var move = -1
    private val bCoefs0 = mutableMapOf<Float, Float>()
    private val bCoefs1 = mutableMapOf<Float, Float>()
    private val bCoefs2 = mutableMapOf<Float, Float>()
    private val bCoefs3 = mutableMapOf<Float, Float>()
    private val bCoefs4 = mutableMapOf<Float, Float>()
    private val bCoefs5 = mutableMapOf<Float, Float>()
    private val bCoefs6 = mutableMapOf<Float, Float>()
    private val tArgs = mutableListOf<Float>()
    private val k = 3

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        weightTVs = mutableListOf()
        weightTVs.add(findViewById(R.id.weigth0_tv))
        weightTVs.add(findViewById(R.id.weigth1_tv))
        weightTVs.add(findViewById(R.id.weigth2_tv))
        weightTVs.add(findViewById(R.id.weigth3_tv))
        weightTVs.add(findViewById(R.id.weigth4_tv))
        weightTVs.add(findViewById(R.id.weigth5_tv))
        weightTVs.add(findViewById(R.id.weigth6_tv))
        applyBtn = findViewById(R.id.apply_btn)
        weigthET = findViewById(R.id.weigth_et)
        pointET = findViewById(R.id.point_et)
        val paint = Paint()
        paint.color = Color.BLACK
        applyBtn.setOnClickListener {
            if (weigthET.text.isNotEmpty() && pointET.text.isNotEmpty()){
                if (points.size > 6) {
                    weightTVs[pointET.text.toString().toInt() - 1].text = weigthET.text.toString()
                    points[pointET.text.toString().toInt() - 1].h = weigthET.text.toString().toFloat()
                    draw()
                    drawCurve(paint)
                }
            }
        }
        var t = 0f
        while (t < 1f){
            tArgs.add(t)
            t += 1f / (7f + k.toFloat())
        }
        tArgs.add(1f)
        imageView = findViewById(R.id.image_view)
        bitmap = Bitmap.createBitmap(1080, 1550, Bitmap.Config.ARGB_8888)
        imageView.setImageBitmap(bitmap)
        canvas = Canvas(bitmap)
        imageView.setOnTouchListener { _: View, motionEvent: MotionEvent ->
            when (motionEvent.action) {
                MotionEvent.ACTION_DOWN -> {
                    move = -1
                    for (i in points.indices) {
                        if (abs(motionEvent.x - points[i].x) < 50f && abs(motionEvent.y - points[i].y) < 50f)
                            move = i
                    }
                    if (move == -1 && points.size < 7) {
                        x = motionEvent.x
                        y = motionEvent.y
                    }
                }
                MotionEvent.ACTION_UP -> {
                    if (abs(motionEvent.x - x) < 50f && abs(motionEvent.y - y) < 50f) {
                        points.add(Point(motionEvent.x, motionEvent.y, 1f, k, points.size, tArgs, canvas, paint))
                        //h.add(1f)
                        draw()
                        if (points.size == 7) {
                            var t = 0f
                            while (t < 1f) {
                                bCoefs0[t] = b(k, 0, t, tArgs)
                                bCoefs1[t] = b(k, 1, t, tArgs)
                                bCoefs2[t] = b(k, 2, t, tArgs)
                                bCoefs3[t] = b(k, 3, t, tArgs)
                                bCoefs4[t] = b(k, 4, t, tArgs)
                                bCoefs5[t] = b(k, 5, t, tArgs)
                                bCoefs6[t] = b(k, 6, t, tArgs)
                                t += 0.01f
                            }
                            bCoefs0[t] = b(k, 0, t, tArgs)
                            bCoefs1[t] = b(k, 1, t, tArgs)
                            bCoefs2[t] = b(k, 2, t, tArgs)
                            bCoefs3[t] = b(k, 3, t, tArgs)
                            bCoefs4[t] = b(k, 4, t, tArgs)
                            bCoefs5[t] = b(k, 5, t, tArgs)
                            bCoefs6[t] = b(k, 6, t, tArgs)
                            drawCurve(paint)
                        }
                    }
                }
                MotionEvent.ACTION_MOVE -> {
                    if (move != -1) {
                        points[move].move(motionEvent.x, motionEvent.y)
                        draw()
                        if (points.size > 6)
                            drawCurve(paint)
                    }
                }
            }

            return@setOnTouchListener true
        }
    }

    private fun draw(){
        canvas.drawColor(Color.WHITE)
        for (i in points)
            i.draw()
        imageView.setImageBitmap(bitmap)
    }

    private fun drawCurve(paint: Paint){
        var t = 0f
        while (t < 1f) {
            if (px(t) != 0 && px(t + 0.01f) != 0)
            canvas.drawLine(
                    px(t).toFloat(),
                    py(t).toFloat(),
                    px(t + 0.01f).toFloat(),
                    py(t + 0.01f).toFloat(),
                    paint
            )
            t += 0.01f
        }
        imageView.setImageBitmap(bitmap)
    }

    private fun b(k: Int, i: Int, t: Float, tArgs: MutableList<Float>): Float{
        return if (k == 1) {
            if (t in tArgs[i]..tArgs[i+1])
                1f
            else
                0f
        } else
            (t - tArgs[i]) * b(k - 1, i, t, tArgs) / (tArgs[i + k - 1] - tArgs[i]) + (tArgs[i+k] - t) * b(k - 1, i + 1, t, tArgs) / (tArgs[i + k] - tArgs[i + 1])
    }

    private fun px(t: Float): Int{
        var a = (points[0].x * points[0].h * bCoefs0[t]!! + points[1].x * points[1].h * bCoefs1[t]!! + points[2].x * points[2].h * bCoefs2[t]!! + points[3].x * points[3].h * bCoefs3[t]!!
                + points[4].x * points[4].h * bCoefs4[t]!! + points[5].x * points[5].h * bCoefs5[t]!! + points[6].x * points[6].h * bCoefs6[t]!!) /
                (points[0].h * bCoefs0[t]!! + points[1].h * bCoefs1[t]!! + points[2].h * bCoefs2[t]!! + points[3].h * bCoefs3[t]!!
                + points[4].h * bCoefs4[t]!! + points[5].h * bCoefs5[t]!! + points[6].h * bCoefs6[t]!!)
        return a.toInt()
    }

    private fun py(t: Float): Int{
        var a =(points[0].y * points[0].h * bCoefs0[t]!! + points[1].y * points[1].h * bCoefs1[t]!! + points[2].y * points[2].h * bCoefs2[t]!! + points[3].y * points[3].h * bCoefs3[t]!!
                + points[4].y * points[4].h * bCoefs4[t]!! + points[5].y * points[5].h * bCoefs5[t]!! + points[6].y * points[6].h * bCoefs6[t]!!).toFloat() /
                (points[0].h * bCoefs0[t]!! + points[1].h * bCoefs1[t]!! + points[2].h * bCoefs2[t]!! + points[3].h * bCoefs3[t]!!
                + points[4].h * bCoefs4[t]!! + points[5].h * bCoefs5[t]!! + points[6].h * bCoefs6[t]!!).toFloat()
        return  a.toInt()
    }
}