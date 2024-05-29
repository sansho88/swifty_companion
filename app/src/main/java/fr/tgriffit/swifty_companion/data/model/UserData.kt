@file:Suppress("PrivatePropertyName")

package fr.tgriffit.swifty_companion.data.model


/**
 *
 */
open class UserData(
    val id: Int,
    private val email: String,
    private val login: String,
    private val first_name: String,
    private val last_name: String,
    private val usual_full_name: String,
    private val usual_first_name: String,
    private val url: String,
    private val phone: String?,
    private val displayname: String,
    private val kind: String,
    internal val image: Image,
    private val staff: Boolean,
    private val correction_point: Int,
    private val pool_month: String,
    private val pool_year: String,
    internal val location: String?,
    private val wallet: Int,
    private val anonymize_date: String,
    private val data_erasure_date: String?,
    private val alumni: Boolean,
    private val active: Boolean,
    private val groups: List<Any>,
    val cursus_users: List<CursusUser>,
    private val projects_users: List<Any>,
    private val languages_users: List<LanguagesUser>,
    private val achievements: List<Any>,
    private val titles: List<Any>,
    private val titles_users: List<Any>,
    private val partnerships: List<Any>,
    private val patroned: List<Patroned>,
    private val patroning: List<Any>,
    private val expertises_users: List<ExpertisesUser>,
    private val roles: List<Any>,
    private val campus: List<Campus>,
    private val campus_users: List<CampusUser>
) {
    override fun toString(): String {
        return "User(" +
                "id=$id, " +
                "email='$email', " +
                "login='$login', " +
                "firstName='$first_name', " +
                "lastName='$last_name', " +
                "usualFullName='$usual_full_name', " +
                "usualFirstName='$usual_first_name', " +
                "url='$url', " +
                "phone=$phone, " +
                "displayName='$displayname', " +
                "kind='$kind', " +
                "image=$image, " +
                "staff=$staff, " +
                "correctionPoint=$correction_point, " +
                "poolMonth='$pool_month', " +
                "poolYear='$pool_year', " +
                "location=$location, " +
                "wallet=$wallet, " +
                "anonymizeDate='$anonymize_date', " +
                "dataErasureDate=$data_erasure_date, " +
                "alumni=$alumni, " +
                "active=$active, " +
                "groups=$groups, " +
                "cursusUsers=$cursus_users, " +
                "projectsUsers=$projects_users, " +
                "languagesUsers=$languages_users, " +
                "achievements=$achievements, " +
                "titles=$titles, " +
                "titlesUsers=$titles_users, " +
                "partnerships=$partnerships, " +
                "patroned=$patroned, " +
                "patroning=$patroning, " +
                "expertisesUsers=$expertises_users, " +
                "roles=$roles, " +
                "campus=$campus, " +
                "campusUsers=$campus_users" +
                ")"
    }

    fun getLogin(): String {
        return login
    }

    fun getFullName(): String {
        return "$first_name $last_name"
    }

    fun getFirstName(): String {
        return first_name
    }

    fun getLastName(): String {
        return last_name
    }

    fun getUsualFullName(): String {
        return usual_full_name
    }

    fun getUsualFirstName(): String {
        return usual_first_name
    }

    fun getDisplayName(): String {
        return displayname
    }

    fun getKind(): String {
        return kind
    }

    fun getUrl(): String {
        return url
    }

    fun getPhone(): String? {
        return phone
    }

    fun getCampusUsers(): List<CampusUser> {
        return campus_users
    }

    fun getCampus(): List<Campus> {
        return campus
    }

    fun getExpertisesUsers(): List<ExpertisesUser> {
        return expertises_users
    }

    fun getPatroned(): List<Patroned> {
        return patroned
    }

    fun getPatroning(): List<Any> {
        return patroning
    }

    fun getProjectsUsers(): List<Any> {
        return projects_users
    }

    fun getTitlesUsers(): List<Any> {
        return titles_users
    }

    fun getTitles(): List<Any> {
        return titles
    }

    fun getPartnerships(): List<Any> {
        return partnerships
    }

    fun getLanguagesUsers(): List<LanguagesUser> {
        return languages_users
    }

    fun getCursusUsers(): List<CursusUser> {
        return cursus_users
    }

    fun getGroups(): List<Any> {
        return groups
    }

    fun getAlumni(): Boolean {
        return alumni
    }

    fun getActive(): Boolean {
        return active
    }

    fun getStaff(): Boolean {
        return staff
    }

    fun getPoolMonth(): String {
        return pool_month
    }

    fun getPoolYear(): String {
        return pool_year
    }

    fun getCorrectionPoint(): Int {
        return correction_point
    }

    fun getWallet(): Int {
        return wallet
    }

    fun getAnonymizeDate(): String {
        return anonymize_date
    }

    fun getDataErasureDate(): String? {
        return data_erasure_date
    }


    public class Image(
        val link: String,
        val versions: Versions
    ) {
    }

    open class Versions(
        val large: String,
        val medium: String,
        val small: String,
        val micro: String
    )

    open class CursusUser(
        val id: Int,
        val beginAt: String,
        val endAt: String?,
        val grade: String?,
        val level: Double,
        val skills: List<Any>,
        val cursus_id: Int,
        val has_coalition: Boolean,
        val user: UserX,
        val cursus: Cursus
    )

    open class UserX(
        val id: Int,
        val login: String,
        val url: String
    )

    open class Cursus(
        val id: Int,
        val created_at: String,
        val name: String,
        val slug: String
    )

    open class LanguagesUser(
        val id: Int,
        val languageId: Int,
        val userId: Int,
        val position: Int,
        val createdAt: String
    )

    open class Patroned(
        val id: Int,
        val userId: Int,
        val godfatherId: Int,
        val ongoing: Boolean,
        val createdAt: String,
        val updatedAt: String
    )

    open class ExpertisesUser(
        val id: Int,
        val expertiseId: Int,
        val interested: Boolean,
        val value: Int,
        val contactMe: Boolean,
        val createdAt: String,
        val userId: Int
    )

    open class Campus(
        val id: Int,
        val name: String,
        val timeZone: String,
        val language: Language,
        val usersCount: Int,
        val vogsphereId: Int
    )

    open class Language(
        val id: Int,
        val name: String,
        val identifier: String,
        val createdAt: String,
        val updatedAt: String
    )

    open class CampusUser(
        val id: Int,
        val userId: Int,
        val campusId: Int,
        val isPrimary: Boolean
    )

}

