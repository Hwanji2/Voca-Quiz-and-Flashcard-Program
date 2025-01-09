package homework;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.util.List;
/**
 * @author 202410647 이지환
 * @since 2024-11-18
 *
 * MainFrame 클래스
 * 프로그램의 GUI 메인 프레임.
 * 이 클래스는 사용자가 퀴즈, 단어 검색, 오답 노트, 종료 등을 실행할 수 있도록 다양한 GUI를 제공하며
 * 관리하는 역할을 수행하고 있습니다.
 *
 * `VocManager` 클래스와 상호작용하며, 단어장 및 학습 데이터의 관리와 UI 업데이트를 처리합니다.
 *`JTextArea`, `JButton`, `JTable` 등을 사용하여 사용자와 상호작용합니다.
 * 사용자가 원하는 기능을 선택할 수 있는 메뉴와 버튼들이 배치되어 있습니다.
 */

public class MainFrame extends JFrame {

    private VocManager manager; // 단어 관리를 위한 Manager 클래스
    JPanel menuPanel = new JPanel(); // 메뉴 버튼 영역
    private Container frame = getContentPane(); // JFrame의 기본 컨테이너
    private JPanel centerPanel, southPanel; // UI 레이아웃 구성용 패널
    private JTextArea displayArea; // 텍스트 출력 영역
    private JPanel optionsPanel; // 객관식 옵션을 표시할 패널
    boolean flag = true; // 단어 테이블 정렬 방향 (true: 오름차순, false: 내림차순)
    JTable table; // 단어 테이블
    DefaultTableModel model; // 테이블의 데이터 모델
    String[] header = {"영단어", "뜻"}; // 테이블 헤더 정보

    /**
     * private void disableMenuButtons(JPanel menuPanel)
     * 메뉴 버튼 비활성화 메서드.
     * @param menuPanel 비활성화할 버튼들이 포함된 패널
     */
    private void disableMenuButtons(JPanel menuPanel) {
        for (Component comp : menuPanel.getComponents()) {
            if (comp instanceof JButton) {
                comp.setEnabled(false); // 버튼 비활성화
            }
        }
    }

    /**
     * public MainFrame(String title, VocManager manager, String filePath) throws HeadlessException
     * MainFrame 생성자.
     * @param title JFrame 제목
     * @param manager 단어 관리 객체
     * @param filePath 단어장 데이터 파일 경로
     * @throws HeadlessException 예외 발생 시 처리
     */
    public MainFrame(String title, VocManager manager, String filePath) throws HeadlessException {
        super(title);
        this.manager = manager;
        this.setSize(600, 500); // 기본 창 크기 설정
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // 창 닫기 설정
        this.setLocationRelativeTo(null); // 화면 중앙에 배치
        initLayout(); // 레이아웃 초기화
        this.setVisible(true);

        // 단어장 데이터가 비어 있는 경우 알림 및 초기화
        if (manager.voc == null || manager.voc.isEmpty()) {
            JOptionPane.showMessageDialog(this, "단어장이 비어 있습니다.");
            manager.run(filePath); // 데이터 로드
        }
    }

    /**
     * private void initLayout()
     * 레이아웃 초기화 메서드: Center, South, Menu 패널을 초기화.
     */
    private void initLayout() {
        initCenterLayout(); // 문제 및 결과 출력 영역
        initSouthLayout();  // 입력 영역
        initMenuPanel();    // 메뉴 버튼 영역
    }

