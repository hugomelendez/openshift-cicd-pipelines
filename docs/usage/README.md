# Usage

This repository contains the Pipeline Library and the Jenkins S2I customization.

## Application Repository

This implementation is designed to contain the majority of the CI/CD logic but some information is required in the applications repositories. 

An **openshift** directory is proposed as a convention (but can be configured too) to host the application pipelines configuration, the template, the integration tests and the configuration.

### Example (Hello Service Application)

The application used in the demo contains the following set of configuration files:

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

#### Pipelines

There are two kinds of pipelines in this implementation:

* CI Pipeline
* CD Pipeline


##### Example

###### CI

This pipeline integrates code to the DEV environment.

    runCIPipeline(application: env.APP_NAME,
                  agent: "maven",
                  compileCommands: "mvn package -DskipTests",
                  testCommands: "mvn test",
                  artifactsDir: "./target")

###### CD

This pipeline does the same than the CI pipeline and continoues deploying the application in TEST and PROD. A difference is that this pipeline clones the code from the master branch.

    runCIPipeline(application: env.APP_NAME,
                  agent: "maven",
                  compileCommands: "mvn package -DskipTests",
                  testCommands: "mvn test",
                  artifactsDir: "./target")

#### Template

The file **template.yaml** has the definition of the application, it is used to create/update the application in the environments to guarantee the correct promotion of all the resources avoiding differences between them.

This template might have ConfigMaps, Routes and PersistentVolumeClaims among others apart from the core resources like DeploymentConfig, ImageStream, BuildConfig and Service. 

An important thing are the parameters. The template must have a mandatory parameters named **APP_NAME**, which is very useful to name the application, thanks to that parameter the template can be instantiated multiple times in the same project (having the same application deployed several times with a different name each time). Extra parameters can be added too.

### Environment Specific Configuration

The **template.yaml** must be used in every environment as it main goal is to promote all the objects required by the application. This is helpful to promote the infrastructure of the application but the configuration needs to be environment specific and for that reason the **environments** directory exists with a set of convenient files:

#### Patching the DeploymentConfig

This operation is done based on the **deploymentPatch.yaml** file:

    spec:
      replicas: 2
      template:
        spec:
        containers:
        - name: hello-service
          resources:
          limits:
            cpu: 1
          memory: 512Mi
            requests:
            cpu: 1
            memory: 512Mi

With this approach it is possible to modify for example:

* the application replicas
* the application resources (in PROD higher resources are needed compare to DEV and TEST, same case for the replicas)

#### Replacing Configuration Resources

This operation is done based on the **replaceConfig.yaml** file:

    apiVersion: v1
    kind: Template
    metadata:
      name: hello-config-template
    objects:
    parameters:
    - description: Application Name
      displayName: Application Name
      name: APP_NAME
      required: true
      value: hello-service

Notice that this file is a template similar to the application template, all the objects in this template replace the ones that exist in the **template.yaml**, this is very useful to replace entire ConfigMaps for example.

#### Setting the Template Parameters

The template.yaml has parameters that can be set with the **templateParameters.txt** file:

    HELLO_STRING=The productive Hello World from OpenShift!
    ENVIRONMENT=prod

Any extra parameter can be added here (and also need to be declare in the template.yaml of course).
