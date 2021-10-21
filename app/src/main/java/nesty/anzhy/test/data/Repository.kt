package nesty.anzhy.test.data

import dagger.hilt.android.scopes.ActivityRetainedScoped
import nesty.anzhy.test.data.network.RemoteDataSource
import javax.inject.Inject

@ActivityRetainedScoped
class Repository @Inject constructor(
    remoteDataSource: RemoteDataSource,
) {
    val remote = remoteDataSource
}