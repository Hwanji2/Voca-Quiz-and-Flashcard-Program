package homework;


/**
 * @author 202410647 이지환
 * @since 2024-11-18
 *
 * TestMain 클래스
 * 실행을 담당하는 클래스입니다.
 * 요구사항 1~2 만족여부 Yes
 * 요구사항 3 만족여부 Yes
 */

public class LjhTestMain {
    public static void main(String[] args)
    {
        // VocManager 객체 생성, "이지환" 이름을 전달
        VocManager manager = new VocManager("이지환");
        // 파일 경로를 지정하기 위한
        String filePath;
        try {
            // 명령줄 인수로 경로가 제공되었는지 확인
            if (args.length > 0 && args[0] != null && !args[0].isEmpty()) {
                // 인수가 제공되면 그 경로를 사용
                filePath = args[0];
            } else {
                // 인수가 제공되지 않으면 기본 경로를 사용
                System.out.println("인수가 제공되지 않았습니다. 기본 경로 'voc/quiz.txt'를 사용합니다.");
                filePath = "voc/quiz.txt";
            }
            // 지정된 경로로 run 메서드를 실행
            manager.run(filePath);
        } catch (Exception ex) {
            // 예기치 않은 예외가 발생한 경우
            System.out.println("오류가 발생했습니다: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
}

