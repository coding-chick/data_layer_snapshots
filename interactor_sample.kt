
class GetFolderSets @Inject constructor(
    private val repository: IFolderSetRepository,
    private val dispatcher: Dispatcher
) {
    fun getBySetIds(setIds: List<Long>, stopToken: Single<Unit>): Observable<List<FolderSet>> {
        return dispatcher.asObservable(stopToken = stopToken) {
            repository.getModelsByStudySets(setIds)
        }
    }
}
