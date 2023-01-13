package app.gym.domain.gym


import app.gym.domain.GymTag
import app.gym.domain.franchise.Franchise
import app.gym.domain.image.Image
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime
import java.util.*
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

    @Column(name = "name")
    var name: String = ""; private set

    @JoinColumn(name = "franchise_id")
    @ManyToOne
    var franchise: Franchise? = null; private set

    @Column(name = "address")
    var address: String = ""; private set

    @Column(name = "description")
    @Lob
    var description: String = ""; private set

    @Column(name = "images")
    @OneToMany(mappedBy = "gym")
    var images: MutableList<Image> = mutableListOf(); private set

    @Column(name = "latitude")
    var latitude: Double = 0.0; private set

    @Column(name = "longitude")
    var longitude: Double = 0.0; private set

    @Column(name = "gym_tag")
    @OneToMany(mappedBy = "gym")
    var gymTag: List<GymTag> = listOf()

    @Column(name = "created_at")
    @CreatedDate
    var createdAt: LocalDateTime = LocalDateTime.MIN; private set

    @Column(name = "modified_at")
    @LastModifiedDate
    var modifiedAt: LocalDateTime = LocalDateTime.MIN; private set

    fun update(
        name: String,
        franchise: Franchise?,
        address: String,
        description: String,
        images: List<Image>,
        latitude: Double,
        longitude: Double
    ) {
        this.name = name
        this.franchise = franchise
        this.address = address
        this.description = description
        this.images = images.toMutableList()
        this.latitude = latitude
        this.longitude = longitude
    }
}