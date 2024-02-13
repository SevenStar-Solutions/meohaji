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
import com.example.meohaji.R
import com.example.meohaji.databinding.ActivityMainBinding
import com.example.meohaji.home.BtnClick2
import com.example.meohaji.search.BtnClick3

class MainActivity : AppCompatActivity(), BtnClick2, BtnClick3 {

    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }
    private val btnOn: Float = 1f
    private val btnOff: Float = 0.5f

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

        setSupportActionBar(binding.toolbarMain)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        binding.viewPagerMain.adapter = adapter
        binding.viewPagerMain.isUserInputEnabled = false
        binding.viewPagerMain.setCurrentItem(1, false)
        binding.viewPagerMain.offscreenPageLimit = 1

        setInitialSize(binding.btnTestMiddle)

        with(binding) {

            btnTestLeft.alpha = 0.6f
            btnTestRight.alpha = 0.6f

            btnTestLeft.setOnClickListener {
                val currentItem = viewPagerMain.currentItem
                if (currentItem != 0) {
                    viewPagerMain.currentItem = 0
                    ivSpotLight.pivotX = (ivSpotLight.width / 2).toFloat()
                    ivSpotLight.pivotY = ivSpotLight.height.toFloat()
                    animateBtnSizeUp(it as CardView) //적용이 되지 않아 gpt에게 확인해 달라고 하고 추가된 코드인데 학습요망

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
                    animateBtnSizeUp(it as CardView) //적용이 되지 않아 gpt에게 확인해 달라고 하고 추가된 코드인데 학습요망

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
                    animateBtnSizeUp(it as CardView) //적용이 되지 않아 gpt에게 확인해 달라고 하고 추가된 코드인데 학습요망

                    when (currentItem) {
                        0 -> spotlight0to2()
                        1 -> spotlight1to2()
                    }

                }

            }


        } //end of with binding!!
    } //end of onCreate!!

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
    private fun testrun(testlist : List) {
        println("안녕하세요")
    }

    private fun setInitialSize(cardView: CardView) {
        val layoutParams = cardView.layoutParams
        val initialWidth = layoutParams.width + 20
        val initialHeight = layoutParams.height + 20
        layoutParams.width = initialWidth
        layoutParams.height = initialHeight
        cardView.layoutParams = layoutParams
    }

    private fun animateBtnSizeUp(cardView: CardView) {
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

    private fun animateBtnSizeDown(cardView: CardView) {
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

    private val spotLeft: Float = -45f
    private val spotMid: Float = 0f
    private val spotRight: Float = 45f

    private fun testAlphaOn(target : CardView){
        ObjectAnimator.ofFloat(target,"alpha",0.6f,1f).apply {
            duration = 400
            start()
        }
    }
    private fun testAlphaOff(target : CardView){
        ObjectAnimator.ofFloat(target,"alpha",1f,0.6f).apply {
            duration = 700
            start()
        }
    }

    private fun spotlight0to1() {
        animateSpotlight(spotLeft, spotMid)
        animateBtnSizeDown(binding.btnTestLeft)
        binding.btnTestLeft.setCardBackgroundColor(ContextCompat.getColor(this, R.color.white))
//        binding.btnTestLeft.setCardBackgroundColor(Color.parseColor("#D1D1D1"))
        binding.btnTestMiddle.setCardBackgroundColor(ContextCompat.getColor(this, R.color.yellow_background))
        testAlphaOff(binding.btnTestLeft)
        testAlphaOn(binding.btnTestMiddle)



    }

    private fun spotlight0to2() {
        animateSpotlight(spotLeft, spotRight)
        animateBtnSizeDown(binding.btnTestLeft)
        binding.btnTestLeft.setCardBackgroundColor(ContextCompat.getColor(this, R.color.white))
        binding.btnTestRight.setCardBackgroundColor(ContextCompat.getColor(this, R.color.yellow_background))
        testAlphaOff(binding.btnTestLeft)
        testAlphaOn(binding.btnTestRight)
    }

    private fun spotlight1to0() {
        animateSpotlight(spotMid, spotLeft)
        animateBtnSizeDown(binding.btnTestMiddle)
        binding.btnTestMiddle.setCardBackgroundColor(ContextCompat.getColor(this, R.color.white))
        binding.btnTestLeft.setCardBackgroundColor(ContextCompat.getColor(this, R.color.yellow_background))
        testAlphaOff(binding.btnTestMiddle)
        testAlphaOn(binding.btnTestLeft)
    }

    private fun spotlight1to2() {
        animateSpotlight(spotMid, spotRight)
        animateBtnSizeDown(binding.btnTestMiddle)
        binding.btnTestMiddle.setCardBackgroundColor(ContextCompat.getColor(this, R.color.white))
        binding.btnTestRight.setCardBackgroundColor(ContextCompat.getColor(this, R.color.yellow_background))
        testAlphaOff(binding.btnTestMiddle)
        testAlphaOn(binding.btnTestRight)
    }

    private fun spotlight2to0() {
        animateSpotlight(spotRight, spotLeft)
        animateBtnSizeDown(binding.btnTestRight)
        binding.btnTestRight.setCardBackgroundColor(ContextCompat.getColor(this, R.color.white))
        binding.btnTestLeft.setCardBackgroundColor(ContextCompat.getColor(this, R.color.yellow_background))
        testAlphaOff(binding.btnTestRight)
        testAlphaOn(binding.btnTestLeft)
    }

    private fun spotlight2to1() {
        animateSpotlight(spotRight, spotMid)
        animateBtnSizeDown(binding.btnTestRight)
        binding.btnTestRight.setCardBackgroundColor(ContextCompat.getColor(this, R.color.white))
        binding.btnTestMiddle.setCardBackgroundColor(ContextCompat.getColor(this, R.color.yellow_background))
        testAlphaOff(binding.btnTestRight)
        testAlphaOn(binding.btnTestMiddle)
    }

    override fun clickFromHome() {
        adapter.notifyChangeData()
    }

    override fun clickFromSearch() {
        adapter.notifyChangeData()
    }
}