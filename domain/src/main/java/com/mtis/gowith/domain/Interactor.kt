package com.mtis.gowith.domain

import kotlinx.coroutines.flow.Flow

abstract class Interactor<P, T> {
    operator fun invoke(params: P) = doWork(params)

    protected abstract fun doWork(params: P): T
}

abstract class FlowInteractor<P: Any, T> {
    operator fun invoke(parameters:P): Flow<T> = createObservable(parameters)

    protected abstract fun createObservable(params: P): Flow<T>
}