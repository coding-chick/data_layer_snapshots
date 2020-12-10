class AddGivenSetToFolderViewModel @Inject constructor(
    private val getFolderSets: GetFolderSets,
    private val getFoldersWithCreator: GetFoldersWithCreator,
    private val updateFolderSets: UpdateFolderSets,
    private val userInfoCache: UserInfoCache
) : BaseViewModel() {

    private val destroyToken: SingleSubject<Unit> = SingleSubject.create()
    private val _viewState = MutableLiveData<AddGivenSetToFolderState>()
    
       override fun onCleared() {
        super.onCleared()
        destroyToken.onSuccess(Unit)
      }
      
      private fun initializeState() {
        Timber.i("Fetching FolderSets and Folders with Creators...")
       
        getFolderSets.getBySetIds(selectedStudySetIds, destroyToken),
            .subscribe(
            { (folderSets) ->
                // some irrelevant lines of code work that I omitted...
                _viewState.value = calculateState(folderSets)
            },
            { error ->
                Timber.w(error, "Encountered error getting FolderSets")
                // TODO: error handling
                _viewState.value = Error(DEFAULT_ERROR)
            }).disposeOnClear()
    }
