package com.example.musicplayer.utils

import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.MutableStateFlow

class StateFlowWrapper<T>(t: T) {
    private val stateFlow by lazy {
        MutableStateFlow(t)
    }

    fun value() = stateFlow.value

    suspend fun emit(t: T) {
        stateFlow.emit(t)
    }

    suspend fun collect(collector: FlowCollector<T>) {
        stateFlow.collect(collector)
    }
}

class LifeCycleAwareBinding<BindingT : ViewDataBinding>(fragmentManager: FragmentManager) :
    FragmentManager.FragmentLifecycleCallbacks() {

    init {
        fragmentManager.registerFragmentLifecycleCallbacks(this, false)
    }

    private var _binding: BindingT? = null

    override fun onFragmentViewCreated(
        fm: FragmentManager,
        f: Fragment,
        v: View,
        savedInstanceState: Bundle?
    ) {
        super.onFragmentViewCreated(fm, f, v, savedInstanceState)
        _binding = DataBindingUtil.bind(v)
    }

    override fun onFragmentViewDestroyed(fm: FragmentManager, f: Fragment) {
        super.onFragmentViewDestroyed(fm, f)
        _binding = null
    }

    operator fun invoke() = _binding!!
}

class Node {
    var next: Node? = null

    fun copy() = Node().apply {
        this.next = this@Node.next
    }
}

fun main() {
    val list = Node()
    function(list, list.copy(), list.next?.copy())
}

fun function(list: Node?, odd: Node?, even: Node?) {
    odd?.next = list?.next
    even?.next = list?.next?.next
    function(list?.next?.next, odd?.next, even?.next)
}
