package app.gym.domain.tag

import app.gym.domain.gymTag.GymTag
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime
import javax.persistence.*

@Entity
@Table(name = "tags")
@EntityListeners(AuditingEntityListener::class)
class Tag(
    id: Long? = null,
    @Column(name = "tag", unique = true)
    val tagString: String
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = id; private set

    @Column(name = "gym_tag")
    @OneToMany(mappedBy = "tag")
    var gymTag: List<GymTag> = emptyList(); private set

    @Column(name = "created_at")
    @CreatedDate
    var createdAt: LocalDateTime = LocalDateTime.MIN; private set

    @Column(name = "modified_at")
    @LastModifiedDate
    var modifiedAt: LocalDateTime = LocalDateTime.MIN; private set
}