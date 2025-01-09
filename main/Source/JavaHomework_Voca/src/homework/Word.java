package homework;

import java.util.Objects;

/**
 * Represents a word with its English term, Korean meaning,
 * and quiz statistics such as correct and incorrect counts.
 *
 * @author 202410647 이지환
 * @since 2024-11-18
 */
public class Word {

    /**
     * The English word.
     */
    String eng;

    /**
     * The Korean meaning of the word.
     */
    String kor;

    /**
     * The number of times this word has been presented in quizzes.
     */
    int count;

    /**
     * The number of times this word was answered incorrectly.
     */
    int wrongCount;

    /**
     * Constructs a Word object with an English term and its Korean meaning.
     *
     * @param eng the English word
     * @param kor the Korean meaning of the word
     */
    public Word(String eng, String kor) {
        this.eng = eng;
        this.kor = kor;
    }

    /**
     * Compares this Word object with another object for equality.
     * Two Word objects are considered equal if their English words are identical.
     *
     * @param o the object to compare with
     * @return true if the objects are equal, false otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Word word = (Word) o;
        return Objects.equals(eng, word.eng);
    }

    /**
     * Returns a string representation of this Word object.
     * The format is "English word : Korean meaning".
     *
     * @return a string representation of this Word object
     */
    @Override
    public String toString() {
        return eng + " : " + kor;
    }
}
