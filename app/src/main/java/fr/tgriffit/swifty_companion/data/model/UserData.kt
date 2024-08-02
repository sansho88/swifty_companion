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
    private val projects_users: List<ProjectsUsers>,
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

    fun getProjectsUsers(): List<ProjectsUsers> {
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
        val link: String?,
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
        val skills: List<Skill>,
        val cursus_id: Int,
        val has_coalition: Boolean,
        val user: UserX,
        val cursus: Cursus


    ) {
        override fun toString(): String {
            return "CursusUser(id=$id, beginAt='$beginAt', endAt=$endAt, grade=$grade, level=$level, skills=$skills, cursus_id=$cursus_id, has_coalition=$has_coalition, user=$user, cursus=$cursus)"
        }
    }

    open class Skill (
        val id: Double,
        val name: String,
        val level: Double
    ){
        override fun toString(): String {
            return "Skill(id=$id, name='$name', level=$level)"
        }
    }

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


    ) {
        override fun toString(): String {
            return "Cursus(id=$id, created_at='$created_at', name='$name', slug='$slug')"
        }
    }

    open class LanguagesUser(
        val id: Int,
        val languageId: Int,
        val userId: Int,
        val position: Int,
        val createdAt: String
    ){
        override fun toString(): String {
            return "LanguagesUser(id=$id, languageId=$languageId, userId=$userId, position=$position, createdAt='$createdAt')"
        }
    }

    open class Patroned(
        val id: Int,
        val userId: Int,
        val godfatherId: Int,
        val ongoing: Boolean,
        val createdAt: String,
        val updatedAt: String
    ){
        override fun toString(): String {
            return "Patroned(id=$id, userId=$userId, godfatherId=$godfatherId, ongoing=$ongoing, createdAt='$createdAt', updatedAt='$updatedAt')"
        }
    }

    open class ExpertisesUser(
        val id: Int,
        val expertiseId: Int,
        val interested: Boolean,
        val value: Int,
        val contactMe: Boolean,
        val createdAt: String,
        val userId: Int
    ){
        override fun toString(): String {
            return "ExpertisesUser(id=$id, expertiseId=$expertiseId, interested=$interested, value=$value, contactMe=$contactMe, createdAt='$createdAt', userId=$userId)"
        }
    }

    open class Campus(
        val id: Int,
        val name: String,
        val timeZone: String,
        val language: Language,
        val usersCount: Int,
        val vogsphereId: Int
    ){
        override fun toString(): String {
            return "Campus(id=$id, name='$name', timeZone='$timeZone', language=$language, usersCount=$usersCount, vogsphereId=$vogsphereId)"
        }
    }

    open class Language(
        val id: Int,
        val name: String,
        val identifier: String,
        val createdAt: String,
        val updatedAt: String
    ){
        override fun toString(): String {
            return "Language(id=$id, name='$name', identifier='$identifier', createdAt='$createdAt', updatedAt='$updatedAt')"
        }
    }

    open class CampusUser(
        val id: Int,
        val user_id: Int,
        val campus_id: Int,
        val is_primary: Boolean
    ){
        override fun toString(): String {
            return "CampusUser(id=$id, userId=$user_id, campusId=$campus_id, isPrimary=$is_primary)"
        }
    }

    class ProjectsUsers (
        val id: Int,
        val occurrence: Int,
        val final_mark: Int? =null,
        val status: String,
        val validated: Boolean? = null,
        val current_team_id: Int? = null,
        val project: Project,
        val cursus_ids: List<Int>,
        val marked_at: String? = null,
        val marked: Boolean,
        val retriable_at: String? = null,
        val created_at: String,
        val updated_at: String
    ){
        override fun toString(): String {
            return "ProjectsUsers(id=$id, occurrence=$occurrence, final_mark=$final_mark, status='$status', validated=$validated, current_team_id=$current_team_id, project=$project, cursus_ids=$cursus_ids, marked_at=$marked_at, marked=$marked, retriable_at=$retriable_at, created_at='$created_at', updated_at='$updated_at')"
        }
    }

    class Project (
        val id: Double,
        val name: String,
        val slug: String,
        val parent_id: Double? = null
    ){
        override fun toString(): String {
            return "Project(id=$id, name='$name', slug='$slug', parent_id=$parent_id)"
        }
    }

}

