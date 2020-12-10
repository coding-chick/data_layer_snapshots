   /**
     * This will default to fetching from network.
     *
     * If network is offline/network request failed, get from local.
     */
    override fun getModelsByStudySets(studySetIds: List<Long>): Observable<List<FolderSet>> {
        return fetchAndImport(
            remote = factory.remoteDataStore.getFolderSetsByStudySets(studySetIds = studySetIds),
            local = factory.localDataStore.getModelsByStudySets(studySetIds = studySetIds)
        )
    }

    private fun fetchAndImport(
        remote: Single<List<FolderSet>>,
        local: Single<List<FolderSet>>
    ): Observable<List<FolderSet>> {
        val observableFromLocal = local.toObservable()
        val observableFromNetwork = Observable.defer {
            networkStatus.doIfConnected(
                connected = remote
                    .flatMap {
                        // If successful, we save the network response to the DB and return the result of the request.
                        factory.localDataStore.importModels(it)
                    }
            ).toObservable()
        }.onErrorResumeNext { e: Throwable ->
            logger.warn("no network connection", e)
            Observable.empty()
        }

        return Observable.concat(observableFromLocal, observableFromNetwork)
            .map { folderSets -> folderSets.filter { it.isDeleted != true } }
    }
