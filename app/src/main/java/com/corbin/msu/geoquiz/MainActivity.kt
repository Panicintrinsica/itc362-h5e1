package com.corbin.msu.geoquiz

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import com.corbin.msu.geoquiz.databinding.ActivityMainBinding
import kotlin.math.log
import kotlin.math.round

private const val TAG = "MainActivity"

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val quizViewModel: QuizViewModel by viewModels()


    private val cheatLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            quizViewModel.isCheater =
                result.data?.getBooleanExtra(IS_CHEATER_KEY, false) ?: false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.d(TAG, "Got a QuizViewModel: $quizViewModel")

        Log.d(TAG, "onCreate(Bundle?) called")

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.trueButton.setOnClickListener {
            answerQuestion(true)
        }

        binding.falseButton.setOnClickListener {
            answerQuestion(false)
        }

        binding.questionTextView.setOnClickListener {
            changeQuestion(NavDirection.NEXT)
        }

        binding.nextButton.setOnClickListener {
            changeQuestion(NavDirection.NEXT)
        }

        binding.lastButton.setOnClickListener {
            changeQuestion(NavDirection.PREVIOUS)
        }

        binding.finishButton.setOnClickListener {
            endQuiz()
        }

        binding.cheatButton.setOnClickListener {
            // Start the CheatActivity
            val answerIsTrue = quizViewModel.currentQuestionAnswer
            val intent = CheatActivity.newIntent(this@MainActivity, answerIsTrue)

            cheatLauncher.launch(intent)
        }
    }

    override fun onStart() {
        super.onStart()
        displayQuestion()
        updateButtonStates()
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
     * Answer the current question
     * @param choice: The user's answer
     */
    private fun answerQuestion(choice: Boolean) {
        showAnswerFeedback(
            quizViewModel.answerQuestion(choice)
        )
        updateButtonStates()
    }

    /**
     * Show feedback to the user
     * @param isCorrect: if the user's answer was correct.
     */
    private fun showAnswerFeedback(isCorrect: Boolean) {
        Log.d(TAG, "Cheated: " + quizViewModel.isCheater.toString())

        val messageResId = when {
            quizViewModel.isCheater -> R.string.judgment_toast
            isCorrect -> R.string.correct_toast
            else -> R.string.incorrect_toast
        }

        Toast.makeText(this, messageResId, Toast.LENGTH_SHORT).show()
    }

    /**
     * Changes the current question based on the provided navigation direction.
     *
     * @param direction The direction to navigate in the quiz. This should be either `NavDirection.NEXT`
     * to move to the next question, or `NavDirection.PREVIOUS` to move to the previous question.
     */
    private fun changeQuestion(direction: NavDirection) {
        when (direction) {
            NavDirection.NEXT -> quizViewModel.moveToNext()
            NavDirection.PREVIOUS -> quizViewModel.moveToPrevious()
        }

        displayQuestion()
        updateButtonStates()
    }

    /**
     * Updates the state of the buttons based on the current question and answers
     */
    private fun updateButtonStates() {
        // Enable/Disable Answer Buttons based on if the question is answered
        val isAnswered = quizViewModel.isAnswered()
        binding.trueButton.isEnabled = !isAnswered
        binding.falseButton.isEnabled = !isAnswered

        // Enable/Disable navigation buttons based on the current question index
        binding.lastButton.isEnabled = quizViewModel.index != 0
        binding.nextButton.isEnabled = quizViewModel.index != quizViewModel.length - 1

        // Show/Hide the finish button based on if it's the final question
        binding.finishButton.visibility = if (quizViewModel.isFinalQuestion()) View.VISIBLE else View.INVISIBLE
    }

    /**
     * Display the current question
     */
    private fun displayQuestion() {
        binding.questionTextView.setText(quizViewModel.currentQuestionText)
    }

    /**
     * End the quiz and display the user's score, then reset the quiz.
     */
    private fun endQuiz() {
        val message = "You scored " + round(quizViewModel.getScore() * 10.0) / 10.0 + "%"

        Toast.makeText(this, message, Toast.LENGTH_LONG).show()

        quizViewModel.resetQuiz()
        displayQuestion()
        updateButtonStates()
    }
}

private enum class NavDirection {
    NEXT,
    PREVIOUS
}