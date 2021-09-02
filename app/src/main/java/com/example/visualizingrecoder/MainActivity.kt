package com.example.visualizingrecoder

import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.media.MediaRecorder
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import java.util.jar.Manifest

//상태정의  녹음전->녹음중->녹음후->재생중
//상태를 class로 정의

//녹음을 하기 위해서는 사용자에게 디바이스의 마이크에 접근을 한다는 것을 알려야함 manifast에 권한명시,

//runtime Permission이란?

//MediaRecorder 클래스 구조

class MainActivity : AppCompatActivity() {

    private val soundVisualizerView: SoundVisualizerView by lazy {
        findViewById(R.id.soundVisualizerView)
    }

    private val recordTimeTextView: CountUpView by lazy {
        findViewById(R.id.recordTimeTextView)
    }

    private val resetButton: Button by lazy {
        findViewById(R.id.resetButton)
    }
    private val recordButton: RecordButton by lazy {
        findViewById(R.id.recordButton)
    }


    private val requiredPermissions = arrayOf(
            android.Manifest.permission.RECORD_AUDIO,
            android.Manifest.permission.READ_EXTERNAL_STORAGE
    )

    private val recordingFilePath: String by lazy {
        //externalCacheDir?.absolutePath + "/recording.3gp" //녹음하고있는 오디오를 저장할 path접근
        "${externalCacheDir?.absolutePath}/recording.3gp"
    }
    private var recorder: MediaRecorder? = null //정지하게되면 다시 초기화 해주어야함. 사용하지 않을때는 메모리 해지하고 null인 편이 관리 용이
    private var player: MediaPlayer? = null
    private var state = State.BEFORE_RECORDING //초기상태
        set(value) {
            field = value
            resetButton.isEnabled = (value == State.AFTER_RECORDING || value == State.ON_PLAYING)
            recordButton.updateIconWithState(value)
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        requestAudioPermission() //시작하자마자 권한요청
        initViews()
        bindViews()
        initVariables()
    }


    override fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<out String>,
            grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        //요청한 권한의 결과를 받음
        val audioRecordPermissionGranted = requestCode == REQUEST_RECORD_AUDIO_PERMISSION &&
                grantResults.firstOrNull() == PackageManager.PERMISSION_GRANTED //이두가지가 충족되면 권한 부여된것

        if (!audioRecordPermissionGranted) {
            finish() //권한 받지 못했다면 종료
        }
    }

    private fun requestAudioPermission() {
        requestPermissions(requiredPermissions, REQUEST_RECORD_AUDIO_PERMISSION)
    }


    private fun initViews() {
        recordButton.updateIconWithState(state)
    }

    private fun bindViews() { //녹음버튼 기능 구현

        soundVisualizerView.onRequestCurrentAmplitude = {
            recorder?.maxAmplitude ?: 0
        }

        resetButton.setOnClickListener {
            //재생중에서도 reset누르는 경우 stop
            stopPlaying()
            soundVisualizerView.clearVisualizing()
            recordTimeTextView.clearCountUp()
            state = State.BEFORE_RECORDING

        }
        recordButton.setOnClickListener {
            when (state) {
                State.BEFORE_RECORDING -> {
                    startRecording()
                }
                State.ON_RECORDING -> {
                    stopRecording()
                }
                State.AFTER_RECORDING -> {
                    startPlaying()
                }
                State.ON_PLAYING -> {
                    stopPlaying()
                }
            }
        }
    }

    private fun initVariables() {
        state = State.BEFORE_RECORDING
    }

    private fun startRecording() { //미디어레코드 사용하기 위한 설정
        //MediaRecorder 클래스 인스턴스 생성 초기화
        recorder = MediaRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
            setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
            setOutputFile(recordingFilePath)//앱에서만 접근할 수 있는 storage 한정 -> 외부 storage 접근
            prepare()
        }
        recorder?.start()//녹음 시작
        soundVisualizerView.startVisualizing(false)
        recordTimeTextView.startCountUp()
        state = State.ON_RECORDING
    }

    private fun stopRecording() {
        recorder?.run {
            stop()
            release()//해제

        }
        recorder = null
        soundVisualizerView.stopVisualizing()
        recordTimeTextView.stopCountUp()
        state = State.AFTER_RECORDING
    }

    private fun startPlaying() {
        //미디어 재생시 MediaPlayer 인스턴스 필요
        player = MediaPlayer()
                .apply {
                    setDataSource(recordingFilePath)//저장소PATH
                    prepare()
                }
        player?.setOnCompletionListener { //재생이 끝났을 때 상태변경
            stopPlaying()
            state = State.AFTER_RECORDING
        }
        player?.start() //재생
        soundVisualizerView.startVisualizing(true)
        recordTimeTextView.startCountUp()
        state = State.ON_PLAYING
    }

    private fun stopPlaying() {
        player?.release()
        player = null
        soundVisualizerView.stopVisualizing()
        recordTimeTextView.stopCountUp()
        state = State.AFTER_RECORDING
    }


    companion object {
        private const val REQUEST_RECORD_AUDIO_PERMISSION = 201
    }
}