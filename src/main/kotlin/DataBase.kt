import com.zaxxer.hikari.HikariDataSource

object DatabaseModule {
    fun createDBConection(): HikariDataSource {
        val datasource = HikariDataSource()
        datasource.jdbcUrl = "jdbc:postgresql://localhost:5434/postgres"
        datasource.username = "postgres"
        datasource.driverClassName = "org.postgresql.Driver"
        datasource.password = "postgres"
        return datasource
    }
}