package xyz.siddharthseth.crostata.util

import okhttp3.Interceptor
import okhttp3.Response

class TokenInterceptor : Interceptor {


    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        /* googleApiClient.blockingConnect()

         val p = Auth.GoogleSignInApi.silentSignIn(googleApiClient)
         val account = p.await().signInAccount
         if (account != null) {
             account.idToken?.let {
                 val newRequest = originalRequest.newBuilder()
                         .header("idToken", it)
                         .build()
                 googleApiClient.disconnect()
                 return chain.proceed(newRequest)
             }
         }
         googleApiClient.disconnect()*/
        return chain.proceed(originalRequest)
    }
}