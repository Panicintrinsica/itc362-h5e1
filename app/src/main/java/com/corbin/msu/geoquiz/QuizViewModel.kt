package com.corbin.msu.geoquiz

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel

private const val TAG = "QuizViewModel"
private const val KEY_INDEX = "index"
private const val KEY_ANSWERS = "answers"

const val IS_CHEATER_KEY = "IS_CHEATER_KEY"
class QuizViewModel(private val savedStateHandle: SavedStateHandle) : ViewModel() {

    private val questionBank = listOf(
        Question(R.string.question_australia, true),
        Question(R.string.question_oceans, true),
        Question(R.string.question_mideast, false),
        Question(R.string.question_africa, false),
        Question(R.string.question_americas, true),
        Question(R.string.question_asia, true)
    )

    var isCheater: Boolean
        get() = savedStateHandle.get(IS_CHEATER_KEY) ?: false
        set(value) {
            savedStateHandle.set(IS_CHEATER_KEY, value)
        }

    private var answers: Array<Boolean?>
        get() = savedStateHandle.get<Array<Boolean?>>(KEY_ANSWERS) ?: Array<Boolean?>(questionBank.size) { null }
        set(value) {
            savedStateHandle.set(KEY_ANSWERS, value)
        }

    private var currentIndex: Int
        get() = savedStateHandle.get(KEY_INDEX) ?: 0
        set(value) {
            savedStateHandle.set(KEY_INDEX, value)
        }

    val length: Int
        get() = questionBank.size

    val index: Int
        get() = currentIndex

    val currentQuestionAnswer: Boolean
        get() = questionBank[currentIndex].answer

    val currentQuestionText: Int
        get() = questionBank[currentIndex].textResId

    fun moveToNext() {
        currentIndex = (currentIndex + 1) % questionBank.size
    }

    fun moveToPrevious() {
        currentIndex = if (currentIndex == 0) {
            length - 1
        } else {
            currentIndex - 1
        }
    }

    fun resetQuiz() {
        currentIndex = 0
        answers = Array(questionBank.size) { null }
    }

    fun answerQuestion(choice: Boolean): Boolean {
        val correctAnswer = currentQuestionAnswer

        answers[currentIndex] = choice == correctAnswer

        // Persist the changes to the answers array
        savedStateHandle[KEY_ANSWERS] = answers

        return choice == correctAnswer
    }

    fun isAnswered(): Boolean {
        return answers.getOrNull(currentIndex) != null
    }

    fun isFinalQuestion(): Boolean {
        return answers.all { it != null }
    }

    fun getScore(): Double {
        val score = answers.count { it == true }
        return (score.toDouble() / questionBank.size.toDouble()) * 100
    }


}