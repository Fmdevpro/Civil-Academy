package com.fmdev.civilacademy.data.remote.datasource

import com.fmdev.civilacademy.domain.errors.GoogleSignInError
import com.fmdev.civilacademy.shared.model.DataResult
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.kotlin.any
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
class AuthRemoteDataSourceImplTest {

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var authRemoteDataSource: AuthRemoteDataSourceImpl

    @Before
    fun setup() {
        firebaseAuth = mock()
        authRemoteDataSource = AuthRemoteDataSourceImpl(firebaseAuth)
    }

    // ==================== LOGIN TESTS ====================

    @Test
    fun `login returns Success when FirebaseAuth returns valid user`() = runTest {
        // Given
        val email = "test@example.com"
        val password = "password123"

        val mockAuthResult = mock<AuthResult>()

        val mockTask = mock<Task<AuthResult>> {
            on { isSuccessful } doReturn true
            on { result } doReturn mockAuthResult
        }

        val mockUser = mock<FirebaseUser> { userMock ->
            on { userMock.uid } doReturn "uid123"
            on { userMock.displayName } doReturn "Test User"
            on { userMock.email } doReturn email
            on { userMock.isEmailVerified } doReturn true
        }
        whenever(firebaseAuth.signInWithEmailAndPassword(email, password)).thenReturn(mockTask)
        whenever(firebaseAuth.currentUser).thenReturn(mockUser)

        whenever(mockTask.addOnCompleteListener(any())).thenAnswer { invocation ->
            val listener = invocation.getArgument<OnCompleteListener<AuthResult>>(0)
            listener.onComplete(mockTask)
            mockTask
        }

        // When
        val result = authRemoteDataSource.login(email, password)

        // Then
        assertTrue(result is DataResult.Success)
        val user = (result as DataResult.Success).data
        assertEquals("uid123", user.uid)
        assertEquals("Test User", user.displayName)
        assertEquals(email, user.email)
        assertTrue(user.isEmailVerified)
        assertFalse(user.isGoogleUser)
        verify(firebaseAuth).signInWithEmailAndPassword(email, password)
    }

    @Test
    fun `login returns Success with null displayName`() = runTest {
        // Given
        val email = "test@example.com"
        val password = "password123"

        val mockAuthResult = mock<AuthResult>()

        val mockTask = mock<Task<AuthResult>> {
            on { isSuccessful } doReturn true
            on { result } doReturn mockAuthResult
        }

        val mockUser = mock<FirebaseUser> { userMock ->
            on { userMock.uid } doReturn "uid123"
            on { userMock.displayName } doReturn null
            on { userMock.email } doReturn email
            on { userMock.isEmailVerified } doReturn true
        }
        whenever(firebaseAuth.signInWithEmailAndPassword(email, password)).thenReturn(mockTask)
        whenever(firebaseAuth.currentUser).thenReturn(mockUser)

        whenever(mockTask.addOnCompleteListener(any())).thenAnswer { invocation ->
            val listener = invocation.getArgument<OnCompleteListener<AuthResult>>(0)
            listener.onComplete(mockTask)
            mockTask
        }

        // When
        val result = authRemoteDataSource.login(email, password)

        // Then
        assertTrue(result is DataResult.Success)
        val user = (result as DataResult.Success).data
        assertEquals("uid123", user.uid)
        assertEquals("", user.displayName)
        assertEquals(email, user.email)
        assertTrue(user.isEmailVerified)
        assertFalse(user.isGoogleUser)
        verify(firebaseAuth).signInWithEmailAndPassword(email, password)
    }

    @Test
    fun `login returns Success with default values when currentUser is null`() = runTest {
        // Given
        val email = "test@example.com"
        val password = "password123"

        val mockAuthResult = mock<AuthResult>()

        val mockTask = mock<Task<AuthResult>> {
            on { isSuccessful } doReturn true
            on { result } doReturn mockAuthResult
        }
        whenever(firebaseAuth.signInWithEmailAndPassword(email, password)).thenReturn(mockTask)
        whenever(firebaseAuth.currentUser).thenReturn(null)

        whenever(mockTask.addOnCompleteListener(any())).thenAnswer { invocation ->
            val listener = invocation.getArgument<OnCompleteListener<AuthResult>>(0)
            listener.onComplete(mockTask)
            mockTask
        }

        // When
        val result = authRemoteDataSource.login(email, password)

        // Then
        assertTrue(result is DataResult.Success)
        val user = (result as DataResult.Success).data
        assertEquals("", user.uid)
        assertEquals("", user.displayName)
        assertEquals("", user.email)
        assertFalse(user.isEmailVerified)
        assertFalse(user.isGoogleUser)
    }

