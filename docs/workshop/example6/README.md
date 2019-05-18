# Example 6 - Custom Jenkins with S2I

The S2I process can be apply to Jenkins too for configuring a custom image automatically.

## Steps

For now this technique is used to preconfigured the pipeline library. For more related features see the [documentation](https://docs.openshift.com/container-platform/3.11/using_images/other_images/jenkins.html#jenkins-as-s2i-builder).

Delete the existing Jenkins instance:

    oc delete all -l app=jenkins-ephemeral -n hello-dev
    oc delete sa jenkins -n hello-dev
    oc delete rolebinding jenkins_edit -n hello-dev

Create a builder for the custom Jenkins image:

oc new-build jenkins:2 --binary --name custom-jenkins -n hello-dev

Start a new binary build injecting the jenkins directory into the S2I builder:

oc start-build custom-jenkins --from-dir=./jenkins --wait -n hello-dev

Finally deploy the Jenkins instance:

oc new-app --template=jenkins-ephemeral --name=jenkins -p JENKINS_IMAGE_STREAM_TAG=custom-jenkins:latest -p NAMESPACE=hello-dev -n hello-dev

For now on the pipeline library is preconfigured and ready to use with no manual steps.

