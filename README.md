# OpenShift CI/CD Pipelines

Basic demonstration of OpenShift CI/CD pipelines for deploying applications across environments using advanced deployment strategies like Blue/Green.

## Pipelines

The pipelines use the declarative approach and the [OpenShift Jenkins Pipeline Plugin](https://github.com/openshift/jenkins-client-plugin).

### CI

![CI](./docs/demo/images/pipeline-ci.png)

### CD

![CD](./docs/demo/images/pipeline-cd.png)

## Documentantion

* [Introduction](./docs/introduction)
* [Usage](./docs/usage)
* [Extras](./docs/extras)
    * [Approvals](./docs/extras/approvals)
    * [Cross-Cluster Image Promotions](./docs/extras/cross-cluster)
* [Demo](./docs/demo)

## Pipeline Library

The pipelines use a shared library for common functionality, the library is embedded in this repository but can be externalized in other Git repository as well.

## Note

This repository serves as an incubator for new features, to apply this pipelines in real scenarios the application and the pipeline library must be splitted.

