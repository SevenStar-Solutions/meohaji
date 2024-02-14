package com.example.meohaji.main

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import com.example.meohaji.NetworkStatus
import com.example.meohaji.R
import com.example.meohaji.databinding.ActivityMainBinding
import com.example.meohaji.home.BtnClick2
import com.example.meohaji.search.BtnClick3

class MainActivity : AppCompatActivity(), BtnClick2, BtnClick3 {

    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    private val adapter = ViewPagerAdapter(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        Handler(Looper.getMainLooper()).postDelayed(Runnable {
            ObjectAnimator.ofFloat(binding.constraintLayoutMainSplash, "alpha", 1f, 0f).apply {
                duration = 1000
                start()
            }
        }, 3000)

        with(binding) {

            setSupportActionBar(toolbarMain)
            supportActionBar?.setDisplayShowTitleEnabled(false)

            viewPagerMain.adapter = adapter
            viewPagerMain.isUserInputEnabled = false
            viewPagerMain.setCurrentItem(1, false)
            viewPagerMain.offscreenPageLimit = 1

            //시작 위치인 홈의 크기와 그 주변의 알파 설정
            setInitialSize(btnTestMiddle)
            btnTestLeft.alpha = 0.6f
            btnTestRight.alpha = 0.6f

            //현재 Frag 위치를 조건으로 행동을 구분
            btnTestLeft.setOnClickListener {
                val currentItem = viewPagerMain.currentItem
                if (currentItem != 0) {
                    viewPagerMain.currentItem = 0
                    //버튼 크기 확대 시 기준점이 필요해 설정
                    ivSpotLight.pivotX = (ivSpotLight.width / 2).toFloat()
                    ivSpotLight.pivotY = ivSpotLight.height.toFloat()
                    animateBtnSizeUp(it as CardView)

                    when (currentItem) {
                        1 -> spotlight1to0()
                        2 -> spotlight2to0()
                    }
                }
            }
            btnTestMiddle.setOnClickListener {
                val currentItem = viewPagerMain.currentItem
                if (currentItem != 1) {
                    viewPagerMain.currentItem = 1
                    animateBtnSizeUp(it as CardView)

                    when (currentItem) {
                        0 -> spotlight0to1()
                        2 -> spotlight2to1()
                    }
                }
            }
            btnTestRight.setOnClickListener {
                val currentItem = viewPagerMain.currentItem
                if (currentItem != 2) {
                    viewPagerMain.currentItem = 2
                    ivSpotLight.pivotX = (ivSpotLight.width / 2).toFloat()
                    ivSpotLight.pivotY = ivSpotLight.height.toFloat()
                    animateBtnSizeUp(it as CardView)

                    when (currentItem) {
                        0 -> spotlight0to2()
                        1 -> spotlight1to2()
                    }
                }
            }
        }
    }

    //Float 값을 쉽게 변경할 수 있도록 변수화
    private val lightOn: Float = 1f
    private val lightOff: Float = 0.6f

    private val spotLeft: Float = -45f
    private val spotMid: Float = 0f
    private val spotRight: Float = 45f

    private val btnL by lazy {
        binding.btnTestLeft
    }
    private val btnM by lazy {
        binding.btnTestMiddle
    }
    private val btnR by lazy {
        binding.btnTestRight
    }

