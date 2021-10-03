![app_logo(horizontal)](https://user-images.githubusercontent.com/57319751/135751439-f6d8f9fd-fa24-435f-b3ef-21d1a9121806.png)

## Summary

- **이 Repository는 학생 APP과 선생님 APP의 코드를 담고 있습니다.**
- IoT와 스마트폰을 이용해 학교 생활 속 불편한 점을 개선하고자 이 프로젝트를 시작하게 되었습니다.
- **프로젝트 기간** : 2019년 3월 ~ 2019년 11월

## Project Feature

![구상도](https://user-images.githubusercontent.com/57319751/135752060-de6911b1-0572-4a09-b4ce-d7ac2c2030fe.png)

- **스마트 교탁 (Raspberry Pi, PyQT 5)**
    - **전등**과 **선풍기** 제어
    - **실시간 시간표** 및 **담당 교사** 확인
    - **학생 출결** 관리

- **스마트 도어락 (Raspberry Pi, PyQT 5)**
    - NFC를 이용한 **자동 출석 체크**
    - DC모터와 근접 센서를 이용한 **자동문**

- **스마트 스쿨버스 (Android, Wemos, Bluetooth)**
    - 블루투스 비콘을 이용한 **실시간 위치 전송**
    - NFC를 이용한 **실시간 탑승인원 전송**

- **사용자 앱 (Android)**
    - 학교, 학과, 학번 등 **개인 정보** 확인
    - **실시간 시간표** 확인
    - **실시간 출석부** 확인
    - **실시간 스쿨버스 위치** 확인
    - **가정통신문** 확인

## APP Feature

- Socket을 이용해 실시간 데이터 전송 및 수신
- GridView를 이용해 시간표,  달력 표시
- ListView를 이용해 출석 정보 표시

## Tech Stack

- Java
- Socket (TCP)
- Bluetooth API

##  Topology
학과 전체가 '스마트 시티' 플랫폼을 주제로 만들어 비슷한 플랫폼끼리 서버를 묶고 IP를 할당시킴. 이 후 포트번호로 서버를 구분.
![네트워크 세팅](https://user-images.githubusercontent.com/57319751/135755240-72da21d8-ee46-4260-a26d-bc42ed3b6d13.png)

## Screenshot

![앱 메인화면](https://user-images.githubusercontent.com/57319751/135751559-5ade58fb-de8a-4d7d-af2d-acfe05bd1b9e.png)
![학생 메인 화면](https://user-images.githubusercontent.com/57319751/135751512-955298f2-db3a-4f28-be1b-37b2783a9604.png)
![학생 시간표](https://user-images.githubusercontent.com/57319751/135751515-ad2ee8de-c69c-4239-a9cf-18df99751c48.png)
![학생 출석부](https://user-images.githubusercontent.com/57319751/135751517-2e89180d-7dbd-4775-8ae8-a3677af93e18.png)
![학생 스쿨버스 확인](https://user-images.githubusercontent.com/57319751/135751522-17e6f9b7-dc3e-4eff-a780-8cc93bcc7665.png)
![학생 가정통신문](https://user-images.githubusercontent.com/57319751/135751524-cfa16d95-4a67-46b0-89e8-2723f0bf0630.png)

## Photo

1. MDP 프로젝트 발표회 현장 사진

![KakaoTalk_20211003_202138016](https://user-images.githubusercontent.com/57319751/135751639-b463a677-693b-4da9-b852-2a4239eea7df.jpg)

2. 작품 전체 사진

![KakaoTalk_20211003_202138016_01](https://user-images.githubusercontent.com/57319751/135751646-2be0e9b6-4155-45ef-9b78-6586c414f678.jpg)

3. 활동 사진

![선정리 1](https://user-images.githubusercontent.com/57319751/135751718-caf126f8-5ec0-4d05-9e47-0c9c956e6e70.jpg)
![선정리 하는모습](https://user-images.githubusercontent.com/57319751/135751722-e4364e36-bca3-4f19-a026-c71eabe73b16.jpg)
![큐티구경하는모습](https://user-images.githubusercontent.com/57319751/135751723-863fbca1-0eee-4b16-a0b6-63d823116cf5.jpg)

## Contact

- 김다훈 (Kimdahoon75@gmail.com)
