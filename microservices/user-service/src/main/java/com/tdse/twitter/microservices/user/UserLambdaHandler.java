package com.tdse.twitter.microservices.user;

import com.amazonaws.serverless.exceptions.ContainerException;
import com.amazonaws.serverless.proxy.model.AwsProxyRequest;
import com.amazonaws.serverless.proxy.model.AwsProxyResponse;
import com.amazonaws.serverless.proxy.spring.SpringBootLambdaHandler;
import com.amazonaws.serverless.proxy.spring.SpringBootProxyHandler;

public class UserLambdaHandler {
    private static SpringBootLambdaHandler<AwsProxyRequest, AwsProxyResponse> handler;

    public static SpringBootLambdaHandler<AwsProxyRequest, AwsProxyResponse> getHandler() throws ContainerException {
        if (handler == null) {
            handler = new SpringBootProxyHandler(UserServiceApplication.class);
        }
        return handler;
    }
}
