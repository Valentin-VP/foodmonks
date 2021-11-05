#!/bin/bash
docker build -t foodmonks-dev-backend .
docker push 711621010839.dkr.ecr.us-east-2.amazonaws.com/foodmonks-dev-backend
docker tag foodmonks-dev-backend:latest 711621010839.dkr.ecr.us-east-2.amazonaws.com/foodmonks-dev-backend:$1
docker push 711621010839.dkr.ecr.us-east-2.amazonaws.com/foodmonks-dev-backend:$1
