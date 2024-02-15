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
            answer(true)
        }

        binding.falseButton.setOnClickListener { it: View ->
            answer(false)
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

    private fun answer(choice: Boolean) {
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

    private fun showAnswerFeedback(boolean: Boolean) {
        val messageResId = if (boolean) {
            R.string.correct_toast
        } else {
            R.string.incorrect_toast
        }

        Toast.makeText(this, messageResId, Toast.LENGTH_SHORT).show()
    }


    private fun calcualteScore() {}

    private fun nextQuestion() {
        this.currentIndex = (this.currentIndex + 1) % questionBank.size
        displayQuestion()
        checkQuestionStatus()
    }

    private fun previousQuestion() {
        this.currentIndex = if (this.currentIndex == 0) {
            questionBank.size - 1
        } else {
            this.currentIndex - 1
        }
        displayQuestion()
        checkQuestionStatus()
    }

    private fun checkQuestionStatus() {

        Log.d(TAG, "Current Index: ${this.currentIndex}")
        Log.d(TAG, "Answers: ${answers.size}")

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

    private fun displayQuestion() {
        val questionTextResId = questionBank[this.currentIndex].textResId
        binding.questionTextView.setText(questionTextResId)
    }

    private fun endQuiz() {
        val score = answers.count { it == true }

        val percentage = (score.toDouble() / questionBank.size.toDouble()) * 100
        val message = "You scored " + round(percentage * 10.0) / 10.0 + "%"

        Toast.makeText(this, message, Toast.LENGTH_LONG).show()

        initQuiz()
    }

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

}