package com.example.visualizingrecoder

import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import java.util.jar.Manifest

//상태정의  녹음전->녹음중->녹음후->재생중
//상태를 class로 정의

//녹음을 하기 위해서는 사용자에게 디바이스의 마이크에 접근을 한다는 것을 알려야함 manifast에 권한명시,

//runtime Permission이란?

//MediaRecorder 클래스 구조

class MainActivity : AppCompatActivity() {

    private val recordButton: RecordButton by lazy{
        findViewById(R.id.recordButton)
    }

    private val requiredPermissions = arrayOf(android.Manifest.permission.RECORD_AUDIO)

    private var state = State.BEFORE_RECORDING //초기상태

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        requestAudioPermission() //시작하자마자 권한요청
        initView()
    }


    override fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<out String>,
            grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        //요청한 권한의 결과를 받음
        val audioRecordPermissionGranted = requestCode == REQUEST_RECORD_AUDIO_PERMISSION &&
                grantResults.firstOrNull() == PackageManager.PERMISSION_GRANTED //이두가지가 충족되면 권한 부여된것

        if(!audioRecordPermissionGranted){
            finish() //권한 받지 못했다면 종료
        }
    }

    private fun requestAudioPermission(){
        requestPermissions(requiredPermissions, REQUEST_RECORD_AUDIO_PERMISSION)
    }


    private fun initView(){
        recordButton.updateIconWithState(state)
    }

    companion object{
        private const val REQUEST_RECORD_AUDIO_PERMISSION = 201
    }
}