    /**
     * private void initCenterLayout()
     * Center 영역 초기화.
     * 주관식, 객관식 문제 출력 및 결과 표시.
     */
    private void initCenterLayout() {
        centerPanel = new JPanel(new BorderLayout());
        displayArea = new JTextArea();
        displayArea.setEditable(false); // 사용자 입력 비활성화
        displayArea.setLineWrap(true);  // 자동 줄바꿈 활성화
        displayArea.setWrapStyleWord(true); // 단어 단위로 줄바꿈

        JScrollPane scrollPane = new JScrollPane(displayArea);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS); // 항상 세로 스크롤 표시
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER); // 가로 스크롤 없음
        centerPanel.add(scrollPane, BorderLayout.CENTER);

        optionsPanel = new JPanel(); // 객관식 답안을 표시하는 패널
        centerPanel.add(optionsPanel, BorderLayout.SOUTH);
        frame.add(centerPanel, BorderLayout.CENTER);
    }

    /**
     * private void clearDisplay()
     * 기존 출력 내용을 초기화하는 메서드.
     * 텍스트 영역 및 테이블 데이터를 모두 제거.
     */
    private void clearDisplay() {
        displayArea.setText(""); // 텍스트 영역 초기화
        optionsPanel.removeAll(); // 객관식 옵션 제거
        optionsPanel.revalidate();
        optionsPanel.repaint();

        if (table != null) {
            // 테이블이 존재하면 제거
            Container parent = table.getParent();
            while (parent != null && !(parent instanceof JScrollPane)) {
                parent = parent.getParent();
            }
            if (parent != null) {
                frame.remove(parent);
            }
            table = null; // 테이블 참조 해제
        }

        // 레이아웃 갱신
        centerPanel.revalidate();
        centerPanel.repaint();
        southPanel.revalidate();
        southPanel.repaint();
    }

    /**
     *  private void initSouthLayout()
     * South 영역 초기화: 사용자의 입력을 위한 레이아웃 설정.
     */
    private void initSouthLayout() {
        southPanel = new JPanel(new FlowLayout());
        frame.add(southPanel, BorderLayout.SOUTH);
    }
    /**
     * private void initMenuPanel()
     * 메뉴 패널 초기화 메서드.
     *
     * - 상단 메뉴 패널(menuPanel)에 퀴즈 시작, 오답 노트, 단어 검색, 종료와 같은 버튼을 생성하고 이벤트를 설정
     * - 각 버튼은 해당 기능을 호출하거나 특정 화면 요소를 조작
     *
     * 주요 동작:
     * 1. 메뉴 패널의 레이아웃 설정 (`GridLayout`으로 1행 5열 구성)
     * 2. 버튼 생성 및 이벤트 리스너 등록
     * 3. 메뉴 버튼과 화면 패널 간의 상호작용 처리
     */
    private void initMenuPanel() {
        // 메뉴 패널 레이아웃 설정: 1행 5열의 버튼 배치
        menuPanel.setLayout(new GridLayout(1, 5));

        // 메뉴 버튼 생성
        JButton subjectiveQuizButton = new JButton("1) 주관식 퀴즈"); // 주관식 퀴즈 시작 버튼
        JButton multipleChoiceQuizButton = new JButton("2) 객관식 퀴즈"); // 객관식 퀴즈 시작 버튼
        JButton incorrectNotesButton = new JButton("3) 오답노트"); // 오답 노트 표시 버튼
        JButton searchWordButton = new JButton("4) 단어검색"); // 단어 검색 시작 버튼
        JButton exitButton = new JButton("5) 종료"); // 프로그램 종료 버튼

        /**
         * 주관식 퀴즈 버튼 이벤트 리스너.
         * - 기존 데이터를 제거하고 화면 레이아웃을 주관식 퀴즈에 맞게 조정.
         */
        subjectiveQuizButton.addActionListener(e -> {
            removeTableData(); // 테이블 데이터 제거
            header = new String[] {}; // 테이블 헤더 초기화 (퀴즈에서는 필요 없음)
            disableMenuButtons(menuPanel); // 메뉴 버튼 비활성화

            // Center 패널 크기 조정 및 표시
            centerPanel.setSize(this.getWidth() - 10, 390); // 가로 크기 조정
            centerPanel.setVisible(true);
            southPanel.removeAll(); // 하단 패널 초기화
            centerPanel.revalidate(); // UI 갱신
            centerPanel.repaint();

            startSubjectiveQuiz(); // 주관식 퀴즈 시작
        });

        /**
         * 객관식 퀴즈 버튼 이벤트 리스너.
         * - 기존 데이터를 제거하고 화면 레이아웃을 객관식 퀴즈에 맞게 조정.
         */
        multipleChoiceQuizButton.addActionListener(e -> {
            removeTableData(); // 테이블 데이터 제거
            header = new String[] {}; // 테이블 헤더 초기화 (퀴즈에서는 필요 없음)
            disableMenuButtons(menuPanel); // 메뉴 버튼 비활성화

            // Center 패널 크기 조정 및 표시
            centerPanel.setSize(this.getWidth() - 10, 280); // 가로 크기 조정
            centerPanel.setVisible(true);
            southPanel.removeAll(); // 하단 패널 초기화
            centerPanel.revalidate(); // UI 갱신
            centerPanel.repaint();

            startMultipleChoiceQuiz(); // 객관식 퀴즈 시작
        });

        /**
         * 오답 노트 버튼 이벤트 리스너.
         * - 오답 데이터를 테이블에 표시하며, 화면 레이아웃을 오답 노트 모드로 전환.
         */
        incorrectNotesButton.addActionListener(e -> {
            showIncorrectNotes(); // 오답 노트 화면 준비 및 데이터 표시
        });

        /**
         * 단어 검색 버튼 이벤트 리스너.
         * - 단어 검색 화면으로 전환하고, 검색 UI 초기화.
         */
        searchWordButton.addActionListener(e -> {
            promptWordSearch(); // 단어 검색 화면 전환
            initstudy(); // 단어 검색 UI 초기화
        });

        /**
         * 종료 버튼 이벤트 리스너.
         * - 프로그램을 종료합니다.
         */
        exitButton.addActionListener(e -> System.exit(0)); // 애플리케이션 종료

        // 메뉴 패널에 버튼 추가 (순서대로 추가)
        menuPanel.add(subjectiveQuizButton);
        menuPanel.add(multipleChoiceQuizButton);
        menuPanel.add(incorrectNotesButton);
        menuPanel.add(searchWordButton);
        menuPanel.add(exitButton);

        // 프레임의 상단(BorderLayout.NORTH)에 메뉴 패널 추가
        frame.add(menuPanel, BorderLayout.NORTH);
    }

    /**
     * private void startSubjectiveQuiz()
     * 주관식 퀴즈를 시작
     */
    private void startSubjectiveQuiz() {
        clearDisplay();
        manager.subjectiveQuizGUI(displayArea, southPanel, menuPanel);
    }

    /**
     * startMultipleChoiceQuiz()
     *
     * 이 메서드는 객관식 퀴즈를 시작합니다. 퀴즈를 시작하기 전에 화면을 초기화한 후,
     * manager 객체를 사용하여 퀴즈 GUI를 표시합니다. 퀴즈 진행 중 예외가 발생할 경우,
     * 해당 예외를 처리하고 오류 메시지를 사용자에게 알립니다.
     *
     * 예외가 발생할 경우:
     * 1. 오류 메시지를 사용자에게 표시합니다.
     */
    private void startMultipleChoiceQuiz() {
        clearDisplay();  // 화면을 초기화하여 퀴즈 시작을 위한 준비
        try {
            // 퀴즈 GUI를 시작하는 메서드 호출
            manager.multipleChoiceQuizGUI(displayArea, southPanel, menuPanel);
        } catch (Exception e) {
            // 예외가 발생했을 때, 오류 메시지를 사용자에게 표시
            JOptionPane.showMessageDialog(null, "퀴즈 실행 중 오류가 발생했습니다: " + e.getMessage(), "오류", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * private void showIncorrectNotes()
     * 오답노트를 표시합니다.
     */
    private void showIncorrectNotes() {
        clearDisplay();
        centerPanel.setVisible(false);
        setOptionsPanelVisible(false);
        southPanel.removeAll();

        if (table == null) {
            initTabel(); // 테이블이 초기화되지 않은 경우 초기화
        }
        showIncorrectNotes(table); // 테이블을 사용하여 데이터 표시
    }

    /**
     * public void showIncorrectNotes(JTable table)
     * 오답노트 데이터를 JTable에 표시합니다.
     *
     * @param table 데이터를 표시할 JTable 객체
     *              - JTable은 테이블 형식으로 데이터를 화면에 출력하는 Swing 컴포넌트입니다.
     *              - 이 메서드는 오답 단어 데이터를 테이블에 채워 화면에 표시합니다.
     */
    public void showIncorrectNotes(JTable table) {
        // manager를 통해 오답 단어 리스트를 가져옴
        List<Word> incorrectWords = manager.getIncorrectWords();

        // 오답 단어가 없을 경우 사용자에게 알림 메시지를 표시하고 종료
        if (incorrectWords.isEmpty()) {
            JOptionPane.showMessageDialog(null, "틀린 문제가 없습니다.");
            return;
        }

        // 테이블에 표시할 컬럼 이름 정의
        String[] columnNames = {"영단어", "뜻", "출제회수", "오답회수"};

        // 오답 단어 데이터를 저장할 2차원 배열 생성
        Object[][] data = new Object[incorrectWords.size()][4];

        // 데이터 배열에 오답 단어 정보 채우기
        for (int i = 0; i < incorrectWords.size(); i++) {
            Word word = incorrectWords.get(i);
            data[i][0] = word.eng;       // 단어(영어)
            data[i][1] = word.kor;       // 뜻(한글)
            data[i][2] = word.count;     // 출제 횟수
            data[i][3] = word.wrongCount; // 오답 횟수
        }

        // 데이터와 컬럼 이름으로 새로운 테이블 모델 생성
        DefaultTableModel tableModel = new DefaultTableModel(data, columnNames);

        // JTable에 생성된 모델을 설정하여 화면에 데이터 표시
        table.setModel(tableModel);
    }

    /**
     * private void initstudy()
     * 단어 검색 기능을 초기화합니다.
     * 이 메서드는 단어 검색, 정렬, 데이터 초기화를 포함한 UI 요소들을 구성합니다.
     */
    private void initstudy() {
        // 테이블 헤더 설정
        header = new String[]{"영단어", "뜻", "출제회수", "오답회수"};

        // 기존 하단 패널 초기화
        southPanel.removeAll();
        southPanel.setLayout(new FlowLayout());
        setSouthPanelVisible(true); // 하단 패널 보이기 설정

        // 단어 검색 필드와 레이블 추가
        southPanel.add(new JLabel("검색할 단어"));
        JTextField textField = new JTextField(10); // 검색어 입력 필드 (길이 10)

        // Enter 키 입력 시 검색 동작 정의
        textField.addActionListener(e -> {
            try {
                textField.requestFocusInWindow();
                // 검색어로 단일 단어 검색
                Word w = manager.searchWord(textField.getText());


                if (w == null) {
                    // 단어가 없는 경우 사용자에게 알림
                    JOptionPane.showMessageDialog(this, "단어장에 등록된 단어가 아닙니다.");
                    return;
                }
                removeTableData(); // 기존 테이블 데이터 삭제
                model.addRow(new Object[]{w.eng, w.kor, w.count, w.wrongCount}); // 검색된 단어를 테이블에 추가
                textField.setText(""); // 검색 필드 초기화
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "단어 검색 중 오류가 발생했습니다: " + ex.getMessage());
            }
        });
        southPanel.add(textField);

        // "검색" 버튼 추가 및 클릭 이벤트 정의
        JButton btn = new JButton("검색");
        btn.addActionListener(e -> {
            try {
                // 입력한 단어로 여러 단어를 검색
                List<Word> list = manager.searchWord2(textField.getText());
                if (!list.isEmpty()) {
                    removeTableData(); // 기존 데이터 삭제
                    for (Word w : list) {
                        // 검색 결과를 테이블에 추가
                        model.addRow(new Object[]{w.eng, w.kor, w.count, w.wrongCount});
                    }
                    textField.setText(""); // 검색 필드 초기화
                } else {
                    JOptionPane.showMessageDialog(this, "찾는 단어가 없습니다.");
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "단어 검색 중 오류가 발생했습니다: " + ex.getMessage());
            }
        });
        southPanel.add(btn);

        // 정렬 옵션 추가: 오름차순(Ascending)
        JRadioButton asc = new JRadioButton("Asc");
        asc.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                flag = true; // 오름차순 플래그 설정
                initTabelData(); // 테이블 데이터 다시 초기화
            }
        });
        asc.setSelected(true); // 기본값으로 오름차순 선택

        // 정렬 옵션 추가: 내림차순(Descending)
        JRadioButton desc = new JRadioButton("Desc");
        desc.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                flag = false; // 내림차순 플래그 설정
                initTabelData(); // 테이블 데이터 다시 초기화
            }
        });

        // 라디오 버튼 그룹화하여 하나만 선택 가능하도록 설정
        ButtonGroup group = new ButtonGroup();
        group.add(asc);
        group.add(desc);

        // 하단 패널에 정렬 옵션 추가
        southPanel.add(asc);
        southPanel.add(desc);

        // 하단 패널 갱신
        southPanel.revalidate();
        southPanel.repaint();
    }

    /**
     * private void removeTableData()
     * 테이블 데이터를 제거합니다.
     */
    private void removeTableData() {
        if (model != null && model.getRowCount() > 0) {
            model = new DefaultTableModel(header, 0);
            table.setModel(model);
        }
    }

    /**
     * private void initTabelData()
     * 테이블 데이터를 초기화하고 정렬합니다.
     */
    private void initTabelData() {
        removeTableData();

        List<Word> list;
        if (flag) {
            list = manager.voc.stream()
                    .sorted((o1, o2) -> o1.eng.compareTo(o2.eng))
                    .toList();
        } else {
            list = manager.voc.stream()
                    .sorted((o1, o2) -> o2.eng.compareTo(o1.eng))
                    .toList();
        }

        if (model != null) {
            for (Word word : list) {
                model.addRow(new Object[]{word.eng, word.kor, word.count, word.wrongCount});
            }
        }
    }

    /**
     * private void initTabel()
     * 테이블(JTable)을 초기화합니다.
     *
     * - 화면에 단어 데이터를 표시할 테이블을 생성하거나, 기존 테이블의 데이터를 초기화합니다.
     * - 테이블은 `DefaultTableModel`을 사용하여 데이터를 관리합니다.
     * - 테이블은 `JScrollPane`으로 감싸져 스크롤이 가능하도록 구성됩니다.
     */
    private void initTabel() {
        if (table == null) {
            // 테이블 모델 초기화: 헤더 정보와 초기 데이터(빈 상태)를 설정
            this.model = new DefaultTableModel(header, 0);

            // JTable 생성 및 모델 연결
            this.table = new JTable(model);

            // JTable을 JScrollPane으로 감싸 스크롤 가능하도록 설정
            JScrollPane scrollPane = new JScrollPane(table);
            table.setFillsViewportHeight(true); // 테이블이 뷰포트의 높이를 채우도록 설정

            // 메인 프레임에 테이블 추가 (중앙 영역에 배치)
            frame.add(scrollPane, BorderLayout.CENTER);
        } else {
            // 기존 테이블이 이미 존재하는 경우 데이터를 초기화
            removeTableData();
        }
    }

    /**
     * public void promptWordSearch()
     * 단어 검색 UI를 초기화하고 표시합니다.
     *
     * - 이 메서드는 단어 검색을 위한 테이블, 데이터 초기화, 패널 상태를 설정합니다.
     * - 검색 UI를 활성화하여 사용자로 하여금 단어를 검색할 수 있도록 합니다.
     */
    public void promptWordSearch() {
        // 화면 초기화: 기존 UI 구성 요소를 지우고 검색 UI를 준비
        clearDisplay();

        // 중간 패널 숨기기 (필요 시 다른 UI 요소를 차단)
        centerPanel.setVisible(false);

        // 객관식 문제 옵션 패널 숨기기
        setOptionsPanelVisible(false);

        // 테이블 초기화 및 화면에 표시
        initTabel();

        // 테이블 데이터를 초기화 및 로드
        initTabelData();
    }

    /**
     * private void setSouthPanelVisible(boolean visible)
     * 남쪽 패널(southPanel)의 가시성을 설정합니다.
     *
     * @param visible true일 경우 패널을 화면에 표시하고, false일 경우 숨깁니다.
     *                - 검색 기능이나 기타 UI 요소가 포함된 패널의 표시 여부를 제어합니다.
     */
    private void setSouthPanelVisible(boolean visible) {
        southPanel.setVisible(visible);
    }

    /**
     * private void setOptionsPanelVisible(boolean visible)
     * 객관식 문제 옵션 패널(optionsPanel)의 가시성을 설정합니다.
     *
     * @param visible true일 경우 패널을 화면에 표시하고, false일 경우 숨깁니다
     *                - 객관식 문제와 관련된 UI를 표시하거나 숨길 때 사용됩니다
     */
    private void setOptionsPanelVisible(boolean visible) {
        optionsPanel.setVisible(visible);
    }

}