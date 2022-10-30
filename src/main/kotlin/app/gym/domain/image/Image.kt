package app.gym.domain.image

import app.gym.domain.gym.Gym
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.domain.Persistable
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime
import java.util.*
import javax.persistence.*

@Entity
@Table(name = "gym_images")
@EntityListeners(AuditingEntityListener::class)
class Image: Persistable<UUID> {
    @Id
    @Column(name = "id", columnDefinition = "uuid")
    var uuid: UUID? = null; private set

    @Column(name = "name")
    // TODO: @NotNull
    // 확인 필요
    var name: String = ""; private set

    @JoinColumn(name = "gym_id")
    @ManyToOne
    var gym: Gym? = null; private set

    @Column(name = "created_at")
    @CreatedDate
    var createdAt: LocalDateTime? = null; private set

    companion object {
        fun create(
            id: UUID?,
            name: String
        ): Image {
            val image = Image()
            image.uuid = id
            image.name = name
            return image
        }
    }

    override fun getId(): UUID? {
        return this.uuid
    }

    override fun isNew(): Boolean {
        return this.createdAt == null
    }
}