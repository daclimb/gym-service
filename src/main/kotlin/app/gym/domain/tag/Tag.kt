package app.gym.domain.tag

import app.gym.domain.GymTag
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import javax.persistence.*

@Entity
@Table(name = "tags")
@EntityListeners(AuditingEntityListener::class)
class Tag(
    id: Long? = null,
    @Column(name = "tag")
    val tagString: String
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = id; private set

    @Column(name = "gym_tag")
    @OneToMany(mappedBy = "tag")
    var gymTag: List<GymTag> = listOf()
}