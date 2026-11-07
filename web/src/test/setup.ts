import { afterEach } from 'vitest'
import { enableAutoUnmount } from '@vue/test-utils'

// Unmount every app between tests so queries from one test can't hit the next test's mocks.
enableAutoUnmount(afterEach)
