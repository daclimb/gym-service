package app.gym.domain.franchise

import app.gym.domain.gym.Gym
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime
import javax.persistence.*

@Entity
@Table(name = "franchises")
@EntityListeners(AuditingEntityListener::class)
class Franchise(id: Long? = null) {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = id; private set

    @Column(name = "name")
    var name: String = ""; private set

    @Column(name = "description")
    var description: String = ""; private set

    @Column(name = "gyms")
    @OneToMany(mappedBy = "franchise")
    var gyms: MutableList<Gym> = mutableListOf(); private set

    @Column(name = "created_at")
    @CreatedDate
    @JsonSerialize(using = LocalDateTimeSerializer::class)
    var createdAt: LocalDateTime = LocalDateTime.MIN; private set

    @Column(name = "modified_at")
    @LastModifiedDate
    @JsonSerialize(using = LocalDateTimeSerializer::class)
    var modifiedAt: LocalDateTime = LocalDateTime.MIN; private set

    fun update(
        name: String,
        description: String
    ) {
        this.name = name
        this.description = description
    }
}