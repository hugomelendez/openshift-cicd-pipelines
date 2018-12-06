# openshift-pipelines

CI/CD pipelines in OpenShift.

## Convention Over Configuration

The applications using this pipelines must follow the next convention:

* Two Git repositories are needed: 

  * The application repository
  * The configuration files repository

* The application repository must have a file called **template.yaml** representing the entire application (DeploymentConfig, BuildConfig, etc.) inside a directory called **openshift**

```
  └── openshift
      └── template.yaml
  └── src
      ├── main...
      └── test...
```

* The configuration repository must have the next structure:

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

### Pipeline Library

The pipelines use a shared library for common functionality, the code is in the following repository:

* [openshift-pipeline-library](https://github.com/redhatcsargentina/openshift-pipeline-library.git)

### Example repositories

* [openshift-hello-world](https://github.com/redhatcsargentina/openshift-hello-world.git)
* [openshift-hello-world-config](https://github.com/redhatcsargentina/openshift-hello-world-config.git)
