# Example 2 - CI Pipeline

In this example we start using an integration pipeline with a simple Jenkins deployment.

## Steps

Create a Jenkins instance in the hello-dev project:

    oc new-app --template=jenkins-ephemeral --name=jenkins -n hello-dev

Create the pipeline (a pipeline is a BuildConfig of type JenkinsPipeline):

    oc new-build ssh://git@github.com/leandroberetta/openshift-hello-service.git --strategy pipeline --name hello-service-pipeline -n hello-dev

After the execution of the command the pipeline will start and will also fail because the repository is private and credentials are needed for pulling.

There is a convenient way to automate this situations with the use of an annotation:

    oc annotate secret repository-credentials 'build.openshift.io/source-secret-match-uri-1=ssh://github.com/*' -n hello-dev

When a new BuildConfig is created a Secret is automatically assigned to it if the repo uri matches with the annotation value. More info in the [documentation](https://docs.openshift.com/online/dev_guide/builds/build_inputs.html#automatic-addition-of-a-source-secret-to-a-build-configuration).

To test this feature delete the pipeline and create it again:

    oc delete bc hello-service -n hello-dev

    oc new-build ssh://git@github.com/leandroberetta/openshift-hello-service.git --strategy pipeline --name hello-service-pipeline -e APP_NAME=hello-service -n hello-dev

The pipeline will end without errors.

