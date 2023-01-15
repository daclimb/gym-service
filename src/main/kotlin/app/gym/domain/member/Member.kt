package app.gym.domain.member

import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime
import javax.persistence.*

@Entity
@EntityListeners(AuditingEntityListener::class)
@Table(name = "members")
class Member(
    email: String,
    password: String,
    name: String
) {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null

    @Column(name = "email")
    var email: String = email; private set

    @Column(name = "password")
    var password: String = password; private set

    @Column(name = "name")
    var name: String = name; private set

    @Column(name = "description")
    var description: String? = null; private set

    @Column(name = "role")
    var role: UserRole = UserRole.Member; private set

    @Transient // TODO
    @Column(name = "logo_url")
    var logoUrl: String? = null; private set

    @Column(name = "created_at")
    @CreatedDate
    var createdAt: LocalDateTime = LocalDateTime.MIN; private set

    @Column(name = "modified_at")
    @LastModifiedDate
    var modifiedAt: LocalDateTime = LocalDateTime.MIN; private set
}
