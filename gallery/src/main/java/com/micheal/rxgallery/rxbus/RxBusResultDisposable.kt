package com.micheal.rxgallery.rxbus

import com.micheal.rxgallery.rxbus.event.BaseResultEvent

abstract class RxBusResultDisposable<T :BaseResultEvent> :RxBusDisposable<T>()