package org.foodmonks.backend.dynamodb;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class TokenResetDAO {
    private final DynamoDBMapper dynamoDBMapper;

    @Autowired
    public TokenResetDAO(DynamoDBMapper dynamoDBMapper) {
        this.dynamoDBMapper = dynamoDBMapper;
    }

    public TokenReset getToken(String email) {
        try{
            return dynamoDBMapper.load(TokenReset.class, email);
        } catch (Exception e) {
            return null;
        }

    }

    public void setToken(TokenReset tokenReset){
        dynamoDBMapper.save(tokenReset);
    }

    public boolean comprobarResetToken(TokenReset tokenReset) {
        String awsToken = getToken(tokenReset.getEmail()).getToken();
        System.out.println("Token de AWS: " + awsToken);
        System.out.println("Token del front: " + tokenReset.getToken());
        return (awsToken.equals(tokenReset.getToken()));
    }
}