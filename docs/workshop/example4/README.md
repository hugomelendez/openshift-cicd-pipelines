# Example 4 - Pipeline Template

In this example a template is created to create a pipeline.

## Steps

To create the template, the previously created pipeline is exported in YAML format:

    oc get bc hello-service -o yaml -n hello-dev > cd-pipeline.yaml

The next snippet is an empty Template that must be completed with the result of the export:

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

The final result is in [this](./cd-pipeline.yaml) file.

The template needs to be created in OpenShift before it is used:

    oc create -f cd-pipeline.yaml -n openshift

Then the pipeline can be created with the following command:

    oc new-app --template=cd-pipeline -p APP_NAME=hello-service -p GIT_REPO= -p GIT_BRANCH=masters --name=hello-service-pipeline -n hello-dev