package com.example.meohaji

import android.animation.ObjectAnimator
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils.replace
import androidx.fragment.app.Fragment
import com.example.meohaji.databinding.ActivityMainBinding
import com.example.meohaji.fragment.HomeFragment
import com.example.meohaji.fragment.MyPageFragment
import com.example.meohaji.fragment.SearchFragment

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setFragment(HomeFragment())

        with(binding){
            btnTestLeft.setOnClickListener {
                checkFragmentLocation()
                moveToSearchFrag()
            }
            btnTestMiddle.setOnClickListener {
                checkFragmentLocation()
                moveToHomeFrag()
            }
            btnTestRight.setOnClickListener {
                checkFragmentLocation()
                moveToMyPageFrag()
            }

        } //end of with binding!!
    } //end of onCreate!!



    private fun setFragment(frag: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(binding.flMainContainer.id, frag)
            .addToBackStack(null)
            .commit()
    }
    private fun checkFragmentLocation(){
        val currentFragment = supportFragmentManager.findFragmentById(R.id.fl_mainContainer)
        when(currentFragment?.id){
            R.id.fl_searchFrag -> "Search"
            R.id.fl_homeFrag -> "Home"
            R.id.fl_myPageFrag -> "MyPage"
            else -> null
        }
    }
    private fun moveToSearchFrag(){
        if (checkFragmentLocation().toString() != "Search"){
            setFragment(SearchFragment())
//            val spotlightON = ObjectAnimator.ofFloat(binding.btnTestLeft, "alpha", 1f, 0.5f).apply {
//                duration = 200
//                start()
//            }
        }
    }
    private fun moveToHomeFrag(){
        if (checkFragmentLocation().toString() != "Home"){
            setFragment(HomeFragment())
        }
    }

    private fun moveToMyPageFrag(){
        if (checkFragmentLocation().toString() != "MyPage"){
            setFragment(MyPageFragment())
        }
    }

}