    @Test
    fun `login returns Error when FirebaseAuth task is not successful`() = runTest {
        // Given
        val email = "test@example.com"
        val password = "wrongpassword"
        val firebaseException = RuntimeException("Invalid credentials")

        val mockTask = mock<Task<AuthResult>> {
            on { isSuccessful } doReturn false
            on { exception } doReturn firebaseException
        }

        whenever(firebaseAuth.signInWithEmailAndPassword(email, password)).thenReturn(mockTask)

        whenever(mockTask.addOnCompleteListener(any())).thenAnswer { invocation ->
            val listener = invocation.getArgument<OnCompleteListener<AuthResult>>(0)
            listener.onComplete(mockTask)
            mockTask
        }

        // When
        val result = authRemoteDataSource.login(email, password)

        // Then
        assertTrue(result is DataResult.Error)
        verify(firebaseAuth).signInWithEmailAndPassword(email, password)
    }

    // ==================== GOOGLE SIGN IN TESTS ====================

    @Test
    fun `signInWithGoogle returns Success when task is successful`() = runTest {
        // Given
        val idToken = "valid_token"

        val firebaseUser = mock<FirebaseUser> {
            on { uid } doReturn "123"
            on { displayName } doReturn "John Doe"
            on { email } doReturn "john.doe@example.com"
            on { isEmailVerified } doReturn true
        }

        val mockTask = mock<Task<AuthResult>> {
            on { isSuccessful } doReturn true
            on { result } doReturn mock()
        }

        whenever(firebaseAuth.signInWithCredential(any())).thenReturn(mockTask)
        whenever(firebaseAuth.currentUser).thenReturn(firebaseUser)

        whenever(mockTask.addOnCompleteListener(any())).thenAnswer { invocation ->
            val listener = invocation.getArgument<OnCompleteListener<AuthResult>>(0)
            listener.onComplete(mockTask)
            mockTask
        }

        // When
        val result = authRemoteDataSource.signInWithGoogle(idToken)

        // Then
        assertTrue(result is DataResult.Success)
        val user = (result as DataResult.Success).data
        assertEquals("123", user.uid)
        assertEquals("John Doe", user.displayName)
        assertEquals("john.doe@example.com", user.email)
        assertTrue(user.isEmailVerified)
        assertTrue(user.isGoogleUser)
    }

    @Test
    fun `signInWithGoogle returns Success with null user`() = runTest {
        // Given
        val idToken = "valid_token"

        val mockTask = mock<Task<AuthResult>> {
            on { isSuccessful } doReturn true
            on { result } doReturn mock()
        }

        whenever(firebaseAuth.signInWithCredential(any())).thenReturn(mockTask)
        whenever(firebaseAuth.currentUser).thenReturn(null)

        whenever(mockTask.addOnCompleteListener(any())).thenAnswer { invocation ->
            val listener = invocation.getArgument<OnCompleteListener<AuthResult>>(0)
            listener.onComplete(mockTask)
            mockTask
        }

        // When
        val result = authRemoteDataSource.signInWithGoogle(idToken)

        // Then
        assertTrue(result is DataResult.Success)
        val user = (result as DataResult.Success).data
        assertEquals("", user.uid)
        assertEquals("", user.displayName)
        assertEquals("", user.email)
        assertFalse(user.isEmailVerified)
        assertTrue(user.isGoogleUser)
    }

    @Test
    fun `signInWithGoogle returns Error when task is not successful`() = runTest {
        // Given
        val idToken = "valid_token"
        val firebaseException = RuntimeException("Invalid credentials")

        val mockTask = mock<Task<AuthResult>> {
            on { isSuccessful } doReturn false
            on { exception } doReturn firebaseException
        }

        whenever(firebaseAuth.signInWithCredential(any())).thenReturn(mockTask)

        whenever(mockTask.addOnCompleteListener(any())).thenAnswer { invocation ->
            val listener = invocation.getArgument<OnCompleteListener<AuthResult>>(0)
            listener.onComplete(mockTask)
            mockTask
        }

        // When
        val result = authRemoteDataSource.signInWithGoogle(idToken)

        // Then
        assertTrue(result is DataResult.Error)
    }

    @Test
    fun `signInWithGoogle returns Error when idToken is blank`() = runTest {
        // Given
        val idToken = "" // Token en blanco

        // When
        val result = authRemoteDataSource.signInWithGoogle(idToken)

        // Then
        assertTrue(result is DataResult.Error)
        val error = (result as DataResult.Error).dataError
        assertEquals(GoogleSignInError.EmptyIdToken, error)
    }

    // ==================== PASSWORD RESET TESTS ====================

    @Test
    fun `sendPasswordResetEmail returns Success when task succeeds`() = runTest {
        // Given
        val email = "test@example.com"

        val mockTask: Task<Void> = mock<Task<Void>> {
            on { isSuccessful } doReturn true
            on { result } doReturn mock(Void::class.java)
        }

        whenever(firebaseAuth.sendPasswordResetEmail(eq(email))).thenReturn(mockTask)

        whenever(mockTask.addOnCompleteListener(any())).thenAnswer { invocation ->
            val listener = invocation.getArgument<OnCompleteListener<Void>>(0)
            listener.onComplete(mockTask)
            mockTask
        }

        // When
        val result = authRemoteDataSource.sendPasswordResetEmail(email)

        // Then
        assertTrue(result is DataResult.Success)
        verify(firebaseAuth).sendPasswordResetEmail(email)
    }

