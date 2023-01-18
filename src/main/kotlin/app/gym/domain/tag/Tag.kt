package app.gym.domain.tag

import app.gym.domain.gymTag.GymTag
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import javax.persistence.*

@Entity
@Table(name = "tags")
@EntityListeners(AuditingEntityListener::class)
class Tag(
    @Column(name = "tag")
    val tagString: String
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null; private set

    @Column(name = "gym_tag")
    @OneToMany(mappedBy = "tag")
    var gymTag: List<GymTag> = emptyList(); private set
}