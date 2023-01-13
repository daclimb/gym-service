package app.gym.config

import com.amazonaws.client.builder.AwsClientBuilder
import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.AmazonS3ClientBuilder
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.DependsOn
import org.testcontainers.containers.localstack.LocalStackContainer
import org.testcontainers.containers.localstack.LocalStackContainer.Service.S3
import org.testcontainers.utility.DockerImageName

@TestConfiguration
class AWSTestConfig {

    @Value("\${cloud.aws.image}")
    private lateinit var localStackImageName: String

    @Value("\${cloud.aws.region}")
    private lateinit var region: String

    @Bean
    @DependsOn("s3Container")
    fun amazonS3(localStackContainer: LocalStackContainer): AmazonS3 {
        return AmazonS3ClientBuilder.standard()
            .withEndpointConfiguration(
                AwsClientBuilder.EndpointConfiguration(
                    s3Container().getEndpointOverride(S3).toString(),
                    s3Container().region
                )
            )
            .withCredentials(localStackContainer.defaultCredentialsProvider)
            .build()
    }

    @Bean(initMethod = "start", destroyMethod = "stop")
    fun s3Container(): LocalStackContainer {
        val localstackImage = DockerImageName.parse(localStackImageName)
        return LocalStackContainer(localstackImage).withServices(S3)
    }
}