# openshift-pipelines

CI/CD pipelines in OpenShift.

## Convention Over Configuration

The applications using this pipelines must follow the next convention:

* Two Git repositories are needed: 

  * the application repo
  * and the configuration files repo

* The application repository must have a file called **template.yaml** representing the entire application (DeploymentConfig, BuildConfig, etc.) inside a directory called **openshift**
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

### Example repositories

* [openshift-hello-world](https://github.com/redhatcsargentina/openshift-hello-world.git)
* [openshift-hello-world-config](https://github.com/redhatcsargentina/openshift-hello-world-config.git)
