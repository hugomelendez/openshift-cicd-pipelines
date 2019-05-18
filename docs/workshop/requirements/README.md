# Requirements

## OpenShift Cluster

An OpenShift cluster is required, in this workshop MiniShift (CDK 3.8) is used:

    minishift setup-cdk
    
    minishift start --memory=4GB --cpus=2

## Sample Application

The Hello Service application is used during the workshop. The source code is in [this](./application) directory. 

It needs to be pushed to a Git repository, so create a Git repository and push it for later use.

