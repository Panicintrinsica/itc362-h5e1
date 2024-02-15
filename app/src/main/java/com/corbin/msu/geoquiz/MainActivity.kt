package com.corbin.msu.geoquiz

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.corbin.msu.geoquiz.databinding.ActivityMainBinding
import kotlin.math.round

private const val TAG = "MainActivity"

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val questionBank = listOf(
        Question(R.string.question_australia, true),
        Question(R.string.question_oceans, true),
        Question(R.string.question_mideast, false),
        Question(R.string.question_africa, false),
        Question(R.string.question_americas, true),
        Question(R.string.question_asia, true)
    )

    private var answers = Array<Boolean?>(questionBank.size) { null }

    private var currentIndex = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.d(TAG, "onCreate(Bundle?) called")

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.trueButton.setOnClickListener { it: View ->
            answerQuestion(true)
        }

        binding.falseButton.setOnClickListener { it: View ->
            answerQuestion(false)
        }

        binding.questionTextView.setOnClickListener {
            nextQuestion()
        }

        binding.lastButton.setOnClickListener {
            previousQuestion()
        }

    }

    override fun onStart() {
        super.onStart()
        initQuiz()
        Log.d(TAG, "onStart() called")
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume() called")
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "onPause() called")
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "onStop() called")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy() called")
    }

    /**
     * Initialize and or reset the quiz to the default state
     */
    private fun initQuiz() {
        answers = Array<Boolean?>(questionBank.size) { null }
        this.currentIndex = 0

        displayQuestion()
        checkQuestionStatus()

        // Reset Next Button
        binding.nextButton.setText(R.string.next_button)
        binding.nextButton.setOnClickListener {
            nextQuestion()
        }
    }

    /**
     * Answer the current question
     * @param choice: The user's answer
     */
    private fun answerQuestion(choice: Boolean) {
        val correctAnswer = questionBank[this.currentIndex].answer

        if (choice == correctAnswer) {
            answers[this.currentIndex] = true
            showAnswerFeedback(true)
        } else {
            answers[this.currentIndex] = false
            showAnswerFeedback(false)
        }

        checkQuestionStatus()
    }

    /**
     * Show feedback to the user
     * @param isCorrect: if the user's answer was correct.
     */
    private fun showAnswerFeedback(isCorrect: Boolean) {
        val messageResId = if (isCorrect) {
            R.string.correct_toast
        } else {
            R.string.incorrect_toast
        }

        Toast.makeText(this, messageResId, Toast.LENGTH_SHORT).show()
    }

    /**
     * Display the next question
     */
    private fun nextQuestion() {
        this.currentIndex = (this.currentIndex + 1) % questionBank.size
        displayQuestion()
        checkQuestionStatus()
    }

    /**
     * Display the previous question
     */
    private fun previousQuestion() {
        this.currentIndex = if (this.currentIndex == 0) {
            questionBank.size - 1
        } else {
            this.currentIndex - 1
        }
        displayQuestion()
        checkQuestionStatus()
    }

    /**
     * Check the status of the current question and update the UI accordingly
     */
    private fun checkQuestionStatus() {
        // Is Answered
        if (answers.getOrNull(this.currentIndex) != null) {
            binding.trueButton.isEnabled = false
            binding.falseButton.isEnabled = false
        } else {
            binding.trueButton.isEnabled = true
            binding.falseButton.isEnabled = true
        }

        // Is First Question
        binding.lastButton.isEnabled = this.currentIndex != 0

        // Is Last Question
        binding.nextButton.isEnabled = this.currentIndex != questionBank.size - 1

        // All Questions Answered
        if (answers.all { it != null }) {
            binding.nextButton.setText(R.string.finish_button)
            binding.nextButton.isEnabled = true
            binding.nextButton.setOnClickListener {
                endQuiz()
            }

        } else {
            binding.nextButton.setText(R.string.next_button)
        }

    }

    /**
     * Display the current question
     */
    private fun displayQuestion() {
        val questionTextResId = questionBank[this.currentIndex].textResId
        binding.questionTextView.setText(questionTextResId)
    }

    /**
     * Calculate the user's score and display it to the user
     * @return the user's score as a percentage
     */
    private fun calculateScore(): Double {
        val score = answers.count { it == true }
        return (score.toDouble() / questionBank.size.toDouble()) * 100
    }

    /**
     * End the quiz and display the user's score
     */
    private fun endQuiz() {
        val score = calculateScore()
        val message = "You scored " + round(score * 10.0) / 10.0 + "%"

        Toast.makeText(this, message, Toast.LENGTH_LONG).show()

        initQuiz()
    }
}