    @Test
    fun `sendPasswordResetEmail returns Error when task fails`() = runTest {
        // Given
        val email = "test@example.com"

        val mockTask: Task<Void> = mock<Task<Void>> {
            on { isSuccessful } doReturn false
            on { exception } doReturn RuntimeException("Error")
            on { result } doReturn mock(Void::class.java)
        }

        whenever(firebaseAuth.sendPasswordResetEmail(email)).thenReturn(mockTask)

        whenever(mockTask.addOnCompleteListener(any())).thenAnswer { invocation ->
            val listener = invocation.getArgument<OnCompleteListener<Void>>(0)
            listener.onComplete(mockTask)
            mockTask
        }

        // When
        val result = authRemoteDataSource.sendPasswordResetEmail(email)

        // Then
        assertTrue(result is DataResult.Error)
        verify(firebaseAuth).sendPasswordResetEmail(email)
    }

    // ==================== EMAIL VERIFICATION TESTS ====================

    @Test
    fun `sendUserEmailVerification returns Success when task succeeds`() = runTest {
        // Given
        val mockTask: Task<Void> = mock<Task<Void>> {
            on { isSuccessful } doReturn true
            on { result } doReturn mock(Void::class.java)
        }

        val mockUser = mock<FirebaseUser> { userMock ->
            on { userMock.uid } doReturn "uid123"
            on { userMock.displayName } doReturn "Test User"
            on { userMock.email } doReturn "test@example.com"
            on { userMock.isEmailVerified } doReturn true
        }

        whenever(firebaseAuth.currentUser).thenReturn(mockUser)
        whenever(mockUser.sendEmailVerification()).thenReturn(mockTask)
        whenever(mockTask.addOnCompleteListener(any())).thenAnswer { invocation ->
            val listener = invocation.getArgument<OnCompleteListener<Void>>(0)
            listener.onComplete(mockTask)
            mockTask
        }

        // When
        val result = authRemoteDataSource.sendUserEmailVerification()

        // Then
        assertTrue(result is DataResult.Success)
        verify(mockUser).sendEmailVerification()
    }

    @Test
    fun `sendUserEmailVerification returns Error when task fails`() = runTest {
        // Given
        val user: FirebaseUser = mock()
        val mockTask: Task<Void> = mock()
        whenever(firebaseAuth.currentUser).thenReturn(user)
        whenever(user.sendEmailVerification()).thenReturn(mockTask)
        whenever(mockTask.addOnCompleteListener(any())).thenAnswer { invocation ->
            val listener = invocation.getArgument<OnCompleteListener<Void>>(0)
            listener.onComplete(mockTask)
            mockTask
        }
        whenever(mockTask.isSuccessful).thenReturn(false)
        whenever(mockTask.exception).thenReturn(RuntimeException("Error"))

        // When
        val result = authRemoteDataSource.sendUserEmailVerification()

        // Then
        assertTrue(result is DataResult.Error)
        verify(user).sendEmailVerification()
    }

    @Test
    fun `sendUserEmailVerification returns Error if currentUser is null`() = runTest {
        // Given
        whenever(firebaseAuth.currentUser).thenReturn(null)

        // When
        val result = authRemoteDataSource.sendUserEmailVerification()

        // Then
        assertTrue(result is DataResult.Error)
        verify(firebaseAuth).currentUser
    }

    @Test
    fun `isEmailVerified returns Success when email is verified`() = runTest {
        // Given
        val user: FirebaseUser = mock()
        whenever(firebaseAuth.currentUser).thenReturn(user)
        whenever(user.isEmailVerified).thenReturn(true)

        // When
        val result = authRemoteDataSource.isEmailVerified()

        // Then
        assertTrue(result is DataResult.Success)
    }

    @Test
    fun `isEmailVerified returns Error when email is not verified`() = runTest {
        // Given
        val user: FirebaseUser = mock()
        whenever(firebaseAuth.currentUser).thenReturn(user)
        whenever(user.isEmailVerified).thenReturn(false)

        // When
        val result = authRemoteDataSource.isEmailVerified()

        // Then
        assertTrue(result is DataResult.Error)
    }

    @Test
    fun `isEmailVerified returns Error when currentUser is null`() = runTest {
        // Given
        whenever(firebaseAuth.currentUser).thenReturn(null)

        // When
        val result = authRemoteDataSource.isEmailVerified()

        // Then
        assertTrue(result is DataResult.Error)
    }

    // ==================== SIGN OUT TESTS ====================

    @Test
    fun `signOut calls auth signOut`() {
        // Given (Setup)

        // When
        authRemoteDataSource.signOut()

        // Then
        verify(firebaseAuth).signOut()
    }
}