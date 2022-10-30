package app.gym.domain.gym


import app.gym.domain.image.Image
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime
import javax.persistence.*

@SequenceGenerator(
    name = "GYMS_SEQ_GEN",
    sequenceName = "GYMS_SEQ",
    initialValue = 1,
    allocationSize = 1
)

@Entity
@Table(name = "gyms")
@EntityListeners(AuditingEntityListener::class)
class Gym(id: Long? = null) {
    @Id
    @GeneratedValue(
        strategy = GenerationType.SEQUENCE,
        generator = "GYMS_SEQ_GEN"
    )
    var id: Long? = id; private set

    @Column(name = "title")
    var title: String = ""

    @Column(name = "price")
    var price: Int = 0

    @Column(name = "description")
    @Lob
    var description: String = ""

    @Column(name = "images")
    @OneToMany(mappedBy = "gym")
    var images: MutableList<Image> = mutableListOf()

    @Column(name = "created_at")
    @CreatedDate
    var createdAt: LocalDateTime = LocalDateTime.MIN
}