package com.bignerdranch.android.geoquiz

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.bignerdranch.android.geoquiz.databinding.ActivityMainBinding
import com.bignerdranch.android.geoquiz.ui.theme.GeoQuizTheme

class MainActivity : ComponentActivity() {

    private lateinit var binding: ActivityMainBinding

    private val quizViewModel: QuizViewModel by viewModels()

    private val cheatLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            result ->
            // Handle the result
            if (result.resultCode == Activity.RESULT_OK) {
                quizViewModel.isCheater =
                    result.data?.getBooleanExtra(EXTRA_ANSWER_SHOWN, false) ?: false
            }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Log.d(TAG, "Got a QuizViewModel: $quizViewModel")


        binding.trueButton.setOnClickListener {view: View ->
            // Do something in response to the click here
            checkAnswer(true)
        }

        binding.falseButton.setOnClickListener {view: View ->
            // Do something in response to the click here
            checkAnswer(false)
        }

        binding.nextButton.setOnClickListener {
            quizViewModel.moveToNext()
            updateQuestion()
        }

        binding.cheatButton.setOnClickListener{
            //Start CheatActivity
            val answerIsTrue = quizViewModel.currentQuestionAnswer
            val intent = CheatActivity.newIntent(this@MainActivity, answerIsTrue)
            cheatLauncher.launch(intent)
        }

        updateQuestion()

    }

    private fun updateQuestion(){
        val questionTextResId = quizViewModel.currentQuestionText
        binding.questionTextView.setText(questionTextResId)
    }

    private fun checkAnswer(userAnswer: Boolean){
        val correctAnswer = quizViewModel.currentQuestionAnswer

        val messageResId = when {
            quizViewModel.isCheater -> R.string.judgment_toast
            userAnswer == correctAnswer -> R.string.correct_toast
            else -> R.string.incorrect_toast
        }

        Toast.makeText(
            this,
            messageResId,
            Toast.LENGTH_SHORT).show()
    }

}