# Workshop

A set of examples to demonstrate CI/CD pipelines in OpenShift.

## Requirements

### An OpenShift 3.11 Cluster

An OpenShift 3.11 cluster is required, in this workshop MiniShift ([CDK 3.8](https://developers.redhat.com/products/cdk/download)) is used:

    minishift setup-cdk
    
    minishift start --memory=4GB --cpus=2 --disk-size=40GB

    # Expose the cluster registry
    minishift addons apply registry-route

    # Avoid the creation of a Jenkins instance in every project a pipeline is created and started
    minishift openshift config set --patch '{"jenkinsPipelineConfig":{"autoProvisionEnabled":false}}'

### A Sample Application

The Hello Service application is used during the workshop. The source code is in [this](./application) directory. 

It needs to be pushed to a Git repository, so create a Git repository and push it for later use.

## Examples

* [Example 1 - First Steps](./example1)
* [Example 2 - CI Pipeline](./example2)
* [Example 3 - CD Pipeline](./example3)
* [Example 4 - Pipeline Templates](./example4)
* [Example 5 - Pipeline Library](./example5)
* [Example 6 - Custom Jenkins with S2I](./example6)
* [Example 7 - Application Templates and Configuration](./example7)
* [Example 8 - Single-Cluster Revisited](./example8)
* [Example 9 - Image Promotion Approvals](./example9)
* [Example 10 - Multi-Cluster](./example10)