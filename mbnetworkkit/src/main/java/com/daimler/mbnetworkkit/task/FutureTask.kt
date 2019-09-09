package com.daimler.mbnetworkkit.task

interface FutureTask<C, F> {
    fun onComplete(callback: (C) -> Unit): FutureTask<C, F>
    fun onFailure(callback: (F) -> Unit): FutureTask<C, F>
    fun onAlways(callback: (TaskState, C?, F?) -> Unit): FutureTask<C, F>
    fun isPending(): Boolean
    fun isCompleted(): Boolean
    fun isFailed(): Boolean
}