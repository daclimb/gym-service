package app.gym.config

import com.amazonaws.auth.AWSStaticCredentialsProvider
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.client.builder.AwsClientBuilder
import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.AmazonS3ClientBuilder
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile

@Configuration
class AWSConfig {

    @Bean
    @Profile("local")
    fun amazonS3Local(
        @Value("\${cloud.aws.endpoint}")
        endpoint: String,
        @Value("\${cloud.aws.region}")
        region: String
    ): AmazonS3 {
        val creds = BasicAWSCredentials("", "")
        return AmazonS3ClientBuilder.standard()
            .withEndpointConfiguration(AwsClientBuilder.EndpointConfiguration(endpoint, region))
            .withCredentials(AWSStaticCredentialsProvider(creds))
            .build()
    }

    @Bean
    @Profile("dev")
    fun amazonS3Dev(): AmazonS3 {
        return AmazonS3ClientBuilder.standard()
            .build()
    }

    @Bean
    fun amazonS3(): AmazonS3 {
        return AmazonS3ClientBuilder.standard()
            .build()
    }
}