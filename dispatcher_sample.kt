class Dispatcher(
    val uiScheduler: Scheduler,
    val backgroundScheduler: Scheduler
) {
    fun <T> asObservable(stopToken: Single<Unit>, build: () -> Observable<T>): Observable<T> {
        return build()
            .subscribeOn(backgroundScheduler)
            .observeOn(uiScheduler)
            .takeUntil(stopToken.toObservable())
    }
