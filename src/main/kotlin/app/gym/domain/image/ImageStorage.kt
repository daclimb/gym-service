package app.gym.domain.image

import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.model.DeleteObjectsRequest
import com.amazonaws.services.s3.model.DeleteObjectsRequest.*
import com.amazonaws.services.s3.model.ObjectMetadata
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.io.InputStream
import java.util.*

interface ImageStorage {
    fun save(data: InputStream): UUID
    fun deleteWithUuids(uuids: List<String>)
}

@Component
class S3ImageStorage(
    private val amazonS3: AmazonS3,
    @Value("\${cloud.aws.bucket}")
    private val bucket: String
): ImageStorage {

    override fun save(data: InputStream): UUID {
        val uuid = UUID.randomUUID()
        val metadata = ObjectMetadata()
        amazonS3.putObject(this.bucket, uuid.toString(), data, metadata)
        return uuid
    }

    override fun deleteWithUuids(uuids:List<String>) {
        if(uuids.isEmpty())
            return

        val keys = uuids.map {
            KeyVersion(it)
        }
        val request = DeleteObjectsRequest(bucket)
        request.keys = keys
        amazonS3.deleteObjects(request)
    }

}