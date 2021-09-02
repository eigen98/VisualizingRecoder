package com.example.visualizingrecoder

import android.content.Context
import android.util.AttributeSet
import android.widget.ImageButton
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatImageButton

//CustomView로서 안드로이드XML에서 수정을 하려면 조건이 필요함
//context와AttributeSet을 전달해야함
class RecordButton (
        context : Context,
        attrs: AttributeSet,
        ): AppCompatImageButton(context,attrs){//appCompat으로 기존 클래스를 래핑해서 정상적으로 동작


    init {
        setBackgroundResource(R.drawable.shape_oval_button)
    }

    fun updateIconWithState(state : State){
        when(state){
            State.BEFORE_RECORDING -> {
                setImageResource(R.drawable.ic_record)
            }
            State.ON_RECORDING -> {
                setImageResource(R.drawable.ic_baseline_stop_24)
            }
            State.AFTER_RECORDING ->{
                setImageResource(R.drawable.ic_play)
            }
            State.ON_PLAYING ->{
                setImageResource(R.drawable.ic_baseline_stop_24)
            }
        }
    }
}