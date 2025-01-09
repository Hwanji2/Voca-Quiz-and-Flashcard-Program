package homework;

import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import javax.swing.*;

/**
 * @author 202410647 이지환
 * @since 2024-11-18
 *
 * VocManager 클래스
 *
 * 이 클래스는 사용자의 단어장을 관리하는 기능을 제공합니다.
 * 사용자가 단어를 암기하고 복습할 수 있도록 다양한 퀴즈 및 학습 모드를 지원합니다.
 *
 */

public class VocManager
{
    // 단어장 이름
    String name;

    // 단어를 저장할 LinkedList
    LinkedList<Word> voc = new LinkedList<>();

    // Scanner 객체 (사용자 입력을 받기 위한 객체)
    static Scanner scanner = new Scanner(System.in);

    // 랜덤 객체 (퀴즈에서 랜덤하게 단어를 선택하기 위한 객체)
    Random random = new Random();

    /**
     * public VocManager(String name)
     * VocManager 생성자.
     *
     * @param name 단어장의 이름을 지정하는 문자열.
     */
    public VocManager(String name) {
        this.name = name;
    }

    /**
     * public void addWord(Word w)
     * 단어를 단어장에 추가하는 메서드.
     *
     * @param w 추가할 단어 객체. Word 클래스의 인스턴스를 전달해야 합니다.
     */
    public void addWord(Word w) {
        if (!voc.add(w)) {
            System.out.println("더이상 단어를 추가할 수 없습니다.");
        }
    }

    /**
     * public void run(String filename)
     * 단어장 파일을 읽고 단어를 추가하는 메서드.
     *
     * 이 메서드는 지정된 파일에서 단어들을 읽어 단어장에 추가합니다. 각 단어는 탭("\t") 문자로 구분되어 있으며,
     * 단어장이 생성되면 MainFrame을 호출하여 프로그램을 시작합니다.
     * 또한, 메서드는 사용자가 입력한 파일을 바탕으로 단어장과 프로그램을 초기화합니다.
     *
     * @param filename 단어가 저장된 파일의 경로
     */
    public void run(String filename) {
        try {
            // 파일에서 단어 읽기
            Scanner scan = new Scanner(new File(filename));
            while (scan.hasNextLine()) {
                String line = scan.nextLine();
                StringTokenizer st = new StringTokenizer(line, "\t");
                String eng = st.nextToken();
                String kor = st.nextToken();
                this.addWord(new Word(eng.trim(), kor.trim())); // 단어 추가
            }
            System.out.println(name + "의 단어장이 생성되었습니다.");
            new MainFrame("이지환의 단어장 프로그램", this, filename); // MainFrame 호출
            menu(); // 메뉴 호출

        } catch (FileNotFoundException e) {
            System.out.println("파일을 찾을 수 없습니다.");
        } catch (Exception e) {
            System.out.println("단어장 생성 중 문제가 발생했습니다.");
        }
    }

    /**
     * public void menu()
     * 프로그램의 메뉴를 출력하고 선택된 옵션에 따라 적절한 동작을 수행하는 메서드.
     *
     * 사용자가 메뉴에서 선택한 옵션에 따라 퀴즈, 오답노트, 단어 검색 등을 실행합니다.
     * 선택한 옵션에 따라 적절한 메서드를 호출하고, 사용자가 잘못된 입력을 했을 때 적절한 예외 처리를 통해 프로그램이 종료되지 않도록 처리합니다.
     *
     */
    public void menu() {
        int choice = 0;
        while (choice != 5) {
            // 메뉴 출력
            System.out.println("1) 주관식 퀴즈  2) 객관식 퀴즈  3) 오답노트  4) 단어검색  5) 종료");
            System.out.print("메뉴를 선택하세요 : ");
            try {
                // 사용자 입력 받기
                choice = scanner.nextInt();
                scanner.nextLine(); // 버퍼 비우기
                switch (choice) {
                    case 1 -> subjectiveQuiz(); // 주관식 퀴즈 실행
                    case 2 -> multipleChoiceQuiz(); // 객관식 퀴즈 실행
                    case 3 -> showIncorrectNotes(); // 오답 노트 실행
                    case 4 -> searchWord(); // 단어 검색 실행
                    case 5 -> {
                        System.out.println("프로그램을 종료합니다.");
                        System.exit(0); // 프로그램 종료
                    }
                    default -> System.out.println("잘못된 선택입니다. 다시 시도하세요.");
                }
            } catch (InputMismatchException e) {
                // 사용자가 숫자가 아닌 값을 입력한 경우 발생하는 예외 처리
                System.out.println("숫자를 입력하세요.");
                scanner.nextLine(); // 입력 버퍼 초기화
            }
        }
    }

