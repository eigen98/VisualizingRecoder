package com.example.visualizingrecoder

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.text.BoringLayout
import android.util.AttributeSet
import android.view.View
import kotlin.random.Random

//CustomView로서 안드로이드XML에서 수정을 하려면 조건이 필요함
//context와AttributeSet을 전달해야함
class SoundVisualizerView(
        context: Context,
        attrs: AttributeSet? = null
) : View(context, attrs) {

    var onRequestCurrentAmplitude: (() -> Int)? = null

    //Paint라는 객체 ( color,width요청)
    private val amplituePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {//도트 방지플래그
        color = context.getColor(R.color.purple_500)
        strokeWidth = Line_WIDTH
        strokeCap = Paint.Cap.ROUND //Line의 끄트머리 표시 (동그랗게)
    }

    private var drawingWidth: Int = 0 //그려야할 사이즈
    private var drawingHeight: Int = 0
    private var drawingAmplitudes: List<Int> = emptyList()//진폭 리스트
    private var isReplaying: Boolean = false
    private var replayingPosition: Int = 0


    //
    private val visualizeRepeatAction: Runnable = object : Runnable {
        override fun run() {
            if (!isReplaying) {
                //Amplitude, Draw요청
                val currntAMplitude = onRequestCurrentAmplitude?.invoke()
                        ?: 0//현재 recorder의 max값 가져옴
                drawingAmplitudes = listOf(currntAMplitude) + drawingAmplitudes
            } else {
                replayingPosition++
            }


            invalidate() // 마지막으로 데이터가 초과되었을 때 갱신

            handler?.postDelayed(this, ACTION_INTERVAL)//20밀리 세컨드 마다 실행
        }
    }


    //실제로 그려야할 영역 사이즈 명확하게 파악
    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        drawingWidth = w
        drawingHeight = h
    }

    //진폭을 그려줌(canvas에 길이,간격 요청)(Paint에 color,width요청)
    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        canvas ?: return

        val centerY = drawingHeight / 2f //중앙값
        var offsetX = drawingWidth.toFloat() //시작값 (오른쪽부터)

        drawingAmplitudes
                .let { amplitudes ->
                    if (isReplaying) {

                        amplitudes.takeLast(replayingPosition)//가장뒤에있는 것부터 가져옴
                    } else {
                        amplitudes
                    }
                            .forEach { amplitude ->
                                val lineLength = amplitude / MAX_AMPLITUDE * drawingHeight * 0.8F //0.8로 이쁘게 크기 조절

                                offsetX -= LINE_SPACE
                                if (offsetX < 0) return@forEach//뷰의 왼쪽영역보다 바깥쪽으로 나가면

                                canvas.drawLine(//시작xy값, 정지 xy값, paint
                                        offsetX,
                                        centerY - lineLength / 2F,
                                        offsetX,
                                        centerY + lineLength / 2F,
                                        amplituePaint
                                )
                            }
                }

    }

    fun startVisualizing(isReplaying: Boolean) {
        this.isReplaying = isReplaying
        handler?.post(visualizeRepeatAction) //시작
    }


    fun stopVisualizing() {

        replayingPosition = 0
        handler?.removeCallbacks(visualizeRepeatAction) //정지
    }


    fun clearVisualizing() {
        drawingAmplitudes = emptyList()
        invalidate()
    }


    companion object {
        private const val Line_WIDTH = 10F
        private const val LINE_SPACE = 15F //간격
        private const val MAX_AMPLITUDE = Short.MAX_VALUE.toFloat()// short타입의 max값
        private const val ACTION_INTERVAL = 20L
    }

}