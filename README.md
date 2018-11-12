# openshift-pipelines

CI/CD pipelines in OpenShift.

## Convention Over Configuration

The applications using this pipelines must follow the next convention:

* Two Git repositories are needed, the application and the other for configuration files
* The application repository must have a file called **template.yaml** representing the entire application (DeploymentConfig, BuildConfig, etc.) inside a directory called **openshift**
* The configuration repository must have the next structure:

    └── environments
        ├── prod
        │   ├── config.yaml
        │   └── deployment.yaml
        └── test
            ├── config.yaml
            └── deployment.yaml

Note 1: Add more environment directories if needed.
Note 2: The development configuration is located in the template designed by the developers.
