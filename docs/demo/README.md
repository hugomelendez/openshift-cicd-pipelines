# Demo

The demonstration uses the [Hello Service](https://github.com/redhatcsargentina/openshift-hello-service) application.

## Create the Projects

These projects (environments) are used to deploy the application:

    oc new-project hello-dev
    oc new-project hello-test
    oc new-project hello-prod

This project is used to deploy the Jenkins instance:

    oc new-project jenkins

## Create the Secrets (Optional)
    
If the repository used is private a pull Secret is needed in **hello-dev** (to pull the pipelines and the application source code) and **jenkins** (to pull the pipeline library).

The Secrets need to be label with **credential.sync.jenkins.openshift.io=true** to be synchronized in Jenkins as Credentials thanks to the [OpenShift Jenkins Sync Plugin](https://github.com/openshift/jenkins-sync-plugin). 

In **hello-dev** an annotation is used to automatically assign the Secret to any BuildConfig that matches the Git URI used.

The commands to create and label the Secrets are:

    oc create secret generic repository-credentials --from-file=ssh-privatekey=$HOME/.ssh/id_rsa --type=kubernetes.io/ssh-auth -n hello-dev
    oc label secret repository-credentials credential.sync.jenkins.openshift.io=true -n hello-dev
    oc annotate secret repository-credentials 'build.openshift.io/source-secret-match-uri-1=ssh://github.com/*' -n hello-dev

    oc create secret generic repository-credentials --from-file=ssh-privatekey=$HOME/.ssh/id_rsa --type=kubernetes.io/ssh-auth -n jenkins
    oc label secret repository-credentials credential.sync.jenkins.openshift.io=true -n jenkins

## Create a Preconfigured Jenkins Instance with S2I

A custom preconfigured Jenkins instance is created with the following commands:

    oc new-build jenkins:2 --binary --name custom-jenkins -n jenkins
    oc start-build custom-jenkins --from-dir=./jenkins --wait -n jenkins
    oc new-app --template=jenkins-ephemeral --name=jenkins -p JENKINS_IMAGE_STREAM_TAG=custom-jenkins:lahello-test -p NAMESPACE=jenkins -n jenkins

Finally a set of permissions need to be granted:

    oc adm policy add-role-to-user edit system:serviceaccount:jenkins:jenkins -n hello-dev
    oc adm policy add-role-to-user edit system:serviceaccount:jenkins:jenkins -n hello-test
    oc adm policy add-role-to-user edit system:serviceaccount:jenkins:jenkins -n hello-prod

## Create the Pipelines

### Creating the BuildConfigs Directly

A pipeline is a BuildConfig of type **JenkinsPipeline** so for creation the **new-build** command is used:

### CI 

    oc new-build ssh://git@github.com/redhatcsargentina/openshift-hello-service.git --name=hello-service-ci-pipeline --context-dir=./openshift/pipelines/ci --strategy=pipeline -e APP_NAME=hello-service-ci -n hello-dev
    
### CD

    oc new-build ssh://git@github.com/redhatcsargentina/openshift-hello-service.git --name=hello-service-pipeline --context-dir=./openshift/pipelines/cd --strategy=pipeline -e APP_NAME=hello-service -n hello-dev

After the execution of this commands the pipelines are automatically started.

### Using Templates

Another creation method is using Templates:

### CI 

    oc create -f ./templates/ci-pipeline.yaml -n hello-dev
    oc new-app --template ci-pipeline -p APP_NAME=hello-service-ci -p GIT_REPO=ssh://git@github.com/redhatcsargentina/openshift-hello-service.git -p GIT_BRANCH=master -n hello-dev

### CD

    oc create -f ./templates/cd-pipeline.yaml -n hello-dev
    oc new-app --template cd-pipeline -p APP_NAME=hello-service -p GIT_REPO=ssh://git@github.com/redhatcsargentina/openshift-hello-service.git -n hello-dev
