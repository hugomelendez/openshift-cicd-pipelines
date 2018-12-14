# OpenShift Pipelines Demo

## Requirements

* MiniShift (CDK 3.5+)
* OpenShift CLI

## Usage

In the **setup.sh** script are the required steps to deploy the demo. 

The script requires some environment variables to be present in the context where the script runs:

| Environment Variable             | Description                                                              |
| -------------------------------- | ------------------------------------------------------------------------ |
| REPOSITORY_CREDENTIALS_USERNAME  | Username to authenticate to different Git repositories used in this demo |
| REPOSITORY_CREDENTIALS_PASSWORD  | Password to authenticate to different Git repositories used in this demo |

Run the script:

    sh setup.sh

After the completion of the script a non-prod and a prod cluster will be ready to use with all templates created and configuration applied to start using the pipelines.

## Example Repositories

* [openshift-hello-world](https://github.com/redhatcsargentina/openshift-hello-world.git)
* [openshift-hello-world-config](https://github.com/redhatcsargentina/openshift-hello-world-config.git)
