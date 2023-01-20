package app.gym.domain.tag

import org.springframework.stereotype.Service
import javax.validation.ConstraintViolationException

class TagDuplicatedException : RuntimeException()

class TagNotExistsException : RuntimeException()

@Service
class TagService(
    private val tagRepository: TagRepository
) {
    fun addTag(command: AddTagCommand) {
        val tag = Tag(null, command.tag)

        try {
            tagRepository.save(tag)
        } catch (e: ConstraintViolationException) {
            throw TagDuplicatedException()
        }
    }

    fun getTags(): List<Tag> {
        return tagRepository.findAll()
    }

    fun deleteTag(tagId: Long) {
        if(!tagRepository.existsById(tagId))
        {
            throw TagNotExistsException()
        }
        tagRepository.deleteById(tagId)
    }
}

