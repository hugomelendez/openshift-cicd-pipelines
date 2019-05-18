# Example 4 -Pipeline Template

In this example we will see that a pipeline can be created with a Template.

## Steps

First we need to create the template, for that we will export the previously created pipeline:

    oc get bc hello-service -o yaml -n hello-dev > cd-pipeline.yaml

With that we need to create a Template using the next YAML:

    apiVersion: v1
    kind: Template
    metadata:
      name: cd-pipeline
    objects:
    - apiVersion: v1
      kind: BuildConfig
      ...
    parameters:
    - description: Application Name
      displayName: Application Name
      name: APP_NAME
      required: true
    - description: Git Repository
      displayName: Git Repository
      name: GIT_REPO
      required: true
    - description: Git Branch
      displayName: Git Branch
      name: GIT_BRANCH
      value: master
      required: true

 The template needs to be created before its used:

    oc create -f cd-pipeline.yaml -n openshift

Then the pipeline can be created with the following command:

    oc new-app --template=cd-pipeline -p APP_NAME=hello-service -p GIT_REPO= -p GIT_BRANCH=masters --name=hello-service-pipeline -n hello-dev