# OpenShift Pipelines

CI/CD pipelines in OpenShift.

## Demo

### Requirements

* MiniShift (CDK 3.5+)
* OpenShift CLI

### Usage

In the **setup.sh** script are the required steps to deploy the demo. 

The script requires some environment variables to be present in the context where the script runs:

| Environment Variable             | Description                                                              |
| -------------------------------- | ------------------------------------------------------------------------ |
| REPOSITORY_CREDENTIALS_USERNAME  | Username to authenticate to different Git repositories used in this demo |
| REPOSITORY_CREDENTIALS_PASSWORD  | Password to authenticate to different Git repositories used in this demo |

Run the script:

    sh setup.sh

After the completion of the script a non-prod and a prod cluster will be ready to use with all templates created and configuration applied to start using the pipelines.

### Convention Over Configuration

The applications using this pipelines must follow the next convention:

* Two Git repositories are needed: 

  * The application repository
  * The application configuration repository

* The application repository must have a file called **template.yaml** representing the entire application (DeploymentConfig, BuildConfig, etc.) inside a directory called **openshift**.

```
  └── openshift
      └── template.yaml
  └── src
      ├── main...
      └── test...
```

* The application configuration repository must have the next structure:

```
  └── environments
      ├── prod
      │   ├── config.yaml
      │   └── deployment.yaml
      └── test
          ├── config.yaml
          └── deployment.yaml
```

#### Notes

* Add more environment directories if needed.
* The development configuration is located in the template designed by the developers.

#### Pipeline Library

The pipelines use a shared library for common functionality, the code is in the following repository:

* [openshift-pipeline-library](https://github.com/redhatcsargentina/openshift-pipeline-library.git)

#### Example Repositories

* [openshift-hello-world](https://github.com/redhatcsargentina/openshift-hello-world.git)
* [openshift-hello-world-config](https://github.com/redhatcsargentina/openshift-hello-world-config.git)
