# OpenShift Hello Service

Simple Java application that can be deployed into OpenShift. 

It also contains configuration to be deployed by CI/CD pipelines based on the [OpenShift CI/CD Pipelines](https://github.com/redhatcsargentina/openshift-cicd-pipelines) repository.

## Deploy into OpenShift

    oc new-project hello-dev

    oc create secret generic repository-credentials --from-file=ssh-privatekey=$HOME/.ssh/id_rsa --type=kubernetes.io/ssh-auth -n hello-dev
    oc label secret repository-credentials credential.sync.jenkins.openshift.io=true -n hello-dev
    oc annotate secret repository-credentials 'build.openshift.io/source-secret-match-uri-1=ssh://github.com/*' -n hello-dev

    oc create -f ./openshift/template.yaml

    oc new-app --template=hello-service -n hello-dev

## Deploy with CI/CD pipelines

Follow [this](https://github.com/redhatcsargentina/openshift-cicd-pipelines/tree/master/docs/demo) instructions to deploy and promote the application with pipelines.