package app.gym.domain.gymTag

import app.gym.domain.gym.Gym
import app.gym.domain.tag.Tag
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime
import javax.persistence.*

@Entity
@Table(name = "gym_tags")
@EntityListeners(AuditingEntityListener::class)
class GymTag(
    tag: Tag
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null; private set

    @ManyToOne
    @JoinColumn(name = "gym_id")
    var gym: Gym? = null

    @ManyToOne
    @JoinColumn(name = "tag_id")
    var tag: Tag = tag; private set

    @Column(name = "created_at")
    @CreatedDate
    var createdAt: LocalDateTime = LocalDateTime.MIN; private set

    @Column(name = "modified_at")
    @LastModifiedDate
    var modifiedAt: LocalDateTime = LocalDateTime.MIN; private set
}
