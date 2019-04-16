# OpenShift CI/CD Pipelines

Basic demonstration of OpenShift CI/CD pipelines for deploying applications across environments using advanced deployment strategies like Blue/Green.

## Pipeline

![Pipeline](demo/images/pipeline.png)

## Pipeline library

The pipelines use a shared library for common functionality, the code is in the following repository:

* [openshift-pipeline-library](https://github.com/redhatcsargentina/openshift-pipeline-library.git)

## Demo

### Create the environments (projects)

These are the environments where the applications will be promoted by the pipeline.

    oc new-project dev
    oc new-project test
    oc new-project prod
    
### Create a Jenkins instance

A Jenkins instances will be created in the development project.

    oc new-app --template=jenkins-ephemeral --name=jenkins -n dev

Then a set of permissions need to be granted.

    oc adm policy add-role-to-user edit system:serviceaccount:dev:jenkins -n test
    oc adm policy add-role-to-user edit system:serviceaccount:dev:jenkins -n prod

### Create the application (and the pipeline)

    oc new-app -f src/main/openshift/template.yaml -n dev -p APP_NAME=openshift-hello-world -p GIT_REPO=https://github.com/redhatcsargentina/openshift-pipelines.git -p GIT_BRANCH=master

### Create and assign the pull secrets

All the repositories used are private so pull secrets are needed.

The pipeline library uses a secret named **pipeline-library-repository-credentials** and the pipeline (used both in the BuildConfig and the Checkout step) uses a secret named **app-repository-credentials**.

These secrets needs to be label with **credential.sync.jenkins.openshift.io=true** to be synchronized in Jenkins as a credential (thanks to the [OpenShift Jenkins Sync Plugin](https://github.com/openshift/jenkins-sync-plugin)). 

The commands to create and label the secrets are:

    oc create secret generic app-repository-credentials --from-literal=username=leandroberetta --from-literal=password=********* --type=kubernetes.io/basic-auth -n dev
    oc create secret generic pipeline-library-repository-credentials --from-literal=username=leandroberetta --from-literal=password=********* --type=kubernetes.io/basic-auth -n dev

    oc label secret app-repository-credentials credential.sync.jenkins.openshift.io=true -n dev
    oc label secret pipeline-library-repository-credentials credential.sync.jenkins.openshift.io=true -n dev

### Start the pipeline

The pipeline is a BuildConfig so it can be started as follows:

    oc start-build openshift-hello-world-pipeline -n dev


