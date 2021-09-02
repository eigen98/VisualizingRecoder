package com.example.visualizingrecoder

import android.content.Context
import android.os.SystemClock
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView

class CountUpView(
        context : Context,
        attrs : AttributeSet? = null
) :AppCompatTextView(context,attrs) {

    private var startTimeStamp : Long = 0L

    //타임스탬프를 찍음. 1초마다 반복. 현재시간을 가져와서 지금시간과 비교하여  몇초가 흘렀는지 텍스트변경
    private val countUpAction : Runnable = object : Runnable{ //인터페이스 이므로 object로 구현
        override fun run() {
            val currentTimeStamp = SystemClock.elapsedRealtime()

            val countTimeSeconds = ((currentTimeStamp - startTimeStamp)/1000L).toInt()
            updateCountTime(countTimeSeconds)

            handler?.postDelayed(this,1000L)
        }
    }

    fun startCountUp(){
        startTimeStamp = SystemClock.elapsedRealtime()
        handler?.post(countUpAction)
    }

    fun stopCountUp(){
        handler?.removeCallbacks(countUpAction)
    }

    fun clearCountUp(){
        updateCountTime(0)
    }

    private fun updateCountTime(countTimeSeconds : Int){
        val minutes = countTimeSeconds / 60
        val seconds = countTimeSeconds % 60

        text = "%02d:%02d".format(minutes,seconds)
    }


}