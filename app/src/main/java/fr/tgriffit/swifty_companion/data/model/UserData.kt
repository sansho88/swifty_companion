package fr.tgriffit.swifty_companion.data.model



/**
 *
 */
open class UserData(
    private val id: Int,
    private val email: String,
    private val login: String,
    private val firstName: String,
    private val lastName: String,
    private val usualFullName: String,
    private val usualFirstName: String,
    private val url: String,
    private val phone: String?,
    private val displayName: String,
    private val kind: String,
    private val image: Image,
    private val staff: Boolean,
    private val correctionPoint: Int,
    private val poolMonth: String,
    private val poolYear: String,
    private val location: String?,
    private val wallet: Int,
    private val anonymizeDate: String,
    private val dataErasureDate: String?,
    private val alumni: Boolean,
    private val active: Boolean,
    private val groups: List<Any>,
    private val cursusUsers: List<CursusUser>,
    private val projectsUsers: List<Any>,
    private val languagesUsers: List<LanguagesUser>,
    private val achievements: List<Any>,
    private val titles: List<Any>,
    private val titlesUsers: List<Any>,
    private val partnerships: List<Any>,
    private val patroned: List<Patroned>,
    private val patroning: List<Any>,
    private val expertisesUsers: List<ExpertisesUser>,
    private val roles: List<Any>,
    private val campus: List<Campus>,
    private val campusUsers: List<CampusUser>
) {
    override fun toString(): String {
        return "User(" +
                "id=$id, " +
                "email='$email', " +
                "login='$login', " +
                "firstName='$firstName', " +
                "lastName='$lastName', " +
                "usualFullName='$usualFullName', " +
                "usualFirstName='$usualFirstName', " +
                "url='$url', " +
                "phone=$phone, " +
                "displayName='$displayName', " +
                "kind='$kind', " +
                "image=$image, " +
                "staff=$staff, " +
                "correctionPoint=$correctionPoint, " +
                "poolMonth='$poolMonth', " +
                "poolYear='$poolYear', " +
                "location=$location, " +
                "wallet=$wallet, " +
                "anonymizeDate='$anonymizeDate', " +
                "dataErasureDate=$dataErasureDate, " +
                "alumni=$alumni, " +
                "active=$active, " +
                "groups=$groups, " +
                "cursusUsers=$cursusUsers, " +
                "projectsUsers=$projectsUsers, " +
                "languagesUsers=$languagesUsers, " +
                "achievements=$achievements, " +
                "titles=$titles, " +
                "titlesUsers=$titlesUsers, " +
                "partnerships=$partnerships, " +
                "patroned=$patroned, " +
                "patroning=$patroning, " +
                "expertisesUsers=$expertisesUsers, " +
                "roles=$roles, " +
                "campus=$campus, " +
                "campusUsers=$campusUsers" +
                ")"
    }
}

open class Image(
    val link: String,
    val versions: Versions
)

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
    val cursusId: Int,
    val hasCoalition: Boolean,
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
    val createdAt: String,
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

