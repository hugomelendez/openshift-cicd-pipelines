# openshift-pipelines

CI/CD pipelines in OpenShift.

## Requirements

* Minishift (CDK 3.5 or higher)
* Command line interface tool (**oc**) that matches with OpenShift clusters version

## Usage

In the **setup.sh** script are the required steps to deploy the demo. 

The script requires some environment variables to be present in the context where the script runs:

| Environment Variable             | Description            |
| -------------------------------- | ----------------------------------------------------------- |
| REPOSITORY_CREDENTIALS_USERNAME  | Username to authenticate to the different Git repositories  |
| REPOSITORY_CREDENTIALS_PASSWORD  | Password to authenticate to the different Git repositories  |

Run the script:

    sh setup.sh

## Convention Over Configuration

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

### Notes

* Add more environment directories if needed.
* The development configuration is located in the template designed by the developers.

### Pipeline Library

The pipelines use a shared library for common functionality, the code is in the following repository:

* [openshift-pipeline-library](https://github.com/redhatcsargentina/openshift-pipeline-library.git)

### Example Repositories

* [openshift-hello-world](https://github.com/redhatcsargentina/openshift-hello-world.git)
* [openshift-hello-world-config](https://github.com/redhatcsargentina/openshift-hello-world-config.git)