    //네비게이션 버튼을 비추는 스포트라이트의 rotation + alpha 애니메이션
    private fun animateSpotlight(fromRotation: Float, toRotation: Float) {
        val spotlight = binding.ivSpotLight
        val rotationAnimator =
            ObjectAnimator.ofFloat(spotlight, "rotation", fromRotation, toRotation).apply {
                duration = 200
            }
        ObjectAnimator.ofFloat(spotlight, "alpha", 1f, 0.1f).apply {
            duration = 50
            start()
        }
        //애니메이션을 변수화 해서 이동이 완료되면 다시 밝히도록 리스너를 추가
        rotationAnimator.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                ObjectAnimator.ofFloat(spotlight, "alpha", 0.1f, 1f).apply {
                    duration = 200
                    start()
                }
            }
        })
        rotationAnimator.start()
    }

    //시작 Frag인 홈 위치 버튼 크기를 증가
    private fun setInitialSize(cardView: CardView) {
        val layoutParams = cardView.layoutParams
        val initialWidth = layoutParams.width + 20
        val initialHeight = layoutParams.height + 20
        layoutParams.width = initialWidth
        layoutParams.height = initialHeight
        cardView.layoutParams = layoutParams
    }

    //인자로 받은 카드 뷰의 크기를 확대
    private fun animateBtnSizeUp(cardView: CardView) {
        ObjectAnimator.ofFloat(cardView, "alpha", lightOff, lightOn).apply {
            duration = 400
            start()
        }
        val initialWidth = cardView.width
        val initialHeight = cardView.height
        val targetWidth = initialWidth + 20
        val targetHeight = initialHeight + 20
        val animator = ValueAnimator.ofFloat(0f, 1f)
        animator.addUpdateListener { valueAnimator ->
            val value = valueAnimator.animatedValue as Float
            val width = (initialWidth - (initialWidth - targetWidth) * value).toInt()
            val height = (initialHeight - (initialHeight - targetHeight) * value).toInt()
            val layoutParams = cardView.layoutParams
            layoutParams.width = width
            layoutParams.height = height
            cardView.layoutParams = layoutParams
        }
        animator.duration = 200
        animator.start()
    }

    //인자로 받은 카드 뷰의 키워진 크기를 원래 크기로 축소
    private fun animateBtnSizeDown(cardView: CardView) {
        ObjectAnimator.ofFloat(cardView, "alpha", lightOn, lightOff).apply {
            duration = 700
            start()
        }
        val initialWidth = cardView.width
        val initialHeight = cardView.height
        val targetWidth = initialWidth - 20
        val targetHeight = initialHeight - 20
        val animator = ValueAnimator.ofFloat(0f, 1f)
        animator.addUpdateListener { valueAnimator ->
            val value = valueAnimator.animatedValue as Float
            val width = (initialWidth - (initialWidth - targetWidth) * value).toInt()
            val height = (initialHeight - (initialHeight - targetHeight) * value).toInt()
            val layoutParams = cardView.layoutParams
            layoutParams.width = width
            layoutParams.height = height
            cardView.layoutParams = layoutParams
        }
        animator.duration = 200
        animator.start()
    }

    //선택된 버튼의 색상을 변경
    private fun colorChange(target1: CardView, target2: CardView) {
        target1.setCardBackgroundColor(ContextCompat.getColor(this, R.color.white))
        target2.setCardBackgroundColor(ContextCompat.getColor(this, R.color.yellow_background))
    }

    /**
     *  버튼간 상호 작용 하는 모습을 만들어 주는 최선의 방법
     *  좋은 방법이 있으면 지도 부탁드립니다
     */
    private fun spotlight0to1() {
        animateSpotlight(spotLeft, spotMid)
        animateBtnSizeDown(btnL)
        colorChange(btnL, btnM)
    }
    private fun spotlight0to2() {
        animateSpotlight(spotLeft, spotRight)
        animateBtnSizeDown(btnL)
        colorChange(btnL, btnR)
    }
    private fun spotlight1to0() {
        animateSpotlight(spotMid, spotLeft)
        animateBtnSizeDown(btnM)
        colorChange(btnM, btnL)
    }
    private fun spotlight1to2() {
        animateSpotlight(spotMid, spotRight)
        animateBtnSizeDown(btnM)
        colorChange(btnM, btnR)
    }
    private fun spotlight2to0() {
        animateSpotlight(spotRight, spotLeft)
        animateBtnSizeDown(btnR)
        colorChange(btnR, btnL)
    }
    private fun spotlight2to1() {
        animateSpotlight(spotRight, spotMid)
        animateBtnSizeDown(btnR)
        colorChange(btnR, btnM)
    }

    //데이터 변경을 인터페이스로 전달하기 위한 통로
    override fun clickFromHome() {
        adapter.notifyChangeData()
    }
    override fun clickFromSearch() {
        adapter.notifyChangeData()
    }
}