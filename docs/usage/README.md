# Usage

As can be seen this repository contains the pipelines, the pipeline library and the application. For this repository it is acceptable but in a real scenario this repository need to be splitted in:

* Pipelines repository
* Applications repositories

## Repositories

### Pipelines Repository

This repository must contain:

* vars
* jenkins

### Pipeline Library

The **vars** directory contains the implementation of the pipeline library that is used. It contains common steps to interact with OpenShift.

### Jenkins S2I

The **jenkins** directory contains the configuration of Jenkins to be used in the S2I build image process for its customization.

### Applications Repositories

The applications use the pipelines and this implementation is designed to contain the majority of the CI/CD logic but some information is required in the applications repositories. 

An **openshift** directory is proposed as a convention (can be configured too) to host the application pipelines configuration, the template, the integration tests and the configuration.

#### Example

```
    └── openshift
        ├── environments
        │   ├── dev
        |   |   ├── deploymentPatch.yaml
        |   |   ├── replaceConfig.yaml
        |   |   └── templateParametesr.txt 
        │   ├── test
        |   |   ├── integration-test
        |   |   |   ├── int-test.groovy
        |   |   |   └── int-test.yaml
        |   |   ├── deploymentPatch.yaml
        |   |   ├── replaceConfig.yaml
        |   |   └── templateParametesr.txt 
        │   ├── prod
        |   |   ├── deploymentPatch.yaml
        |   |   ├── replaceConfig.yaml
        |   |   └── templateParametesr.txt
        ├── pipelines
        |   ├── ci
        |   |   └── Jenkinsfile
        |   ├── cd
        |   |   └── Jenkinsfile
        └── template.yaml
```      