    /**
     * public void subjectiveQuiz()
     * 주관식 퀴즈 메서드 (GUI)
     *
     * 사용자가 주어진 한국어 뜻(복합적일 수 있음)에 해당하는 영어 단어를 맞추는 주관식 퀴즈를 진행합니다.
     * 단어는 중복되지 않도록 출제되며, 퀴즈는 총 10문제로 구성됩니다.
     * 문제 출제, 정답 확인, 오답 처리, 퀴즈 결과를 출력하는 기능을 포함합니다.
     *
     * 주요 기능:
     * 1. 중복되지 않은 뜻을 기준으로 문제 생성.
     * 2. 사용자 입력으로 정답/오답 여부 확인.
     * 3. 정답 시 출제된 뜻에 대한 단어의 출제 횟수 증가.
     * 4. 오답 시 해당 단어의 오답 횟수 증가.
     * 5. 총 퀴즈 시간과 정답 개수를 출력.
     *
     * 사전 조건:
     *    - `String eng`: 영어 단어.
     *    - `String kor`: '/'로 구분된 한국어 뜻.
     *    - `int count`: 출제된 횟수.
     *    - `int wrongCount`: 오답 횟수.
     *
     * 퀴즈 종료 조건:
     * - 10문제
     *
     *모두 출제하거나, 문제로 사용할 단어가 부족한 경우.
     *
     * 로직:
     * 1. `voc` 리스트를 기반으로 문제를 중복되지 않도록 준비.
     * 2. 준비된 문제를 섞어서 사용자에게 출력.
     * 3. 사용자 입력을 받아 정답 여부를 판단.
     * 4. 퀴즈 결과(정답 개수, 소요 시간)를 출력.
     *
     */
    public void subjectiveQuiz() {
        List<Word> shuffledVoc = new ArrayList<>(voc);
        Collections.shuffle(shuffledVoc);
        Set<String> usedMeanings = new HashSet<>();
        List<Word> quizQuestions = new ArrayList<>();

        // 중복을 피하고 랜덤하게 10개의 문제 준비
        for (Word word : shuffledVoc) {
            String[] meanings = word.kor.split("/");

            boolean isDuplicated = Arrays.stream(meanings).allMatch(usedMeanings::contains);
            if (!isDuplicated) {
                quizQuestions.add(word);
                Arrays.stream(meanings).forEach(usedMeanings::add);
            }

            if (quizQuestions.size() == 10) {
                break;
            }
        }

        if (quizQuestions.size() < 10) {
            System.out.println("출제할 문제가 충분하지 않습니다. 퀴즈를 중단합니다.");
            return;
        }

        Collections.shuffle(quizQuestions);
        int correctCount = 0;
        long startTime = System.currentTimeMillis();
        Scanner scanner = new Scanner(System.in);

        for (int i = 0; i < 10; i++) {
            Word word = quizQuestions.get(i);
            System.out.println("------주관식 퀴즈 " + (i + 1) + "번 ------");
            System.out.println("\"" + word.kor + "\"의 뜻을 가진 영어 단어는 무엇일까요?");
            System.out.print("답을 입력하세요: ");
            String answer = scanner.nextLine().trim();

            // 출제 횟수 증가: 같은 뜻을 공유하는 모든 단어에 대해 증가
            voc.stream()
                    .filter(w -> sharesMeaning(w, word))
                    .forEach(w -> w.count++);

            // 정답 체크
            boolean isCorrect = voc.stream()
                    .filter(w -> sharesMeaning(w, word))
                    .anyMatch(w -> w.eng.equalsIgnoreCase(answer));

            if (isCorrect) {
                correctCount++;
                System.out.println("정답입니다!");
            } else {
                System.out.println("틀렸습니다. 정답은 " + word.eng + "입니다.");

                // 오답 횟수 증가: 같은 뜻을 공유하는 모든 단어에 대해 증가
                voc.stream()
                        .filter(w -> sharesMeaning(w, word))
                        .forEach(w -> w.wrongCount++);
            }
        }

        long endTime = System.currentTimeMillis();
        System.out.println("\n" + this.name + "님 10문제 중 " + correctCount + "개 맞추셨고, 총 " + (endTime - startTime) / 1000 + "초 소요되었습니다.");
    }

