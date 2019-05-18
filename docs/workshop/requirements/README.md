# Requirements

## An OpenShift 3.9+ Cluster

An OpenShift 3.9+ cluster is required, in this workshop MiniShift ([CDK 3.8](https://developers.redhat.com/products/cdk/download)) is used:

    minishift setup-cdk
    
    minishift start --memory=4GB --cpus=2 --disk-size=40GB

## A Sample Application

The Hello Service application is used during the workshop. The source code is in [this](./app) directory. 

It needs to be pushed to a Git repository, so create a Git repository and push it for later use.

