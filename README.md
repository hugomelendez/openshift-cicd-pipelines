# openshift-pipelines

This repository contains common pipelines to use in OpenShift.

## Dev

### Pipelines

The development pipeline has several environment variables to be configured:

| Environment Variable         | Description
| ---------------------------- | -------------------------------------------------------------------------- |
| GIT_REPO                     | The Git repository to use                                                  |
| GIT_BRANCH                   | The Git branch to use                                                      |
| TAG                          | The tag to use when tagging (if tag is configured, default tag is latest)  |
| MANUAL_TAG                   | Specify whether to ask for the tag value with an input step or not         |
| APP_NAME                     | The application name (used in several OpenShift API objects)               |
| IMAGE_NAME                   | The application image name (if not specified APP_NAME is used instead)     |
| APP_TEMPLATE                 | The application template to create/apply OpenShift API objects             |
| COMPILE_COMMAND              | The command to compile the application                                     |    
| TEST_COMMAND                 | The command to test the application                                        |
| ANALIZE_COMMAND              | The command to analize the application code                                |
| RELEASE_COMMAND              | The command to release artifacts                                           |
| ARTIFACTS_DIR                | The directory where binaries exists                                        |

### Templates

#### Application pipelines (DEV)

This template creates two pipelines (based on parameters):

* **<APP_NAME>-develop-pipeline** (Deploys to DEV from the development branch, usually develop)
* **<APP_NAME>-release-pipeline** (Desploys to DEV from a release branch)

#### Application pipeline (BRANCH)

This template create a pipeline to deploy to DEV from an specific branch (a feature branch for example)


## Test

### Pipelines

TBD

### Templates

TBD

## Prod

### Pipelines

TBD

### Templates

TBD
