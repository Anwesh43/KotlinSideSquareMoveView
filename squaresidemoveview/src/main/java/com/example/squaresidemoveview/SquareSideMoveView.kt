package com.example.squaresidemoveview

/**
 * Created by anweshmishra on 22/05/18.
 */

import android.app.Activity
import android.content.Context
import android.view.View
import android.view.MotionEvent
import android.graphics.*

class SquareSideMoveView(ctx : Context) : View(ctx) {

    private val paint : Paint = Paint(Paint.ANTI_ALIAS_FLAG)

    private val renderer : Renderer = Renderer(this)

    override fun onDraw(canvas : Canvas) {
        renderer.render(canvas, paint)
    }

    override fun onTouchEvent(event : MotionEvent) : Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                renderer.handleTap()
            }
        }
        return true
    }

    data class State (var prevScale : Float = 0f, var j : Int = 0, var dir : Float = 0f) {

        val scales : Array<Float> = arrayOf(0f, 0f, 0f)

        fun update(stopcb : (Float) -> Unit) {
            scales[j] += dir * 0.1f
            if (Math.abs(scales[j] - prevScale) > 1) {
                scales[j] = prevScale + dir
                j += dir.toInt()
                if (j == scales.size || j == -1) {
                    j -= dir.toInt()
                    dir = 0f
                    prevScale = scales[j]
                    stopcb(prevScale)
                }
            }
        }

        fun startUpdating(startcb : () -> Unit) {
            if (dir == 0f) {
                dir = 1 - 2 * prevScale
                startcb()
            }
        }
    }

    data class Animator(var view : View, var animated : Boolean = false) {

        fun animate(updatecb : () -> Unit) {
            if (animated) {
                updatecb()
                try {
                    Thread.sleep(50)
                    view.invalidate()
                } catch(ex : Exception) {

                }
            }
        }

        fun start() {
            if (!animated) {
                animated = true
                view.postInvalidate()
            }
        }

        fun stop() {
            if (animated) {
                animated = false
            }
        }
    }

    data class SquareSideMove (var i : Int, val state : State = State()) {

        fun draw(canvas : Canvas, paint : Paint) {
            val w : Float = canvas.width.toFloat()
            val size : Float = w/10
            val lx : Float = w + size
            val k : Float = (lx + size)
            paint.style = Paint.Style.STROKE
            paint.strokeWidth = w/50
            paint.strokeCap = Paint.Cap.ROUND
            paint.color = Color.parseColor("#3498db")
            canvas.save()
            canvas.translate(size/2 , size/2 + k)
            canvas.rotate(180f * state.scales[1])
            for (i in 0..1) {
                canvas.save()
                canvas.translate(i * k * (1 - state.scales[0] + state.scales[2]), -k + (1 - i) * k * (state.scales[0] + 1 - state.scales[2]))
                canvas.scale(1f - 2 * i, 1f)
                val path : Path = Path()
                path.moveTo(0f, size/2)
                path.lineTo(-size/2, size/2)
                path.lineTo(-size/2, -size/2)
                path.lineTo(0f, -size/2)
                canvas.drawPath(path, paint)
                canvas.restore()
            }
            canvas.restore()
        }

        fun update(stopcb : (Float) -> Unit) {
            state.update(stopcb)
        }

        fun startUpdating(startcb : () -> Unit) {
            state.startUpdating(startcb)
        }
    }

    data class Renderer (var view : SquareSideMoveView) {

        private val animator : Animator = Animator(view)

        private val squareSideMove : SquareSideMove = SquareSideMove(0)

        fun render(canvas : Canvas, paint : Paint) {
            canvas.drawColor(Color.parseColor("#212121"))
            squareSideMove.draw(canvas, paint)
            animator.animate {
                squareSideMove.update {
                    animator.stop()
                }
            }
        }

        fun handleTap() {
            squareSideMove.startUpdating {
                animator.start()
            }
        }
    }

    companion object {
        fun create(activity : Activity) : SquareSideMoveView {
            val view : SquareSideMoveView = SquareSideMoveView(activity)
            activity.setContentView(view)
            return view 
        }
    }
}
