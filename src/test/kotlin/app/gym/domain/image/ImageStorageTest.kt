package app.gym.domain.image

import app.gym.config.AWSTestConfig
import com.amazonaws.services.s3.AmazonS3
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.assertDoesNotThrow
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.core.io.ClassPathResource
import org.springframework.core.io.FileSystemResource
import java.util.*
import kotlin.io.path.toPath

@SpringBootTest(
    classes = [AWSTestConfig::class]
)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ImageStorageTest {

    @Autowired
    lateinit var imageStorage: ImageStorage

    @Autowired
    lateinit var s3client: AmazonS3

    @Value("\${cloud.aws.bucket}")
    lateinit var bucket: String

    @BeforeAll
    fun beforeAll() {
        this.s3client.createBucket(bucket)
    }

    @Test
    fun `Should not throw any exception when delete images`() {
        val imageResource = ClassPathResource("images/pooh.png")
        val file = FileSystemResource(imageResource.uri.toPath())

        val ids = mutableListOf<String>()
        for (i in 0 until 3) {
            ids.add(imageStorage.save(file.inputStream).toString())
        }

        assertDoesNotThrow {
            imageStorage.deleteWithUuids(ids)
        }
    }
}
