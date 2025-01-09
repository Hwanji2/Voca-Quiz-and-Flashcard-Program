package homework;

import java.util.Objects;

/**
 * @author 202410647 이지환
 * @since 2024-11-18
 * Word 클래스
 * 단어에 대해 관리하는 클래스이다.
 *
 */

public class Word {
    String eng;
    String kor;
    int count;
    int wrongCount;

    /**
     * Word 클래스의 생성자.
     * 영어 단어와 한국어 뜻을 초기화합니다.
     *
     * @param eng 영어 단어
     * @param kor 한국어 뜻
     */
    public Word(String eng, String kor) {
        this.eng = eng;
        this.kor = kor;
    }

    /**
     * 두 Word 객체가 동일한지를 비교합니다.
     * 객체의 영어 단어(eng) 값이 같으면 동일한 것으로 간주합니다.
     *
     * @param o 비교할 객체
     * @return 동일하면 true, 그렇지 않으면 false
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Word word = (Word) o;
        return Objects.equals(eng, word.eng); // && Objects.equals(kor, word.kor);
    }

    /**
     * Word 객체를 문자열 형태로 반환합니다.
     * 형식: "영어 단어 : 한국어 뜻"
     *
     * @return 객체의 문자열 표현
     */
    @Override
    public String toString() {
        return eng + " : " + kor;
    }
}
