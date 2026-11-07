package com.abika.expense.core.datastore

import kotlinx.coroutines.test.runTest
import okio.FileSystem
import okio.Path.Companion.toPath
import kotlin.random.Random
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class DataStoreTokenStoreTest {

    private val path = FileSystem.SYSTEM_TEMPORARY_DIRECTORY / "token-test-${Random.nextLong()}.preferences_pb"

    @AfterTest
    fun cleanUp() {
        FileSystem.SYSTEM.delete(path, mustExist = false)
    }

    @Test
    fun savesReadsAndClearsTheToken() = runTest {
        val store = DataStoreTokenStore(createDataStore { path.toString() })

        assertNull(store.current())
        store.save("jwt-1")
        assertEquals("jwt-1", store.current())
        store.clear()
        assertNull(store.current())
    }
}