    /**
     * public void multipleChoiceQuiz()
     * 객관식 퀴즈 메서드 (GUI)
     *
     * 단어와 뜻을 기반으로 10개의 객관식 퀴즈를 출제하는 메서드입니다.
     * 각 문제는 4지 선다형이며, 사용자 입력으로 정답을 선택합니다.
     *
     * 주요 기능:
     * 1. `voc` 리스트를 기반으로 문제를 생성하며, 중복된 뜻은 제외.
     * 2. 각 문제는 1개의 정답과 3개의 오답으로 구성.
     * 3. 사용자의 입력을 통해 정답 여부를 확인하고 결과를 출력.
     *
     *
     * 퀴즈 종료 조건:
     * - `voc`의 단어 수가 10개 미만일 경우.
     * - 문제 생성을 위한 단어 부족.
     *
     * 로직:
     * 1. 단어 리스트 `voc`를 섞어서 문제를 구성.
     * 2. 중복되지 않은 뜻으로 정답과 오답을 선택.
     * 3. 4개의 선지를 구성하여 문제 리스트에 추가.
     * 4. 사용자 입력으로 정답 확인 및 결과 처리.
     * 5. 최종 결과 출력: 정답 개수와 소요 시간.
     *
     * @throws NumberFormatException 사용자가 숫자가 아닌 값을 입력했을 경우 발생하는 예외
     * @throws NoSuchElementException 사용자 입력을 받을 수 없을 경우 발생하는 예외
     *
     */
    public void multipleChoiceQuiz() {
        // 단어 리스트 섞기
        Collections.shuffle(voc);

        // 정답 개수 추적 변수
        int correctCount = 0;
        long startTime = System.currentTimeMillis();

        // 문제 생성 가능 여부 확인
        if (voc.size() < 10) {
            System.out.println("단어 개수가 부족하여 10문제를 구성할 수 없습니다.");
            return;
        }

        // 문제 및 선지 구성
        List<Question> questions = new ArrayList<>();
        Set<String> usedMeanings = new HashSet<>();

        // 문제 생성
        for (int i = 0; i < 10; i++) {
            Word correctWord = null;

            // 중복되지 않은 정답 선택
            for (Word word : voc) {
                String[] meanings = word.kor.split("/");
                boolean hasOverlap = Arrays.stream(meanings).anyMatch(usedMeanings::contains);

                if (!hasOverlap) {
                    correctWord = word;
                    Arrays.stream(meanings).forEach(usedMeanings::add); // 정답의 뜻 추가
                    break;
                }
            }

            if (correctWord == null) {
                System.out.println("중복되지 않은 단어로 문제를 구성할 수 없습니다.");
                return;  // 문제를 만들 수 없는 경우 종료
            }

            // 정답의 한국어 뜻 분리
            List<String> correctMeanings = Arrays.asList(correctWord.kor.split("/"));

            // 정답 포함한 랜덤 4지 선다 구성
            List<Word> filteredVoc = new ArrayList<>();
            for (Word word : voc) {
                if (word.equals(correctWord)) continue;

                List<String> wordMeanings = Arrays.asList(word.kor.split("/"));
                boolean hasOverlap = correctMeanings.stream().anyMatch(wordMeanings::contains) ||
                        wordMeanings.stream().anyMatch(usedMeanings::contains);

                if (!hasOverlap) {
                    filteredVoc.add(word);
                }
            }

            // 선지가 4개 이상이 될 때까지 랜덤으로 선택
            Collections.shuffle(filteredVoc);
            List<Word> options = new ArrayList<>();
            options.add(correctWord);

            for (Word choice : filteredVoc) {
                if (options.size() == 4) break;
                options.add(choice);
            }

            // 선지 개수가 부족하면 종료
            if (options.size() < 4) {
                System.out.println("선지를 구성할 수 있는 단어가 부족합니다. 퀴즈를 종료합니다.");
                return;
            }

            // 선지 뜻을 사용된 뜻 목록에 추가
            options.forEach(opt -> Arrays.stream(opt.kor.split("/")).forEach(usedMeanings::add));

            // 문제 저장
            questions.add(new Question(correctWord, options));
        }

        // 생성된 문제 수가 10개 미만이면 종료
        if (questions.size() < 10) {
            System.out.println("퀴즈 문제를 10개 생성하지 못했습니다.");
            return;
        }

        // 퀴즈 시작
        for (int i = 0; i < 10; i++) {
            Question currentQuestion = questions.get(i);
            System.out.println("------객관식 퀴즈 " + (i + 1) + "번 ------");
            System.out.println(currentQuestion.correctWord.eng + "의 뜻은 무엇일까요?");
            Collections.shuffle(currentQuestion.options);

            // 선지 출력
            for (int j = 0; j < currentQuestion.options.size(); j++) {
                System.out.println((j + 1) + ") " + currentQuestion.options.get(j).kor);
            }

            try {
                System.out.print("답을 입력하세요 : ");
                Scanner scanner = new Scanner(System.in);
                int userChoice = Integer.parseInt(scanner.nextLine()) - 1;

                // 정답 확인
                Word selectedWord = currentQuestion.options.get(userChoice);
                if (selectedWord.equals(currentQuestion.correctWord)) {
                    correctCount++;
                    System.out.println("정답입니다!");
                    increaseCountForSharedMeanings(currentQuestion.correctWord, true);  // 정답 처리
                } else {
                    System.out.println("틀렸습니다. 정답은 \"" + currentQuestion.correctWord.kor + "\"입니다.");
                    increaseCountForSharedMeanings(currentQuestion.correctWord, false); // 오답 처리
                }
            } catch (NumberFormatException e) {
                System.out.println("잘못된 입력입니다. 오답 처리됩니다.");
                increaseCountForSharedMeanings(currentQuestion.correctWord, false); // 오답 처리
            } catch (NoSuchElementException e) {
                System.out.println("입력 오류가 발생했습니다. 프로그램을 종료합니다.");
                break;
            }
        }

        long endTime = System.currentTimeMillis();
        System.out.println("\n" + this.name + "님 10문제 중 " + correctCount + "개 맞추셨고, 총 " + (endTime - startTime) / 1000 + "초 소요되었습니다.");
    }
    /**
     * 같은 뜻을 공유하는 단어의 출제 횟수 및 오답 횟수를 증가시키는 메서드
     *
     * @param word      기준이 되는 단어
     * @param isCorrect 정답 여부
     */
    private void increaseCountForSharedMeanings(Word word, boolean isCorrect) {
        voc.stream()
                .filter(w -> sharesMeaning(w, word))
                .forEach(w -> {
                    w.count++; // 출제 횟수 증가
                    if (!isCorrect) {
                        w.wrongCount++; // 오답 횟수 증가
                    }
                });
    }
    /**
     * public void subjectiveQuizGUI(JTextArea displayArea, JPanel southPanel, JPanel menuPanel)
     * 주관식 퀴즈 GUI를 설정하고 실행하는 함수입니다.
     * 이 메서드는 10개의 주관식 퀴즈 문제를 출제하고, 사용자가 입력한 답을 맞췄는지 확인합니다.
     * 문제는 단어장에 있는 단어들 중에서 중복되지 않도록 출제되며, 사용자가 답을 제출하면 정답 여부를 체크하고,
     * 결과를 출력합니다. 퀴즈가 종료되면 사용자가 맞춘 개수와 소요 시간을 표시합니다.
     *
     * @param displayArea 퀴즈 진행 상태를 보여줄 JTextArea
     * @param southPanel 퀴즈 문제와 입력 필드를 포함한 패널
     * @param menuPanel 퀴즈 종료 후 메뉴 버튼을 포함한 패널
     *
     */
    public void subjectiveQuizGUI(JTextArea displayArea, JPanel southPanel, JPanel menuPanel) {
        southPanel.setSize(500, 240);
        southPanel.removeAll();
        southPanel.setLayout(new FlowLayout());
        southPanel.setVisible(true);

        JLabel label = new JLabel("정답 입력");
        JTextField textField = new JTextField(10);
        JButton submitButton = new JButton("제출");

        southPanel.add(label);
        southPanel.add(textField);
        southPanel.add(submitButton);

        List<Word> shuffledVoc = new ArrayList<>(voc);
        Collections.shuffle(shuffledVoc);
        Set<String> usedMeanings = new HashSet<>();
        List<Word> quizQuestions = new ArrayList<>();

        // 중복을 피하고 랜덤하게 10개의 문제 준비
        for (Word word : shuffledVoc) {
            String meaningKey = String.join("/", word.kor.split("/")).trim();
            if (!usedMeanings.contains(meaningKey)) {
                quizQuestions.add(word);
                usedMeanings.add(meaningKey);
            }
            if (quizQuestions.size() == 10) {
                break;
            }
        }

        if (quizQuestions.size() < 10) {
            JOptionPane.showMessageDialog(null, "출제할 문제가 충분하지 않습니다. 퀴즈를 중단합니다.");
            return;
        }

        int totalQuestions = quizQuestions.size();
        int[] correctCount = {0};
        int[] currentIndex = {0};
        long startTime = System.currentTimeMillis();

        displayArea.setText("------주관식 퀴즈 1번 ------\n\"" + quizQuestions.get(0).kor + "\"의 뜻을 가진 영어 단어는 무엇일까요?");

        submitButton.addActionListener(e -> {
            if (currentIndex[0] >= totalQuestions) {
                return;
            }

            Word currentWord = quizQuestions.get(currentIndex[0]);
            String answer = textField.getText().trim();

            if (!answer.isEmpty()) {
                // 출제 횟수 증가: 같은 뜻을 가진 모든 단어의 출제 횟수 증가
                voc.stream()
                        .filter(w -> sharesMeaning(w, currentWord))
                        .forEach(w -> w.count++);

                // 정답 체크
                boolean isCorrect = voc.stream()
                        .filter(w -> sharesMeaning(w, currentWord))
                        .anyMatch(w -> w.eng.equalsIgnoreCase(answer));

                if (isCorrect) {
                    correctCount[0]++;
                    displayArea.setText("정답입니다!");
                    JOptionPane.showMessageDialog(null, "정답입니다!");
                } else {
                    displayArea.setText("틀렸습니다. 정답은 " + currentWord.eng + "입니다.");
                    JOptionPane.showMessageDialog(null, "틀렸습니다. 정답은 " + currentWord.eng + "입니다.");

                    // 오답 횟수 증가: 같은 뜻을 가진 모든 단어의 오답 횟수 증가
                    voc.stream()
                            .filter(w -> sharesMeaning(w, currentWord))
                            .forEach(w -> w.wrongCount++);
                }

                currentIndex[0]++;

                if (currentIndex[0] < totalQuestions) {
                    Word nextWord = quizQuestions.get(currentIndex[0]);
                    displayArea.setText("------주관식 퀴즈 " + (currentIndex[0] + 1) + "번 ------\n\"" + nextWord.kor + "\"의 뜻을 가진 영어 단어는 무엇일까요?");
                } else {
                    long endTime = System.currentTimeMillis();
                    String resultText = "10문제 중 " + correctCount[0] + "개를 맞추셨고, 총 " + (endTime - startTime) / 1000 + "초 소요되었습니다.";
                    displayArea.setText(resultText);
                    JOptionPane.showMessageDialog(null, resultText, "퀴즈 결과", JOptionPane.INFORMATION_MESSAGE);
                    textField.setEnabled(false);
                    submitButton.setEnabled(false);
                    enableMenuButtons(menuPanel);
                }
            }

            textField.setText("");
        });

        textField.addActionListener(e -> {
            submitButton.doClick();
        });
    }

