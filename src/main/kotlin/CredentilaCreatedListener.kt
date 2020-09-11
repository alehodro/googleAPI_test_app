import com.google.api.client.auth.oauth2.AuthorizationCodeFlow
import com.google.api.client.auth.oauth2.Credential
import com.google.api.client.auth.oauth2.StoredCredential
import com.google.api.client.auth.oauth2.TokenResponse
import com.google.api.client.util.store.AbstractDataStore
import com.google.api.client.util.store.AbstractDataStoreFactory
import com.google.api.client.util.store.DataStore
import com.google.api.client.util.store.DataStoreFactory
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction

import java.io.IOException
import java.io.Serializable

/*class CredentilaCreatedListenerImpl() : AuthorizationCodeFlow.CredentialCreatedListener {
    override fun onCredentialCreated(credential: Credential?, tokenResponse: TokenResponse) {
        println(credential)
        println(tokenResponse)
        val a = StoredCredential()
        a.accessToken = tokenResponse.accessToken
        a.refreshToken = tokenResponse.refreshToken
        a.expirationTimeMilliseconds = tokenResponse.expiresInSeconds * 1000

    }
}*/

data class Token(val accessToken: String, val refreshToken: String, val expiresTime: Long)

class GoogleRepository {


  //  val tokens = mutableMapOf<String, StoredCredential>()

    fun get(key: String): StoredCredential? {
        val token = transaction {
            GoogleCredentisls
                .select { GoogleCredentisls.key eq key }
                .map {
                    Token(
                        accessToken = it[GoogleCredentisls.accessToken],
                        refreshToken = it[GoogleCredentisls.refreshToken],
                        expiresTime = it[GoogleCredentisls.expirationTime]
                    )
                }
                .firstOrNull()
            //return tokens[key]
        }
        return token?.let {
            val creds = StoredCredential()
            creds.accessToken = it.accessToken
            creds.refreshToken = it.refreshToken
            creds.expirationTimeMilliseconds = it.expiresTime
            creds
        }
    }

    fun set(key: String, value: StoredCredential) {
        transaction {
            GoogleCredentisls
                .insert {
                    it[GoogleCredentisls.key] = key
                    it[accessToken] = value.accessToken
                    it[refreshToken] = value.refreshToken
                    it[expirationTime] = value.expirationTimeMilliseconds
                }
        }
        //    tokens[key] = value
    }

    fun deleteAll() {
        return transaction {
            GoogleCredentisls.deleteAll()
        }
        //      tokens.clear()
    }

    fun delete(key: String) {
        //     tokens.remove(key)
        transaction {
            GoogleCredentisls.deleteWhere { GoogleCredentisls.key eq key }

        }
    }

    fun getAll(): List<StoredCredential> {
        return transaction {
            GoogleCredentisls.selectAll()
                .map {
                    Token(
                        accessToken = it[GoogleCredentisls.accessToken],
                        refreshToken = it[GoogleCredentisls.refreshToken],
                        expiresTime = it[GoogleCredentisls.expirationTime]
                    )
                }
                .map {
                    StoredCredential()
                        .apply {
                            accessToken = it.accessToken
                            refreshToken = it.refreshToken
                            expirationTimeMilliseconds = it.expiresTime
                        }
                }
        }
        //tokens
    }


    fun findByAccessToken(accessToken: String): StoredCredential? {
        return transaction {
            GoogleCredentisls
                .select { GoogleCredentisls.accessToken eq accessToken }
                .map {
                    Token(
                        accessToken = it[GoogleCredentisls.accessToken],
                        refreshToken = it[GoogleCredentisls.refreshToken],
                        expiresTime = it[GoogleCredentisls.expirationTime]
                    )
                }
                .map {
                    StoredCredential()
                        .apply {
                            this.accessToken = it.accessToken
                            refreshToken = it.refreshToken
                            expirationTimeMilliseconds = it.expiresTime
                        }

                }
                .firstOrNull()
        }
        //     return tokens.map { it.value }.find { it.accessToken == accessToken }
    }

    fun findAllKeys(): List<String> = transaction {
        GoogleCredentisls.slice(GoogleCredentisls.key)
            .selectAll()
            .map { it[GoogleCredentisls.key] }
    }
    //tokens.keys
}


class GoogleDataStoreFactory(private val googleRepository: GoogleRepository) : AbstractDataStoreFactory() {
    override fun <V : Serializable?> createDataStore(id: String): DataStore<V> {
        return GoogleTestDataStore(id, googleRepository, this) as DataStore<V>
    }
/*  override fun <V : Serializable?> getDataStore(id: String): DataStore<V> {
       return GoogleTestDataStore(id, googleRepository, this)
    }*/

}

class GoogleTestDataStore(
    private val id: String,
    private val googleRepository: GoogleRepository,
    private val googleDataStoreFactory: DataStoreFactory
) : DataStore<StoredCredential> {
    override fun getDataStoreFactory(): DataStoreFactory {
        return googleDataStoreFactory
    }

    override fun clear(): DataStore<StoredCredential> {
        googleRepository.deleteAll()
        return this
    }

    override fun isEmpty(): Boolean {
        return size() == 0
    }

    override fun containsValue(value: StoredCredential): Boolean {
        return googleRepository.findByAccessToken(value.accessToken) != null
    }

    override fun getId(): String {
        return id
    }

    override fun set(key: String, value: StoredCredential): DataStore<StoredCredential> {
        googleRepository.get(key) ?: googleRepository.set(key, value)
        return this
    }

    override fun values(): MutableCollection<StoredCredential> {
        return googleRepository.getAll().toMutableList()
    }

    override fun size(): Int {
        return googleRepository.getAll().size
    }

    override fun keySet(): MutableSet<String> {
        return googleRepository.findAllKeys().toMutableSet()
    }

    override fun containsKey(key: String): Boolean {
        return googleRepository.get(key) != null
    }

    override fun get(key: String): StoredCredential? {
        return googleRepository.get(key)
    }

    override fun delete(key: String): DataStore<StoredCredential> {
        googleRepository.delete(key)
        return this
    }
}



interface Animal {
    fun move()
}

class Cat() : Animal {
    override fun move() {
        println("тыгыдык")
    }
}

class Fish() : Animal {
    override fun move() {
        println("Swim")
    }
}

