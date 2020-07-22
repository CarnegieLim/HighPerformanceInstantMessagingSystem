package db;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProviderChain;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException;

//@Service
public class DynamoDBService {

    //    @Value("${amazon.aws.accesskey}")
    private String amazonAWSAccessKey = "";

    //    @Value("${amazon.aws.secretkey}")
    private String amazonAWSSecretKey = "";

    private DynamoDbEnhancedClient client;

    private DynamoDbTable<Message> messageTable;

    //    @PostConstruct
    public DynamoDBService() {
        client = dynamoDbEnhancedClient();
        messageTable = client.table("msg_sync", TableSchema.fromBean(Message.class));
    }

    public void save(Message m) throws DynamoDbException {
        messageTable.putItem(m);
    }

    private AwsCredentialsProvider amazonAWSCredentialsProvider() {
        return AwsCredentialsProviderChain.builder().addCredentialsProvider(this::amazonAWSCredentials).build();
    }

    private AwsCredentials amazonAWSCredentials() {
        return AwsBasicCredentials.create(amazonAWSAccessKey, amazonAWSSecretKey);
    }


    public DynamoDbClient dynamoDbClient() {
        return DynamoDbClient.builder()
                .credentialsProvider(amazonAWSCredentialsProvider())
                .region(Region.US_EAST_1)
                .build();
    }

    //    @Bean
    public DynamoDbEnhancedClient dynamoDbEnhancedClient() {
        return DynamoDbEnhancedClient.builder()
                .dynamoDbClient(dynamoDbClient())
                .build();
    }

    //    @Bean
    public DynamoDbTable<Message> getMessageTable(DynamoDbEnhancedClient client) {
        return client.table("msg_sync", TableSchema.fromBean(Message.class));
    }

//    public
}