    /**
     * 같은 뜻을 공유하는지 확인하는 메서드
     *
     * @param w1 비교할 첫 번째 단어
     * @param w2 비교할 두 번째 단어
     * @return 두 단어가 같은 뜻을 하나라도 공유하면 true, 그렇지 않으면 false
     */
    private boolean sharesMeaning(Word w1, Word w2) {
        Set<String> meanings1 = new HashSet<>(Arrays.asList(w1.kor.split("/")));
        Set<String> meanings2 = new HashSet<>(Arrays.asList(w2.kor.split("/")));
        meanings1.retainAll(meanings2); // 교집합 계산
        return !meanings1.isEmpty(); // 교집합이 비어있지 않으면 같은 뜻을 공유하는 것으로 간주
    }


    /**
     * public void multipleChoiceQuizGUI(JTextArea displayArea, JPanel southPanel, JPanel menuPanel)
     * 객관식 퀴즈를 생성하고 GUI를 구성하는 메소드.
     *
     * 이 메소드는 주어진 단어 리스트에서 10개의 문제를 무작위로 선택하여 객관식 퀴즈를 생성합니다.
     * 각 문제에 대해 4개의 선택지를 무작위로 배치하고, 사용자가 문제를 풀고 제출할 수 있는 기능을 제공합니다.
     * 문제는 단어장에 있는 단어들 중에서 중복되지 않도록 출제되며, 사용자가 선택한 답을 제출하면
     * 정답 여부를 체크하고 결과를 출력합니다. 퀴즈가 종료되면 사용자가 맞춘 개수와 소요 시간을 표시합니다.
     *
     * @param displayArea 퀴즈 진행 상태를 보여줄 JTextArea
     * @param southPanel 퀴즈 문제와 선택지를 표시할 패널
     * @param menuPanel 퀴즈 종료 후 메뉴 버튼을 포함한 패널
     */
    public void multipleChoiceQuizGUI(JTextArea displayArea, JPanel southPanel, JPanel menuPanel) throws Exception {
        southPanel.setSize(500, 240);
        southPanel.removeAll();
        southPanel.setLayout(new GridLayout(0, 1)); // 1열로 세로로 배치
        southPanel.setVisible(true);

        // 문제 생성 전, voc 크기와 문제 생성 가능 여부 확인
        if (voc.size() < 10) {
            JOptionPane.showMessageDialog(null, "단어 개수가 부족하여 10문제를 구성할 수 없습니다.", "퀴즈 생성 실패", JOptionPane.ERROR_MESSAGE);
            enableMenuButtons(menuPanel);
            return;
        }

        JButton submitButton = new JButton("제출");
        southPanel.add(submitButton);

        Collections.shuffle(voc); // 단어 리스트 섞기
        final int[] correctCount = {0};
        long startTime = System.currentTimeMillis();

        int[] currentIndex = {0};
        List<Question> questions = new ArrayList<>();

        // 문제 및 선지 생성
        for (int i = 0; i < 10; i++) {
            Word correctWord = voc.get(i);

            // 정답 포함한 랜덤 4지 선다 구성
            List<Word> options = new ArrayList<>(voc);
            options.remove(correctWord);
            Collections.shuffle(options);
            options = options.subList(0, 3);
            options.add(correctWord);
            Collections.shuffle(options);

            questions.add(new Question(correctWord, options));
        }

        // 첫 번째 문제를 보여줌
        loadQuestion(displayArea, southPanel, submitButton, questions.get(currentIndex[0]), currentIndex[0]);

        // 제출 버튼 리스너
        submitButton.addActionListener(e -> {
            try {
                if (currentIndex[0] >= questions.size()) {
                    return;
                }

                Question currentQuestion = questions.get(currentIndex[0]);
                Word correctWord = currentQuestion.correctWord;

                int selectedIndex = currentQuestion.getSelectedOption();
                if (selectedIndex == -1) {
                    JOptionPane.showMessageDialog(null, "답변을 선택해주세요!", "경고", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                Word selectedWord = currentQuestion.options.get(selectedIndex);

                // 출제 횟수 증가: 같은 뜻을 공유하는 모든 단어에 대해 증가
                voc.stream()
                        .filter(w -> sharesMeaning(w, correctWord))
                        .forEach(w -> w.count++);

                if (selectedWord.equals(correctWord)) {
                    correctCount[0]++;
                    JOptionPane.showMessageDialog(null, "정답입니다!", "퀴즈 결과", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(null, "틀렸습니다. 정답은 \"" + correctWord.kor + "\"입니다.", "퀴즈 결과", JOptionPane.INFORMATION_MESSAGE);

                    // 오답 횟수 증가: 같은 뜻을 공유하는 모든 단어에 대해 증가
                    voc.stream()
                            .filter(w -> sharesMeaning(w, correctWord))
                            .forEach(w -> w.wrongCount++);
                }

                currentIndex[0]++;
                if (currentIndex[0] >= questions.size()) {
                    long endTime = System.currentTimeMillis();
                    String resultText = "10문제 중 " + correctCount[0] + "개를 맞추셨고, 총 " + (endTime - startTime) / 1000 + "초 소요되었습니다.";
                    displayArea.setText(resultText);
                    JOptionPane.showMessageDialog(null, resultText, "퀴즈 결과", JOptionPane.INFORMATION_MESSAGE);
                    enableMenuButtons(menuPanel);
                } else {
                    loadQuestion(displayArea, southPanel, submitButton, questions.get(currentIndex[0]), currentIndex[0]);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "문제 처리 중 오류가 발생했습니다: " + ex.getMessage(), "오류", JOptionPane.ERROR_MESSAGE);
            }
        });
    }



    /**
     * private void loadQuestion(JTextArea displayArea, JPanel southPanel, JButton submitButton, Question question, int questionIndex)
     * 주어진 문제를 GUI에 표시하는 메소드.
     *
     * 이 메소드는 주어진 문제와 선택지를 화면에 표시합니다. 사용자가 선택할 수 있는 4개의 선지를 추가하고,
     * 선택된 답변을 추적합니다. 문제를 표시하고 선택지를 동적으로 생성합니다.
     *
     * @param displayArea 문제를 출력할 JTextArea 컴포넌트
     * @param southPanel 선택지를 표시할 JPanel 컴포넌트
     * @param submitButton 제출 버튼
     * @param question 현재 문제 (정답과 선택지를 포함)
     * @param questionIndex 현재 문제의 인덱스
     */
    private void loadQuestion(JTextArea displayArea, JPanel southPanel, JButton submitButton, Question question, int questionIndex) {
        // 문제 텍스트 업데이트
        displayArea.setText("------객관식 퀴즈 " + (questionIndex + 1) + "번 ------\n" + question.correctWord.eng + "의 뜻은 무엇일까요?\n");
        southPanel.removeAll(); // 이전 UI 초기화
        ButtonGroup group = new ButtonGroup();
        // 선택지 추가
        for (int i = 0; i < question.options.size(); i++) {
            JRadioButton optionButton = new JRadioButton((i + 1) + ") " + question.options.get(i).kor);
            group.add(optionButton);
            southPanel.add(optionButton);

            // 선택 상태를 저장
            int finalIndex = i;
            optionButton.addActionListener(e -> question.setSelectedOption(finalIndex));
        }

        southPanel.add(submitButton); // 제출 버튼 추가
        southPanel.revalidate(); // 레이아웃 재배치
        southPanel.repaint();    // UI 갱신
    }
    /**
     * public List<Word> getIncorrectWords()
     * 틀린 단어 목록을 반환하는 메소드.
     *
     * 이 메소드는 `voc` 리스트에서 틀린 횟수(`wrongCount`)가 0보다 큰 단어들만 필터링하여,
     * 그 중 틀린 횟수가 많은 순으로 정렬한 후 반환합니다.
     *
     * @return 틀린 문제들을 포함하는 `Word` 객체 리스트
     */
    public List<Word> getIncorrectWords() {
        // 'voc' 리스트에서 틀린 횟수가 0보다 큰 단어들을 필터링하고,
        // 이를 틀린 횟수가 많은 순서대로 정렬하여 새 리스트에 저장
        List<Word> incorrectWords = voc.stream()
                .filter(word -> word.wrongCount > 0) // 'wrongCount'가 0보다 큰 단어 필터링
                .sorted((w1, w2) -> Integer.compare(w2.wrongCount, w1.wrongCount)) // 'wrongCount'가 많은 순으로 정렬
                .collect(Collectors.toList()); // 결과를 리스트로 수집
        return incorrectWords; // 정렬된 틀린 단어 리스트 반환
    }

    /**
     * @author 202410647 이지환
     * @since 2024-11-18
     *
     * Question 클래스
     * 중첩 클래스로 객관식 퀴즈의 문제와 선택지를 캡슐화하여 관리
     *
     */

    class Question {
        Word correctWord;    // 정답 단어 (객관식 문제의 정답)
        List<Word> options;  // 객관식 선택지 리스트 (정답을 포함한 4개의 선택지)
        private int selectedOption = -1; // 사용자가 선택한 옵션의 인덱스 (초기값: -1로 설정하여 선택 안 됨)

        /**
         * Question 객체의 생성자
         *
         * 이 생성자는 각 문제의 정답 단어와 선택지를 받아 객체를 초기화합니다.
         *
         * @param correctWord 객관식 문제의 정답이 되는 단어
         * @param options 객관식 문제의 선택지들 (정답을 포함한 4개의 단어)
         */
        public Question(Word correctWord, List<Word> options) {
            this.correctWord = correctWord; // 정답 단어 설정
            this.options = options; // 선택지 리스트 설정
        }

        /**
         * public int getSelectedOption()
         * 사용자가 선택한 옵션의 인덱스를 반환하는 메소드
         *
         * @return 사용자가 선택한 옵션의 인덱스 (초기값은 -1)
         */
        public int getSelectedOption() {
            return selectedOption; // 사용자가 선택한 옵션 인덱스 반환
        }

        /**
         * public void setSelectedOption(int index)
         * 사용자가 선택한 옵션의 인덱스를 설정하는 메소드
         *
         * @param index 사용자가 선택한 옵션의 인덱스 (0부터 시작)
         */
        public void setSelectedOption(int index) {
            this.selectedOption = index; // 사용자가 선택한 옵션의 인덱스를 설정
        }
    }

    /**
     * 주어진 영어 단어에 해당하는 단어를 검색하여 반환하는 메서드.
     *
     * @param eng 검색할 영어 단어
     * @return 해당하는 단어가 있으면 그 단어를 반환하고, 없으면 null을 반환
     */
    public Word searchWord(String eng) {
        // 입력된 단어를 공백 제거 후 저장
        String trimmedEng = eng.trim();

        // 1. 영어 단어로 검색
        for (Word w : voc) {
            if (w.eng.equalsIgnoreCase(trimmedEng)) {  // 대소문자 구분 없이 비교
                System.out.println(w);
                return w;
            }
        }

        // 2. 영어 단어로 찾지 못했을 경우, 한국어 뜻으로 검색
        for (Word w : voc) {
            String[] meanings = w.kor.split("/");  // 한국어 뜻을 '/'로 나눔
            for (String meaning : meanings) {
                if (meaning.trim().equals(trimmedEng)) {  // 정확히 일치하는 뜻 비교
                    System.out.println(w);
                    return w;
                }
            }
        }

        // 단어를 찾지 못한 경우
        System.out.println("단어장에 등록되지 않은 단어입니다.");
        return null;
    }


    /**
     * 주어진 영어 단어로 시작하는 모든 단어를 검색하여 리스트로 반환하는 메서드.
     *
     * @return 주어진 단어로 시작하는 모든 단어들의 리스트
     *         검색된 단어가 없으면 빈 리스트를 반환
     */


    /**
     * public void showIncorrectNotes()
     * 틀린 단어들을 출력하는 메소드.
     *
     * 이 메소드는 사용자가 퀴즈에서 틀린 단어들을 오답 횟수 순으로 정렬하여 출력합니다.
     * 오답이 없는 경우에는 "틀린 문제가 없습니다."라는 메시지를 출력합니다.
     */
    public void showIncorrectNotes() {
        List<Word> incorrectWords = voc.stream()
                .filter(word -> word.wrongCount > 0)
                .sorted((w1, w2) -> Integer.compare(w2.wrongCount, w1.wrongCount))
                .toList();

        // 리스트가 비어 있으면 "틀린 문제가 없습니다." 출력
        if (incorrectWords.isEmpty()) {
            System.out.println("틀린 문제가 없습니다.");
        } else {
            incorrectWords.forEach(word -> {
                System.out.println(word.eng + " 뜻 : " + word.kor);
                System.out.println("출제회수 : " + word.count + "   오답회수 : " + word.wrongCount);
                System.out.println("---------------------------------");
            });
        }
    }
    public List<Word> searchWord2(String eng) {
        List<Word> list = new ArrayList<>();

        // 입력된 검색어가 비어 있으면 검색을 수행하지 않음
        if (eng == null || eng.trim().isEmpty()) {
            System.out.println("검색어가 비어 있습니다. 검색을 수행하지 않습니다.");
            return list;
        }

        String trimmedEng = eng.trim();

        // 1. 한국어 뜻으로 먼저 검색
        for (Word w : voc) {
            String[] meanings = w.kor.split("/");  // 한국어 뜻을 '/'로 나눔
            for (String meaning : meanings) {
                if (meaning.trim().equals(trimmedEng)) {  // equals로 정확히 비교
                    list.add(w);
                    break;  // 뜻이 일치하면 더 이상 비교하지 않음
                }
            }
        }

        // 2. 한국어 뜻으로 검색한 결과가 없으면 영어 단어로 검색
        if (list.isEmpty()) {
            for (Word w : voc) {
                if (w.eng.equalsIgnoreCase(trimmedEng)) {  // 영어 단어는 대소문자 무시하고 비교
                    list.add(w);
                }
            }
        }

        // 검색 결과가 없으면 메시지 출력
        if (list.isEmpty()) {
            System.out.println("단어장에 등록되지 않은 단어입니다.");
        }

        return list;
    }


    /**
     * public void searchWord()
     * 단어를 검색하여 결과를 출력하는 메소드.
     *
     * 이 메소드는 사용자가 입력한 단어를 단어장에 검색하여, 해당 단어가 있을 경우
     * 영어 단어와 그 뜻, 출제 횟수, 오답 횟수를 출력합니다.
     * 단어가 단어장에 없을 경우 "단어장에 등록되지 않은 단어입니다."라는 메시지를 출력합니다.
     */
    public void searchWord() {
        System.out.print("검색할 단어를 입력하세요 : ");
        String eng = scanner.nextLine();
        Word found = voc.stream().filter(w -> w.eng.equalsIgnoreCase(eng)).findFirst().orElse(null);

        if (found != null) {
            System.out.println(found.eng+" 뜻 : "+ found.kor);
            System.out.println("출제회수 : " + found.count + "   오답회수 : " + found.wrongCount);
        } else {
            System.out.println("단어장에 등록되지 않은 단어입니다.");
        }
    }
    /**
     * 메뉴 버튼을 활성화하는 메서드.
     *
     * @param menuPanel 메뉴 패널
     * 메뉴 패널에 포함된 모든 버튼을 활성화하여 사용자가 클릭할 수 있게 만듬.
     */
    private void enableMenuButtons(JPanel menuPanel) {
        // 메뉴 패널에 포함된 모든 컴포넌트를 확인
        for (Component comp : menuPanel.getComponents()) {
            if (comp instanceof JButton) {
                comp.setEnabled(true);  // 버튼을 활성화
            }
        }
}
}








