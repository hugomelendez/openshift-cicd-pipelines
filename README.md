# OpenShift Pipelines

CI/CD pipelines in OpenShift.

## Pipelines

* Development
  * CI pipeline from develop
  * Release pipeline to generate a version that can be promoted to the next environment
  * Feature based pipeline (for specific branches like features or bugs)
* Testing
  * Image deployment pipeline
  * Configuration change pipeline
* Production
  * Image deployment pipeline
  * Configuration change pipeline
    
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

## Pipeline Library

The pipelines use a shared library for common functionality, the code is in the following repository:

* [openshift-pipeline-library](https://github.com/redhatcsargentina/openshift-pipeline-library.git)
