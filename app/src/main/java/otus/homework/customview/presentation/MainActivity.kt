package otus.homework.customview.presentation

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.tabs.TabLayout
import otus.homework.customview.R
import otus.homework.customview.databinding.ActivityMainBinding
import otus.homework.customview.presentation.task1.Task1Fragment
import otus.homework.customview.presentation.task2.Task2Fragment

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bindViews()
    }

    private fun bindViews() {
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.taskTabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {

            override fun onTabSelected(tab: TabLayout.Tab?) {
                val fragmentTransaction = supportFragmentManager.beginTransaction()
                when {
                    tab?.position == 0 -> fragmentTransaction.replace(R.id.task_content, Task1Fragment())
                    else -> fragmentTransaction.replace(R.id.task_content, Task2Fragment())
                }
                fragmentTransaction.commitNow()
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
                // Handle tab reselect
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                // Handle tab unselect
            }
        })
    }

}
