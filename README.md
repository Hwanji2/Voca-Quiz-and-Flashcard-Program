단어장 프로그램
이 프로젝트는 영단어 퀴즈 및 학습을 위한 GUI와 콘솔 통합 프로그램입니다. 사용자는 단어장을 관리하고, 주관식 및 객관식 퀴즈를 통해 영어 단어를 학습할 수 있습니다.

주요 기능
주관식 퀴즈
사용자는 제시된 한국어 뜻에 해당하는 영어 단어를 입력하여 정답을 맞추는 형식으로 진행됩니다.

객관식 퀴즈
영어 단어에 대한 뜻을 4지 선다형으로 선택하여 학습할 수 있습니다.

오답 노트
퀴즈에서 틀린 단어들을 확인할 수 있으며, 오답 횟수 기준으로 정렬하여 출력합니다.

단어 검색
영어 단어 또는 한국어 뜻을 입력하여 단어장에 저장된 단어를 검색할 수 있습니다.

프로그램 실행 화면
아래는 프로그램의 실제 실행 화면입니다.
![image](https://github.com/user-attachments/assets/54a5c184-2e49-436d-91ba-25acba11900d)



사용 방법
단어장 파일 불러오기
프로그램은 실행 시 기본적으로 voc/quiz.txt 파일을 불러와 단어장을 초기화합니다.

메뉴 선택
초기 화면에서 제공되는 메뉴를 통해 원하는 기능을 사용할 수 있습니다.

퀴즈 진행
주관식 또는 객관식 퀴즈를 선택한 후, 제시된 문제에 대한 정답을 입력하거나 선택합니다.

단어 관리
새로운 단어를 추가하거나 기존 단어를 삭제할 수 있으며, 단어 검색을 통해 저장된 단어를 조회할 수 있습니다.

개발 환경
개발 도구: IntelliJ IDEA
프로그래밍 언어: Java
GUI 라이브러리: Swing
폴더 구조
bash
코드 복사
JavaHomework_Voca/
│
├── src/                        # 소스 코드 폴더
│   └── homework/               # 주요 클래스 파일들
│       ├── LjhTestMain.java    # 메인 실행 파일
│       ├── MainFrame.java      # GUI 구현 파일
│       ├── VocManager.java     # 단어 관리 및 퀴즈 기능 구현
│       └── Word.java           # 단어 클래스
│
├── voc/                        # 단어장 파일 폴더
│   └── quiz.txt                # 단어 데이터 파일
│
└── README.md                   # 프로젝트 설명 파일
