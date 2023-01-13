package app.gym.domain

import app.gym.domain.gym.Gym
import app.gym.domain.tag.Tag
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import javax.persistence.*

@Entity
@Table(name = "gym_tags")
@EntityListeners(AuditingEntityListener::class)
class GymTag(
    id: Long? = null,
    @ManyToOne
    @JoinColumn(name = "gym_id")
    val gym: Gym,
    @ManyToOne
    @JoinColumn(name = "tag_id")
    val tag: Tag,
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = id; private set
}
