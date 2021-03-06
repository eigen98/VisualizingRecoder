# VisualizingRecoder
# [녹음기]
여기에 사용되는 기술들이 요즘 오디오스트리밍에도 활용이 된다고 하여   
이번 프로젝트를 통해  관련된 기술들을 공부를 목표로함.   

![KakaoTalk_20210902_101419483](https://user-images.githubusercontent.com/68258365/131765884-d3d8a3f0-b894-44b0-a18c-7ad5d64bb560.jpg)
![KakaoTalk_20210902_101419483_02](https://user-images.githubusercontent.com/68258365/131765909-57a3dc47-3976-484d-9d4e-a0470d258ccc.jpg)
![KakaoTalk_20210902_101419483_01](https://user-images.githubusercontent.com/68258365/131765896-32de8090-ed19-480f-8fdc-629c001d1451.jpg)


### (마이크)녹음기능
-음성 visualizing
-정지
-재생

### 필요한 기술
-Request runtime permission 마이크 접근
-CustomView를 통해 미디어 시각화
-MediaRecoder를 통해 사용자의 목소리 녹음

### 구현순서
#### 01. 기본 ui구성
#### 02. 권한요청
-> 녹음을 하기 위해서는 사용자에게 디바이스의 마이크에 접근을 한다는 것을 알려야함 manifast에 권한명시   
#### 03.녹음기능 구현
![mediarecorder_state_diagram](https://user-images.githubusercontent.com/68258365/131695820-49d2904a-c1a0-4c95-b23f-5fe35e1d9e9a.gif)

-> MediaRecorder 클래스 필요
-> 첫 상태 initial 

MediaRecorder recorder = new MediaRecorder();   
 recorder.setAudioSource(MediaRecorder.AudioSource.MIC);   //마이크 소스에 접근    
 recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);   //컨테이너(THREE_GPP) 박스에 정리하는 역할 -> 나중에 디코더가 꺼내서 해석후 재생
 recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);   //인코더(코덱)를 통해 소스를 AMR_NB 방식으로 압축 -> 컨테이너와 인코더 호환되는 여부 중요   
 recorder.setOutputFile(PATH_NAME);   
 recorder.prepare();   
 recorder.start();   // Recording is now started   
 ...   
 recorder.stop();   
 recorder.reset();   // You can reuse the object by going back to setAudioSource() step   
 recorder.release(); // Now the object cannot be reused   

![mediaplayer_state_diagram](https://user-images.githubusercontent.com/68258365/131700483-58589e49-b543-4cfa-a628-6adcd1f318b3.gif)

->미디어 재생시 MediaPlayer 인스턴스 필요
-> 처음은 idle 스테이트


#### 04. 완성도 높이기

-> 오디오시각화(customView의 Custom Drawing이용)   
onDraw()를 override하는 것이 중요.   
캔버스와 paint라는 객체를 전달받음(캔버스에 드로잉, paint는 어떻게 그릴지).   
onDraw메소드 내부에서Paint생성 주의 (자주 호출되는 onDraw()에서 객체 생성시 성능 저하).   
실제로 그려야할 영역 사이즈 명확하게 파악 주의(다양한 디바이스).-> onSizeChanged() 를 override
