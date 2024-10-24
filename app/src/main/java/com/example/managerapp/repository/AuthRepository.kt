package com.example.managerapp.repository

//import com.example.managerapp.db.UserDatabase
import com.example.managerapp.models.User
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AuthRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val firebaseAuth: FirebaseAuth,
//    private val db: UserDatabase
) {


    fun signUp(email:String,password:String): Task<AuthResult>{
        return firebaseAuth.createUserWithEmailAndPassword(email,password)
    }

    fun logIn(email: String,password: String): Task<AuthResult>{
        return firebaseAuth.signInWithEmailAndPassword(email,password)
    }

    fun forgotPassword(email:String): Task<Void>{
        return firebaseAuth.sendPasswordResetEmail(email)
    }

    fun logout(){
        firebaseAuth.signOut()
    }

    fun getCurrentUser():FirebaseUser?{
        return firebaseAuth.currentUser
    }

    fun addUserData(user: User):Task<Void>{
        return firestore.collection("users").document(user.uid).set(user)
    }

//    suspend fun storeUserLocally(user: User) = db.getUserDao().insert(user)
//
//    suspend fun deleteUserLocally(user: User) = db.getUserDao().deleteUser(user)
//
//    fun getLocalUser() = db.getUserDao().getUser()


//    using suspend functions
//    suspend fun signUp(email:String,password:String): AuthResult{
//        return firebaseAuth.createUserWithEmailAndPassword(email,password).await()
//    }
//
//    suspend fun logIn(email: String,password: String): AuthResult{
//        return firebaseAuth.signInWithEmailAndPassword(email,password).await()
//    }


}