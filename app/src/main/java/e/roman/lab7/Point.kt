package e.roman.lab7

import android.graphics.Canvas
import android.graphics.Paint

class Point {

    var x: Float
    var y: Float
    var h: Float
    private var canvas: Canvas
    private var paint: Paint
    var bCoefs: MutableList<Float>

    constructor(x: Float, y: Float, h: Float, k: Int, i: Int, tArgs: MutableList<Float>, canvas: Canvas, paint: Paint){
        this.x = x
        this.y = y
        this.h = h
        this.canvas = canvas
        this.paint = paint
        bCoefs = mutableListOf()
    }

    fun move(x: Float, y: Float){
        this.x = x
        this.y = y
    }

    fun draw(){
        canvas.drawRect(x - 10, y - 10, x + 10, y + 10, paint)
    }
}