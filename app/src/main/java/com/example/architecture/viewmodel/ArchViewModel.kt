package com.example.architecture.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.kotlin.subscribeBy
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class ArchViewModel : ViewModel() {
    private val counter: MutableLiveData<Int> by lazy {
        MutableLiveData<Int>(0)
    }

    private var isIncreasing = false
    private var counterJob: Job = GlobalScope.launch { }
    private var counterDisposable: Disposable = Disposable.empty()
    private val tag = "LiveData"

    fun increase() {
        if (isIncreasing) {
            return
        }

        isIncreasing = true
        counterJob = GlobalScope.launch(Dispatchers.IO) {
            counterDisposable = Observable.interval(1L, TimeUnit.SECONDS)
                .timeInterval()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeBy(
                    onNext = {
                        counter.value = counter.value?.plus(1)
                        Log.d(tag, counter.value.toString())
                    },
                    onError = {
                        it.printStackTrace()
                    },
                    onComplete = { }
                )
        }
    }

    public override fun onCleared() {
        super.onCleared()

        if (counterJob.isActive) {
            Log.d(tag, "counter job canceled.")
            counterJob.cancel()
        }

        if (!counterDisposable.isDisposed) {
            Log.d(tag, "counter disposable disposed.")
            counterDisposable.dispose()
        }

        counter.value = 0
        isIncreasing = false
    }

    // 暴露给View层的数据结构是LiveData
    // 如果想修改LiveData的值，需要额外提供一个修改的方法
    // 接口隔离，Mutable不会给view暴露出来
    fun getCounter(): LiveData<Int> {
        return counter
    }
}