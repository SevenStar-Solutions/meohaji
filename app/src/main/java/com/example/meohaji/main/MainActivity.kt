package com.example.meohaji.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.meohaji.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }
    private val btnOn : Float = 1f
    private val btnOff : Float = 0.5f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbarMain)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        binding.viewPagerMain.adapter = ViewPagerAdapter(this)
        binding.viewPagerMain.isUserInputEnabled = false
        binding.viewPagerMain.setCurrentItem(1, false)

        with(binding) {
            btnTestLeft.setOnClickListener {
                val currentItem = viewPagerMain.currentItem
                if (currentItem != 0) {
                    viewPagerMain.currentItem = 0
                    ivSoptlight.pivotX = (ivSoptlight.width / 2).toFloat()
                    ivSoptlight.pivotY = ivSoptlight.height.toFloat()
                    // 이미지 뷰를 -45도 회전하여 상단이 좌측을 향하도록 함
                    ivSoptlight.rotation = -50f
                }
            }
            btnTestMiddle.setOnClickListener {
                val currentItem = viewPagerMain.currentItem
                if (currentItem != 1) {
                    viewPagerMain.currentItem = 1
                    // 이미지 뷰의 회전을 초기화하여 상단이 직선을 향하도록 함
                    ivSoptlight.rotation = 0f
                }
            }
            btnTestRight.setOnClickListener {
                val currentItem = viewPagerMain.currentItem
                if (currentItem != 2) {
                    viewPagerMain.currentItem = 2
                    // 이미지 뷰의 중심을 하단 중앙으로 이동
                    ivSoptlight.pivotX = (ivSoptlight.width / 2).toFloat()
                    ivSoptlight.pivotY = ivSoptlight.height.toFloat()
                    // 이미지 뷰를 45도 회전하여 상단이 우측을 향하도록 함
                    ivSoptlight.rotation = 50f
                }
            }
            //현재 아이템 인덱스 구하기
            //val currentItem = viewPagerMain.currentItem



        } //end of with binding!!
    } //end of onCreate!!


//    private fun setFragment(frag: Fragment) {
//        supportFragmentManager.beginTransaction()
//            .replace(binding.frameMain.id, frag)
//            .addToBackStack(null)
//            .commit()
//    }
//
//    private fun checkFragmentLocation() {
//        val currentFragment = supportFragmentManager.findFragmentById(R.id.frame_main)
//        when (currentFragment?.id) {
//            R.id.rv_seachfragment_recyclerview -> "Search"
//            R.id.fl_homeFrag -> "Home"
//            R.id.fl_myPageFrag -> "MyPage"
//            else -> null
//        }
//    }
//    private fun moveToSearchFrag() {
//        if (checkFragmentLocation().toString() != "Search") {
//            setFragment(SearchFragment())
////            val spotlightON = ObjectAnimator.ofFloat(binding.btnTestLeft, "alpha", 1f, 0.5f).apply {
////                duration = 200
////                start()
////            }
//        }
//    }

//    private fun moveToHomeFrag() {
//        if (checkFragmentLocation().toString() != "Home") {
//            setFragment(HomeFragment())
//        }
//    }
//
//    private fun moveToMyPageFrag() {
//        if (checkFragmentLocation().toString() != "MyPage") {
//            setFragment(MyPageFragment())
//        }
//    }
}